package com.cj.dreams.video.Test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.BaseCustomActivity;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.PostUtil;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TestIconActivity extends BaseCustomActivity {
    //    com.cj.dreams.video.Test.TestIconActivity
    private TextView getfromback;
    private String videoIdFromBackground;
    private String play_number, praise_num, collect_num, title, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_icon);
        initView();
        initData();
        //需要从服务器得到播放地址和相应的id
        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            Set<String> keySet = bun.keySet();
            for (String key : keySet) {
                videoIdFromBackground = bun.getString(key);
                L.d(videoIdFromBackground);
            }
        }

        Thread loadThread = new Thread(new LoadThread());
        loadThread.start();
    }

    class LoadThread implements Runnable {
        @Override
        public void run() {
            loadData();
        }
    }

    private void loadData() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("videoid", videoIdFromBackground);
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetVideoInfo, map);
            L.d(backMsg);
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                url = jsonObject.optString("url");
                title = jsonObject.optString("title");
                collect_num = jsonObject.optString("collect_num");
                praise_num = jsonObject.optString("praise_num");
                play_number = jsonObject.optString("play_number");
                Message message = Message.obtain();
                message.what = 0;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    getfromback.setText(url + '\n' + title + '\n' + collect_num + '\n' + praise_num + '\n' + play_number);
                    break;
            }
        }
    };


    private void initView() {
        ;
        getfromback = (TextView) findViewById(R.id.getfromback);
    }

    private void initData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_icon, menu);
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
}
