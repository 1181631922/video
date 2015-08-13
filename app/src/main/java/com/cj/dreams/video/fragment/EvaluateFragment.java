package com.cj.dreams.video.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.LoginActivity;
import com.cj.dreams.video.adapter.EvaluateAdapter;
import com.cj.dreams.video.bean.EvaluateUserBean;
import com.cj.dreams.video.bean.UserInfoBean;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;
import com.cj.dreams.video.dbhelper.RecordTableCourse;
import com.cj.dreams.video.dbhelper.UserTableCourse;
import com.cj.dreams.video.dboperate.RecordOperate;
import com.cj.dreams.video.dboperate.UserOperate;
import com.cj.dreams.video.layout.PullToRefreshLayout;
import com.cj.dreams.video.thirdpartylogin.UserInfo;
import com.cj.dreams.video.util.EncryptUtil;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.PostUtil;
import com.cj.dreams.video.util.SP;
import com.cj.dreams.video.util.SingleImageTaskUtil;
import com.cj.dreams.video.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link EvaluateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EvaluateFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "videoid";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String videoid;
    private String MaxId, MinId, UserInput;
    private List<EvaluateUserBean> evaluateUserBeanList = new ArrayList<EvaluateUserBean>();
    private List<EvaluateUserBean> evaluateUserBeanList_loadmore = new ArrayList<EvaluateUserBean>();
    private List<UserInfoBean> userInfoBeanList = new ArrayList<UserInfoBean>();
    private List<UserInfoBean> userInfoBeanList_loadmore = new ArrayList<UserInfoBean>();
    private EvaluateAdapter evaluateAdapter;
    private PullToRefreshLayout ptrl;
    private ListView listView;
    private Handler handler1, handler2;
    private EditText evaluate_input, evaluate_send_content;
    private String userInfoId, userInfoName, userInfoImage;
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
    private UserTableCourse userTableCourse;
    private UserOperate userOperate;
    private ImageView evaluate_icon;
    private Button evalaute_send_btn;
    private LinearLayout evaluate_input_line, evaluate_input_layout;
//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EvaluateFragment.
     */
//    public static EvaluateFragment newInstance(String param1, String param2) {
//        EvaluateFragment fragment = new EvaluateFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
    public EvaluateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            正常的id
            videoid = getArguments().getString(ARG_PARAM1);
