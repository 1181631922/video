package com.cj.dreams.video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.adapter.VideoListAdapter;
import com.cj.dreams.video.bean.VideoListBean;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;
import com.cj.dreams.video.dbhelper.RecordTableCourse;
import com.cj.dreams.video.dboperate.RecordOperate;
import com.cj.dreams.video.dialog.CheckTheNetDialog;
import com.cj.dreams.video.layout.PullToRefreshLayout;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.PostUtil;
import com.cj.dreams.video.util.StringTools;
import com.cj.dreams.video.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VideoListActivity extends BaseNoActionbarActivity {
    private PullToRefreshLayout ptrl;
    private ListView listView;
    private VideoListAdapter videoListAdapter;
    private List<VideoListBean> videoListBeanList = new ArrayList<VideoListBean>();
    private List<VideoListBean> videoListBeanList_first = new ArrayList<VideoListBean>();
    private List<VideoListBean> videoListBeanList_more = new ArrayList<VideoListBean>();
    private String periodicalid;
    //    private List<VideoListBean> videoListBeanList = new ArrayList<VideoListBean>();
    private List<Map<String, Object>> videoInfoList = new ArrayList<Map<String, Object>>();
    private String MaxId, MinId, id_info, title_info, image_info, url_info, name;
    private Handler handler1, handler2;
    private ImageView video_list_back;
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
    private RecordOperate recordOperate;
    private TextView video_list_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(VideoListActivity.this);
        laughSQLiteOpenHelper.getWritableDatabase();
        setContentView(R.layout.activity_video_list);
        initView();
        Thread loadThread = new Thread(new LoadThread());
        loadThread.start();
    }

    private void initView() {
        Intent intent = this.getIntent();
        periodicalid = intent.getStringExtra("periodicalid");
        name = intent.getStringExtra("name");
        video_list_title = (TextView) findViewById(R.id.video_list_title);
        video_list_title.setText(name);
        video_list_back = (ImageView) findViewById(R.id.video_list_back);
        video_list_back.setOnClickListener(this);
        ptrl = ((PullToRefreshLayout) findViewById(R.id.refresh_videolist_view));
        ptrl.setOnRefreshListener(new MyListener());
        listView = (ListView) findViewById(R.id.video_listview);

        videoListAdapter = new VideoListAdapter(VideoListActivity.this, videoListBeanList);
        listView.setAdapter(videoListAdapter);
        listView.setFocusable(true);
        listView.setOnItemClickListener(new IndexOnItemClickListener());
    }

    private class IndexOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(VideoListActivity.this, VideoViewPlayingActivity.class);
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

    class LoadThread implements Runnable {
        @Override
        public void run() {
            loadData("0");
        }
    }

    class LoadMore implements Runnable {
        @Override
        public void run() {
            loadData(MaxId);
        }
    }

    private String loadData(String video) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("type", "iphone");
        map.put("periodicalid", periodicalid);
        map.put("videoid", video);
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetMoreVideoInfo, map);
            L.d("videolist得到的返回参数", backMsg.toString());
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                JSONArray videoArray = jsonObject.getJSONArray("video");
                videoListBeanList_more.clear();
                for (int i = 0; i < videoArray.length(); i++) {
                    VideoListBean videoListBean = new VideoListBean(null, null, null, null, null, null, null);
                    JSONObject object = videoArray.getJSONObject(i);
                    if (video.equals("0") && i == 0) {
                        MinId = object.getString("id");
                        L.d("最小的id", MinId);
                    }
                    MaxId = object.getString("id");
                    videoListBean.setVideoId(object.getString("id"));
                    videoListBean.setVideoUrl(object.getString("url"));
                    videoListBean.setVideoTitle(object.getString("title"));
                    videoListBean.setVideoImage(BaseUrl + object.getString("image"));
                    videoListBean.setVideoCollectTimes(Integer.parseInt(object.getString("collect_num")) + "");
                    videoListBean.setVideoPlayTimes(Integer.parseInt(object.getString("play_number")) + "");
                    videoListBean.setVideoGoodTimes(Integer.parseInt(object.getString("praise_num")) + "");

                    Map<String, Object> idmap = new HashMap<String, Object>();
                    idmap.put("url_info", object.getString("url"));
                    idmap.put("id_info", object.getString("id"));
                    idmap.put("title_info", object.getString("title"));
                    idmap.put("image_info", BaseUrl + object.getString("image"));
                    idmap.put("good_info", Integer.parseInt(object.getString("praise_num")) + "");
                    idmap.put("collect_info", Integer.parseInt(object.getString("collect_num")) + "");
                    videoInfoList.add(idmap);

                    if (video.equals("0")) {
                        videoListBeanList_first.add(videoListBean);
                    } else {
                        videoListBeanList_more.add(videoListBean);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message message = Message.obtain();
        message.what = 0;
        message.obj = video;
        if (message.obj.equals("0")) {
            handler.sendMessage(message);
        } else {
            handler1.sendMessage(message);
        }
        return MaxId;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (msg.obj.equals("0")) {
                        videoListBeanList.addAll(videoListBeanList_first);
                        videoListAdapter.update();
                    }
                    break;
            }
        }
    };


    private class MyListener implements PullToRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
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
                                    if (!msg.obj.equals("0") && !msg.equals(MinId)) {
                                        L.d("获取到的最下面的id", MinId);
                                        L.d(videoListBeanList.toString());
                                        videoListBeanList.addAll(videoListBeanList_more);
                                        videoListAdapter.update();
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.video_list_back:
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CheckNetworkState().equals(mobileState)) {
//            CheckTheNetDialog dialog = new CheckTheNetDialog(this, R.style.mystyle, R.layout.dialog_exit_main, "移动网络", "您确定继续使用移动网络？", mobileState);
//            dialog.show();
            T.showLong(this, "正在使用移动网络");
        }
        if (CheckNetworkState().equals(noState)) {
//            CheckTheNetDialog dialog = new CheckTheNetDialog(this, R.style.mystyle, R.layout.dialog_exit_main, "无网络", "是否去设置网络？", mobileState);
//            dialog.show();
            T.showLong(this, "无网络服务");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
