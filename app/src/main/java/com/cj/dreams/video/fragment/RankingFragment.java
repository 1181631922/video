package com.cj.dreams.video.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.VideoViewPlayingActivity;
import com.cj.dreams.video.adapter.VideoListAdapter;
import com.cj.dreams.video.bean.VideoListBean;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;
import com.cj.dreams.video.dbhelper.RecordTableCourse;
import com.cj.dreams.video.dboperate.RecordOperate;
import com.cj.dreams.video.layout.PullToRefreshLayout;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.PostUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RankingFragment extends BaseFragment {
    private PullToRefreshLayout ptrl;
    private ListView listView;
    private VideoListAdapter videoListAdapter;
    private List<VideoListBean> videoListBeanList = new ArrayList<VideoListBean>();
    private List<VideoListBean> videoListBeanList_first = new ArrayList<VideoListBean>();
    private List<VideoListBean> videoListBeanList_more = new ArrayList<VideoListBean>();
    private List<Map<String, Object>> videoInfoList = new ArrayList<Map<String, Object>>();
    private String id_info, url_info, title_info, image_info;
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
    private RecordOperate recordOperate;
    private LinearLayout fragment_ranking_linearlayout;
    private TextView ranking_header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        return view;
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


    private void initView() {
        fragment_ranking_linearlayout = (LinearLayout) getActivity().findViewById(R.id.fragment_ranking_linearlayout);

        ptrl = ((PullToRefreshLayout) getActivity().findViewById(R.id.refresh_ranking_view));
        ptrl.setOnRefreshListener(new MyListener());
        listView = (ListView) getActivity().findViewById(R.id.ranking_listview);

        videoListAdapter = new VideoListAdapter(getActivity(), videoListBeanList);
        listView.setAdapter(videoListAdapter);
        listView.setFocusable(true);
        listView.setOnItemClickListener(new IndexOnItemClickListener());
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
            Thread postThread = new Thread(new PostThread(id_info, "play"));
            postThread.start();
            startActivity(intent);
        }
    }

    class LoadThread implements Runnable {
        @Override
        public void run() {
            loadData();
        }
    }

    private void loadData() {
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetTopVideo, null);
            L.d("播放排名得到的返回参数", backMsg.toString());
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                JSONArray videoArray = jsonObject.getJSONArray("video");
                videoListBeanList_more.clear();
                for (int i = 0; i < videoArray.length(); i++) {
                    VideoListBean videoListBean = new VideoListBean(null, null, null, null, null, null, null);
                    JSONObject object = videoArray.getJSONObject(i);
                    videoListBean.setVideoId(object.getString("id"));
                    videoListBean.setVideoTitle(object.getString("title"));
                    videoListBean.setVideoUrl(object.getString("url"));
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

    private void initData() {

    }

    class PostThread implements Runnable {
        private String id;
        private String type;

        PostThread(String id, String type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public void run() {
            postData(id, type);
        }
    }

    private void postData(String videoId, String buttonType) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("videoid", videoId);
        map.put("type", buttonType);
        try {
            String backMsg = PostUtil.postData(BaseUrl + PostVideoInfo, map);
            L.d("运行到此，播放次数加1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyListener implements PullToRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (!CheckNetworkState().equals(noState)) {
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    } else {
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    }
                }
            }.sendEmptyMessageDelayed(0, 500);
        }

        @Override
        public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (!CheckNetworkState().equals(noState)) {
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    } else {
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
            }.sendEmptyMessageDelayed(0, 500);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        videoListAdapter.update();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