//            L.d(videoid.toString());
//            测试用的id
//            videoid="2331";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_evaluate, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(getActivity());
        laughSQLiteOpenHelper.getWritableDatabase();
        initView();
        Thread loadThread = new Thread(new LoadThread());
        loadThread.start();
        initData();
    }


    class PostThread implements Runnable {
        private String id;
        private String content;

        PostThread(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public void run() {
            postUserData(id, content);
        }
    }

    private void postUserData(String videoid, String usercontent) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("videoid", videoid);
        L.d((String) SP.get(getActivity(), SP.USER_DATA_USERID, ""));
        map.put("userid", (String) SP.get(getActivity(), SP.USER_DATA_USERID, ""));
        map.put("content", usercontent);
        try {
            String backMsg = PostUtil.postData(BaseUrl + PostVideoComment, map);
            L.d(backMsg.toString());
            if (backMsg.toString().trim().equals("1")) {
                Message message = Message.obtain();
                message.what = 1;
                posthandler.sendMessage(message);
            } else {
                Message message = Message.obtain();
                message.what = 0;
                posthandler.sendMessage(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler posthandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    /**
                     * 不登陆不允许评论
                     */
                    T.showShort(getActivity(), "发送失败！");
                    break;
                case 1:
                    T.showShort(getActivity(), "发送成功！");

                    break;
            }
        }
    };

    class LoadThread implements Runnable {
        @Override
        public void run() {
            loadData("update_comment", "0");
        }
    }

    private void initView() {
        evaluate_input_layout = (LinearLayout) getActivity().findViewById(R.id.evaluate_input_layout);
        evaluate_input_line = (LinearLayout) getActivity().findViewById(R.id.evaluate_input_line);
        evaluate_input_line.setOnClickListener(this);
        evaluate_send_content = (EditText) getActivity().findViewById(R.id.evaluate_send_content);
        evalaute_send_btn = (Button) getActivity().findViewById(R.id.evalaute_send_btn);
        evalaute_send_btn.setOnClickListener(this);
        evaluate_icon = (ImageView) getActivity().findViewById(R.id.evaluate_icon);
        SingleImageTaskUtil imageTask = new SingleImageTaskUtil(evaluate_icon);
        imageTask.execute((String) SP.get(getActivity(), SP.USER_DATA_USERICONURL, ""));
        ptrl = ((PullToRefreshLayout) getActivity().findViewById(R.id.refresh_evaluate_view));
        ptrl.setOnRefreshListener(new MyListener());
        listView = (ListView) getActivity().findViewById(R.id.evaluate_listview);

        evaluateAdapter = new EvaluateAdapter(getActivity(), evaluateUserBeanList);
        listView.setAdapter(evaluateAdapter);
        listView.setFocusable(true);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.evaluate_input_line:
                evaluate_input_layout.setVisibility(View.VISIBLE);
                evaluate_send_content.setFocusableInTouchMode(true);
                evaluate_send_content.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) evaluate_send_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(evaluate_send_content, 0);
                break;
            case R.id.evalaute_send_btn:
                if ((boolean) SP.get(getActivity(), SP.USER_DATA_LOGINED, false)) {
                    UserInput = evaluate_send_content.getText().toString().trim();
                    L.d("获取用户输入的数据", UserInput);
                    L.d(videoid, EncryptUtil.encryptBASE64(UserInput));
                    evaluate_send_content.getEditableText().clear();
                    if (UserInput != null && !UserInput.equals("")) {
                        Thread postThread = new Thread(new PostThread(videoid, EncryptUtil.encryptBASE64(UserInput)));
                        postThread.start();
                    }
                    evaluate_input_layout.setVisibility(View.GONE);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private class MyListener implements PullToRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Thread refresh = new Thread(new Refresh());
                    refresh.start();
                    handler2 = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what) {
                                case 0:
                                    evaluateAdapter.update();
                                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                    break;
                            }
                        }
                    };
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessageDelayed(0, 500);
        }

        @Override
        public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Thread lodeMore = new Thread(new LoadMore());
                    lodeMore.start();

                    handler1 = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what) {
                                case 0:
                                    if (!msg.obj.equals("0") && !msg.equals(MaxId)) {
                                        evaluateUserBeanList.addAll(evaluateUserBeanList_loadmore);
                                        evaluateAdapter.update();
                                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                                    }
                                    break;
                            }
                        }
                    };
                }
            }.sendEmptyMessageDelayed(0, 500);
        }

    }

    private void initData() {

    }

    private String loadData(String updatetype, String id) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("videoid", videoid);
        map.put("updatetype", updatetype);
        map.put("commentid", id);
        map.put("devicetype", "iphone");
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetUserEvaluate, map);
            L.d("得到的评论的页面的返回数据", backMsg.toString());
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                JSONArray commentinfo = jsonObject.getJSONArray("commentinfo");
                L.d(commentinfo.toString());
                evaluateUserBeanList_loadmore.clear();
                userInfoBeanList.clear();
                for (int i = 0; i < commentinfo.length(); i++) {
                    EvaluateUserBean evaluateUserBean = new EvaluateUserBean(null, null, null, null);
                    JSONObject periodicalinfo = commentinfo.getJSONObject(i);
                    if (id.equals("0") && i == 0) {
                        MaxId = periodicalinfo.getString("comment_id");
                        L.d("最初的最大的comment_id", MaxId);
                        MinId = periodicalinfo.getString("comment_id");
                        L.d("最初的最小的comment_id", MinId);
                    }
                    if (Integer.parseInt(MinId) >= Integer.parseInt(periodicalinfo.getString("comment_id"))) {
                        MinId = periodicalinfo.getString("comment_id");
                        L.d("最小的id", MinId);
                    }
                    if (Integer.parseInt(MaxId) <= Integer.parseInt(periodicalinfo.getString("comment_id"))) {
                        MaxId = periodicalinfo.getString("comment_id");
                        L.d("最大的id", MaxId);
                    }
                    L.d("最小的comment_id", MinId);
                    evaluateUserBean.setEvaluateUser(periodicalinfo.getString("user_id"));
                    evaluateUserBean.setEvaluateTime(periodicalinfo.getString("comment_date"));
                    //base64解密
                    evaluateUserBean.setEvaluateDetail(EncryptUtil.decryptBASE64(periodicalinfo.getString("comment_content")));
                    evaluateUserBean.setEvaluateGoodTimes(periodicalinfo.getString("comment_praise_number"));
                    if (id.equals("0")) {
                        evaluateUserBeanList.add(evaluateUserBean);
                    } else {
                        evaluateUserBeanList_loadmore.add(evaluateUserBean);
                    }
                }
                JSONArray userinfo = jsonObject.getJSONArray("userinfo");
                for (int i = 0; i < userinfo.length(); i++) {
                    JSONObject userInfoObject = userinfo.getJSONObject(i);
                    UserInfoBean userInfoBean = new UserInfoBean(null, null, null);
                    userInfoId = userInfoObject.optString("user_id");
                    userInfoName = userInfoObject.optString("user_name");
                    userInfoImage = userInfoObject.optString("user_image");
                    userInfoBean.setUserId(userInfoObject.optString("user_id"));
                    userInfoBean.setUserName(userInfoObject.optString("user_name"));
                    userInfoBean.setUserImage(userInfoObject.optString("user_image"));
                    userInfoBeanList.add(userInfoBean);
                }
                L.d("----------------------", userInfoBeanList.size());
                Message message1 = Message.obtain();
                message1.what = 1;
                handler.sendMessage(message1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message message = Message.obtain();
        message.what = 0;
        message.obj = id;
        if (message.obj.equals("0") && message.obj != null) {
            handler.sendMessage(message);
        } else {
            handler1.sendMessage(message);
        }
        return MinId;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (msg.obj.equals("0")) {
                        evaluateAdapter.update();
                    }
                    break;
                case 1:
                    L.d(userInfoBeanList.size());
                    for (int i = 0; i < userInfoBeanList.size(); i++) {
                        UserInfoBean userInfoBean = userInfoBeanList.get(i);
                        if (new UserTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchRecord(userInfoBean.getUserId())) {
                            L.d("执行到此", "usertablecourse执行操作");
                            userOperate = new UserOperate(laughSQLiteOpenHelper.getReadableDatabase());
                            L.d("执行到此2", "usertablecourse执行操作");
                            userOperate.insert(userInfoBean.getUserId(), userInfoBean.getUserName(), userInfoBean.getUserImage());
                            L.d("执行到此3", "usertablecourse执行操作");
                        }
                    }
                    break;
            }
        }
    };

    class LoadMore implements Runnable {
        @Override
        public void run() {
            loadData("update_home_2down", MinId);
        }
    }

    class Refresh implements Runnable {
        @Override
        public void run() {
            refreshData("update_comment", "0");
        }
    }

    private String refreshData(String updatetype, String id) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("devicetype", "android");
        map.put("updatetype", updatetype);
        map.put("periodicalid", id);
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetHomeInfo, map);
            L.d(backMsg.toString());
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                JSONArray commentinfo = jsonObject.getJSONArray("commentinfo");
                L.d(commentinfo.toString());
                evaluateUserBeanList_loadmore.clear();
                userInfoBeanList.clear();
                for (int i = 0; i < commentinfo.length(); i++) {
                    EvaluateUserBean evaluateUserBean = new EvaluateUserBean(null, null, null, null);
                    JSONObject periodicalinfo = commentinfo.getJSONObject(i);
                    if (id.equals("0") && i == 0) {
                        MaxId = periodicalinfo.getString("comment_id");
                        L.d("最初的最大的comment_id", MaxId);
                        MinId = periodicalinfo.getString("comment_id");
                        L.d("最初的最小的comment_id", MinId);
                    }
                    if (Integer.parseInt(MinId) >= Integer.parseInt(periodicalinfo.getString("id"))) {
                        MinId = periodicalinfo.getString("id");
                        L.d("最小的id", MinId);
                    }
//                    MinId = periodicalinfo.getString("id");
                    if (Integer.parseInt(MaxId) <= Integer.parseInt(periodicalinfo.getString("id"))) {
                        MaxId = periodicalinfo.getString("id");
                        L.d("最大的id", MaxId);
                    }

                    evaluateUserBean.setEvaluateUser(periodicalinfo.getString("user_id"));
                    evaluateUserBean.setEvaluateTime(periodicalinfo.getString("comment_date"));
                    evaluateUserBean.setEvaluateDetail(EncryptUtil.decryptBASE64(periodicalinfo.getString("comment_content")));
                    evaluateUserBean.setEvaluateGoodTimes(periodicalinfo.getString("comment_praise_number"));
                    evaluateUserBeanList.clear();
                    evaluateUserBeanList.add(evaluateUserBean);
                }
                Message message = Message.obtain();
                message.what = 0;
                handler2.sendMessage(message);
                JSONArray userinfo = jsonObject.getJSONArray("userinfo");
                for (int i = 0; i < userinfo.length(); i++) {
                    JSONObject userInfoObject = userinfo.getJSONObject(i);
                    UserInfoBean userInfoBean = new UserInfoBean(null, null, null);
                    userInfoId = userInfoObject.optString("user_id");
                    userInfoName = userInfoObject.optString("user_name");
                    userInfoImage = userInfoObject.optString("user_image");
                    userInfoBean.setUserId(userInfoObject.optString("user_id"));
                    userInfoBean.setUserName(userInfoObject.optString("user_name"));
                    userInfoBean.setUserImage(userInfoObject.optString("user_image"));
                    userInfoBeanList.add(userInfoBean);
                }
                Message message1 = Message.obtain();
                message1.what = 1;
                handler.sendMessage(message1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return MinId;
    }

    @Override
    public void onResume() {
        super.onResume();
        SingleImageTaskUtil imageTask = new SingleImageTaskUtil(evaluate_icon);
        imageTask.execute((String) SP.get(getActivity(), SP.USER_DATA_USERICONURL, ""));
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
