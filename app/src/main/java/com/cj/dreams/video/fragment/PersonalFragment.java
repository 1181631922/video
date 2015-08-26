package com.cj.dreams.video.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.AboutUsActivity;
import com.cj.dreams.video.activity.FreeStatementActivity;
import com.cj.dreams.video.activity.LocalRecordActivity;
import com.cj.dreams.video.activity.LoginActivity;
import com.cj.dreams.video.activity.TestActivity;
import com.cj.dreams.video.dialog.ConfirmWipeCacheDialog;
import com.cj.dreams.video.dialog.GradeDialog;

import FanYaFeng.L;
import FanYaFeng.PostUtil;
import FanYaFeng.SP;
import FanYaFeng.SingleImageTaskUtil;
import com.umeng.fb.FeedbackAgent;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class PersonalFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout personal_logined_linearlayout, personal_login_linearlayout, personal_setting_linearlayout;
    private RelativeLayout personal_logined_relativelayout, personal_login_relativelayout, personal_setting_relativelayout,
            personal_history_relativelayout, personal_favorate_relativelayout, personal_empty_relativelayout, personal_grade_relativelayout,
            personal_feedback_relativelayout, personal_about_relativelayout, personal_declare_relativelayout;
    private TextView personal_exit, personal_user_name;
    private ImageView personal_user_icon;
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/fanyafeng/";
    private String mFileName;
    private String mSaveMessage;
    private Bitmap mBitmap;
    private isLoginListener mListener;
    private Dialog dialog;
    private Button custom_test;
    private String getPicStr;

    public interface isLoginListener {
        public void isLoginListener(String message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        L.d("执行此方法onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {

        custom_test = (Button) getActivity().findViewById(R.id.custom_test);
        custom_test.setText("测试按钮"+"\n"+"一般的测试按钮");
        custom_test.setOnClickListener(this);

        personal_user_icon = (ImageView) getActivity().findViewById(R.id.personal_user_icon);
        personal_user_name = (TextView) getActivity().findViewById(R.id.personal_user_name);
        personal_logined_linearlayout = (LinearLayout) getActivity().findViewById(R.id.personal_logined_linearlayout);
        personal_login_linearlayout = (LinearLayout) getActivity().findViewById(R.id.personal_login_linearlayout);
        personal_setting_linearlayout = (LinearLayout) getActivity().findViewById(R.id.personal_setting_linearlayout);

        personal_logined_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_logined_relativelayout);
        personal_logined_relativelayout.setOnClickListener(this);
        personal_login_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_login_relativelayout);
        personal_login_relativelayout.setOnClickListener(this);
        personal_setting_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_setting_relativelayout);
        personal_setting_relativelayout.setOnClickListener(this);
        personal_history_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_history_relativelayout);
        personal_history_relativelayout.setOnClickListener(this);
        personal_favorate_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_favorate_relativelayout);
        personal_favorate_relativelayout.setOnClickListener(this);
        personal_empty_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_empty_relativelayout);
        personal_empty_relativelayout.setOnClickListener(this);
        personal_grade_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_grade_relativelayout);
        personal_grade_relativelayout.setOnClickListener(this);
        personal_feedback_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_feedback_relativelayout);
        personal_feedback_relativelayout.setOnClickListener(this);
        personal_about_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_about_relativelayout);
        personal_about_relativelayout.setOnClickListener(this);
        personal_declare_relativelayout = (RelativeLayout) getActivity().findViewById(R.id.personal_declare_relativelayout);
        personal_declare_relativelayout.setOnClickListener(this);

        personal_exit = (TextView) getActivity().findViewById(R.id.personal_exit);
        personal_exit.setOnClickListener(this);
        if ((boolean) SP.get(getActivity(), SP.USER_DATA_LOGINED, false)) {
            personal_user_name.setText((String) SP.get(getActivity(), SP.USER_DATA_USERNAME, ""));
            SingleImageTaskUtil imageTask = new SingleImageTaskUtil(personal_user_icon);
            imageTask.execute((String) SP.get(getActivity(), SP.USER_DATA_USERICONURL, ""));
            personal_login_linearlayout.setVisibility(View.GONE);
            personal_exit.setVisibility(View.VISIBLE);
            personal_logined_linearlayout.setVisibility(View.VISIBLE);
        } else {

        }

    }

    private void initData() {

    }


    @Override
    public void onResume() {
        super.onResume();
        if ((boolean) SP.get(getActivity(), SP.USER_DATA_LOGINED, false)) {
            personal_user_name.setText((String) SP.get(getActivity(), SP.USER_DATA_USERNAME, ""));
            SingleImageTaskUtil imageTask = new SingleImageTaskUtil(personal_user_icon);
            imageTask.execute((String) SP.get(getActivity(), SP.USER_DATA_USERICONURL, ""));
            personal_login_linearlayout.setVisibility(View.GONE);
            personal_exit.setVisibility(View.VISIBLE);
            personal_logined_linearlayout.setVisibility(View.VISIBLE);
//            Thread loadThread = new Thread(new LoadThread());
//            loadThread.start();
            Thread getbitmapThread = new Thread(new getBitmapThread());
            getbitmapThread.start();
        }


    }

    class getBitmapThread implements Runnable {
        @Override
        public void run() {
            getHttpBitmap((String) SP.get(getActivity(), SP.USER_DATA_USERICONURL, ""));
        }
    }

    class LoadThread implements Runnable {
        @Override
        public void run() {
            pushUserData();
        }
    }

    private void pushUserData() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("userid", (String) SP.get(getActivity(), SP.USER_DATA_USERID, ""));
        map.put("userimage", getPicStr);
        map.put("username", (String) SP.get(getActivity(), SP.USER_DATA_USERNAME, ""));
        try {
            String backMsg = PostUtil.postData(BaseUrl + SendUserInfo, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getHttpBitmap(String url) {
        URL myFileURL;
        try {
            myFileURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(true);
            InputStream is = conn.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
            int buffsize = 1024;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(buffsize);
            byte[] temp = new byte[buffsize];
            int size = 0;
            while ((size = bufferedInputStream.read(temp)) != -1) {
                byteArrayOutputStream.write(temp, 0, size);
            }
            bufferedInputStream.close();
            byte[] content = byteArrayOutputStream.toByteArray();
            getPicStr = new String(Base64.encode(content, 0, content.length, Base64.DEFAULT));

            L.d("得到的用户头像的string", getPicStr);
            is.close();
            Message message = Message.obtain();
            message.what = 0;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Thread loadThread = new Thread(new LoadThread());
                    loadThread.start();
                    break;
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        L.d("个人中心的fragment暂停状态");
    }

    private void showDialog() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_exit_choose, null);
        dialog = new Dialog(getActivity(), R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_exit:
                SP.put(getActivity(), SP.USER_DATA_LOGINED, false);
                SP.remove(getActivity(), SP.USER_DATA_USERNAME);
                SP.remove(getActivity(), SP.USER_DATA_USERID);
                SP.remove(getActivity(), SP.USER_DATA_USERICONURL);
                personal_login_linearlayout.setVisibility(View.VISIBLE);
                personal_exit.setVisibility(View.GONE);
                personal_logined_linearlayout.setVisibility(View.GONE);
                break;
            case R.id.personal_logined_relativelayout:
                break;
            case R.id.personal_login_relativelayout:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.personal_setting_relativelayout:
                break;
            case R.id.personal_history_relativelayout:
                Intent intent1 = new Intent(getActivity(), LocalRecordActivity.class);
                intent1.putExtra("local_title", "播放记录");
                startActivity(intent1);
                break;
            case R.id.personal_favorate_relativelayout:
                Intent intent2 = new Intent(getActivity(), LocalRecordActivity.class);
                intent2.putExtra("local_title", "我的收藏");
                startActivity(intent2);
                break;
            case R.id.personal_empty_relativelayout:
                ConfirmWipeCacheDialog dialog1 = new ConfirmWipeCacheDialog(getActivity(), R.style.mystyle, R.layout.dialog_confirm_with_progress);
                dialog1.show();
                break;
            case R.id.personal_grade_relativelayout:
                GradeDialog gradedialog = new GradeDialog(getActivity(), R.style.transparentFrameWindowStyle, R.layout.dialog_appshop);
                gradedialog.show();
                break;
            case R.id.personal_feedback_relativelayout:
                /**
                 * 友盟自带的反馈界面
                 * FeedbackAgent agent = new FeedbackAgent(getActivity());
                 * agent.startFeedbackActivity();
                 * 重新自定义的反馈界面
                 */
                FeedbackAgent agent = new FeedbackAgent(getActivity());
                agent.startFeedbackActivity();
//                Intent intent6 = new Intent(getActivity(), FeedBackActivity.class);
//                startActivity(intent6);
                break;
            case R.id.personal_about_relativelayout:
                Intent intent4 = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intent4);
                break;
            case R.id.personal_declare_relativelayout:
                Intent intent3 = new Intent(getActivity(), FreeStatementActivity.class);
                startActivity(intent3);
                break;
            //测试按钮
            case R.id.custom_test:
                Intent intent5 = new Intent(getActivity(), TestActivity.class);
                startActivity(intent5);
                break;
        }
    }


}
