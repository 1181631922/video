package com.cj.dreams.video.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.VideoViewPlayingActivity;
import com.cj.dreams.video.adapter.VideoListAdapter;
import com.cj.dreams.video.bean.VideoListBean;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;
import com.cj.dreams.video.dbhelper.RecordTableCourse;
import com.cj.dreams.video.dboperate.RecordOperate;
import FanYaFeng.L;
import FanYaFeng.PostUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ListView recommend_listview;
    private VideoListAdapter videoListAdapter;
    private List<VideoListBean> videoListBeanList = new ArrayList<VideoListBean>();
    private List<VideoListBean> videoListBeanList_first = new ArrayList<VideoListBean>();
    private List<Map<String, Object>> videoInfoList = new ArrayList<Map<String, Object>>();
    private ImageView add;
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
    private RecordOperate recordOperate;
    private String id_info, url_info, title_info, image_info;

    public static RecommendFragment newInstance(String param1, String param2) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(getActivity());
        laughSQLiteOpenHelper.getWritableDatabase();
        initView();
        if (!CheckNetworkState().equals(noState)) {
            Thread loadThread = new Thread(new LoadThread());
            loadThread.start();
        } else {

        }
        initData();
    }

    private void initView() {
        recommend_listview = (ListView) getActivity().findViewById(R.id.recommend_listview);

        videoListAdapter = new VideoListAdapter(getActivity(), videoListBeanList);
        recommend_listview.setAdapter(videoListAdapter);
        recommend_listview.setFocusable(true);
        recommend_listview.setOnItemClickListener(new IndexOnItemClickListener());
    }

    private class IndexOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), VideoViewPlayingActivity.class);
            for (int i = 0; i <= position; i++) {
                if (position == i) {
                    Map map = (Map) videoInfoList.get(i);
                    url_info = (String) map.get("url_info");
                    intent.putExtra("url_info", url_info);
                    id_info = (String) map.get("id_info");
                    intent.putExtra("id_info", id_info);
                    title_info = (String) map.get("title_info");
                    intent.putExtra("title_info", title_info);
                    image_info = (String) map.get("image_info");
                    intent.putExtra("image_info", image_info);
                    intent.putExtra("good_info", (String) map.get("good_info"));
                    intent.putExtra("collect_info", (String) map.get("collect_info"));
                }
            }
            if (new RecordTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchRecord(id_info)) {
                recordOperate = new RecordOperate(laughSQLiteOpenHelper.getReadableDatabase());
                recordOperate.insert(id_info, image_info, title_info, url_info);
            }
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void initData() {

    }

    class LoadThread implements Runnable {
        @Override
        public void run() {
            loadData();
        }
    }

    private void loadData() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("devicetype", "android");
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetRecommendVideo, map);
            L.d("推荐列表具体内容", backMsg);
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                JSONArray videoArray = jsonObject.getJSONArray("video");
                for (int i = 0; i < videoArray.length(); i++) {
                    VideoListBean videoListBean = new VideoListBean(null, null, null, null, null, null, null);
                    JSONObject object = videoArray.getJSONObject(i);
                    videoListBean.setVideoId(object.getString("id"));
                    videoListBean.setVideoUrl(object.getString("url"));
                    videoListBean.setVideoTitle(object.getString("title"));
                    videoListBean.setVideoImage(BaseUrl + object.getString("image"));
                    videoListBean.setVideoCollectTimes(Integer.parseInt(object.getString("collect_num")) + "");
                    videoListBean.setVideoPlayTimes(Integer.parseInt(object.getString("play_number")) + "");
                    videoListBean.setVideoGoodTimes(Integer.parseInt(object.getString("praise_num")) + "");
                    videoListBeanList_first.add(videoListBean);

                    Map<String, Object> idmap = new HashMap<String, Object>();
                    idmap.put("url_info", object.getString("url"));
                    idmap.put("id_info", object.getString("id"));
                    idmap.put("title_info", object.getString("title"));
                    idmap.put("image_info", BaseUrl + object.getString("image"));
                    idmap.put("good_info", Integer.parseInt(object.getString("praise_num")) + "");
                    idmap.put("collect_info", Integer.parseInt(object.getString("collect_num")) + "");
                    videoInfoList.add(idmap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message message = Message.obtain();
        message.what = 0;
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    videoListBeanList.addAll(videoListBeanList_first);
                    videoListAdapter.update();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
