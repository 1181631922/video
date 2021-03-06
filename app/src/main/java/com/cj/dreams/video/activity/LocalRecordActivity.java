package com.cj.dreams.video.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.adapter.LocalRecordAdapter;
import com.cj.dreams.video.bean.LocalRecordBean;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;
import com.cj.dreams.video.dboperate.RecordOperate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalRecordActivity extends BaseNoActionbarActivity {
    private ListView local_record_listview;
    private LocalRecordAdapter localRecordAdapter;
    private List<LocalRecordBean> localRecordBeanList = new ArrayList<LocalRecordBean>();
    private List<Map<String, Object>> videoInfoList = new ArrayList<Map<String, Object>>();
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
    private RecordOperate recordOperate;
    private SQLiteDatabase db = null;
    private TextView local_title, local_record_empty;
    private String my_title;
    private ImageView local_record_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalRecordActivity.this.laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(LocalRecordActivity.this);
        laughSQLiteOpenHelper.getWritableDatabase();
        db = this.openOrCreateDatabase("laughvideo.db", Context.MODE_PRIVATE, null);
        setContentView(R.layout.activity_local_record);
        Intent intent = this.getIntent();
        my_title = intent.getStringExtra("local_title");
        initView();
        if (my_title.equals("播放记录")) {
            initLocalRecord(1);
        }
        if (my_title.equals("我的收藏")) {
            initFavorite();
        }
    }

    private void initView() {
        local_record_empty = (TextView) findViewById(R.id.local_record_empty);
        local_record_empty.setOnClickListener(this);
        local_record_back = (ImageView) findViewById(R.id.local_record_back);
        local_record_back.setOnClickListener(this);
        local_title = (TextView) findViewById(R.id.local_title);
        local_title.setText(my_title);
        local_record_listview = (ListView) findViewById(R.id.local_record_listview);
        localRecordAdapter = new LocalRecordAdapter(this, localRecordBeanList);
        local_record_listview.setAdapter(localRecordAdapter);
        local_record_listview.setOnItemClickListener(new IndexOnItemClickListener());
    }

    private void initLocalRecord(int i) {
        LocalRecordActivity.this.laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(LocalRecordActivity.this);
        laughSQLiteOpenHelper.getWritableDatabase();
        db = this.openOrCreateDatabase("laughvideo.db", Context.MODE_PRIVATE, null);
        local_record_empty.setVisibility(View.VISIBLE);
        String sql = "SELECT v_id,v_image,v_title,v_url FROM t_record WHERE v_image like ?";
        String args[] = new String[]{"%" + "http" + "%"};
        Cursor cursor = db.rawQuery(sql, args);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            LocalRecordBean localRecordBean = new LocalRecordBean(null, null, null, null);
            localRecordBean.setVideoId(cursor.getString(0));
            localRecordBean.setVideoImageUrl(cursor.getString(1));
            localRecordBean.setVideoTitle(cursor.getString(2));
            localRecordBean.setVideoUrl(cursor.getString(3));
            localRecordBeanList.add(localRecordBean);

            Map<String, Object> idmap = new HashMap<String, Object>();
            idmap.put("url_info", cursor.getString(3));
            idmap.put("id_info", cursor.getString(0));
            idmap.put("title_info", cursor.getString(2));
            idmap.put("image_info", cursor.getString(1));
            videoInfoList.add(idmap);
        }
        db.close();
        if (i == 0) {
            localRecordBeanList.clear();
            videoInfoList.clear();
        }
        localRecordAdapter.update();
    }

    private void initFavorite() {
        String sql = "SELECT v_id,v_image,v_title,v_url FROM t_collect WHERE v_image like ?";
        String args[] = new String[]{"%" + "http" + "%"};
        Cursor cursor = db.rawQuery(sql, args);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            LocalRecordBean localRecordBean = new LocalRecordBean(null, null, null, null);
            localRecordBean.setVideoId(cursor.getString(0));
            localRecordBean.setVideoImageUrl(cursor.getString(1));
            localRecordBean.setVideoTitle(cursor.getString(2));
            localRecordBean.setVideoUrl(cursor.getString(3));
            localRecordBeanList.add(localRecordBean);

            Map<String, Object> idmap = new HashMap<String, Object>();
            idmap.put("url_info", cursor.getString(3));
            idmap.put("id_info", cursor.getString(0));
            idmap.put("title_info", cursor.getString(2));
            idmap.put("image_info", cursor.getString(1));
            videoInfoList.add(idmap);
        }
        db.close();
        localRecordAdapter.update();
    }

    private class IndexOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(LocalRecordActivity.this, VideoViewPlayingActivity.class);
            for (int i = 0; i <= position; i++) {
                if (position == i) {
                    Map map = (Map) videoInfoList.get(i);
                    intent.putExtra("url_info", (String) map.get("url_info"));
                    intent.putExtra("id_info", (String) map.get("id_info"));
                    intent.putExtra("title_info", (String) map.get("title_info"));
                    intent.putExtra("image_info", (String) map.get("image_info"));
                    intent.putExtra("type_info", "notnone");
                }
            }
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.local_record_back:
                finish();
                break;
            case R.id.local_record_empty:
                EmptyRecordDialog dialog = new EmptyRecordDialog(this, R.style.mystyle, R.layout.dialog_exit_main);
                dialog.show();

                break;
        }
    }

    public class EmptyRecordDialog extends Dialog implements View.OnClickListener {
        int layoutRes;
        Context context;
        private TextView dialog_exit_title;
        private TextView dialog_exit_detail;
        private Button confirmBtn;
        private Button cancelBtn;
        private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
        private SQLiteDatabase db = null;

        public EmptyRecordDialog(Context context) {
            super(context);
            this.context = context;
        }

        /**
         * @param context
         * @param resLayout
         */
        public EmptyRecordDialog(Context context, int resLayout) {
            super(context);
            this.context = context;
            this.layoutRes = resLayout;
        }

        /**
         * @param context
         * @param theme
         * @param resLayout
         */
        public EmptyRecordDialog(Context context, int theme, int resLayout) {
            super(context, theme);
            this.context = context;
            this.layoutRes = resLayout;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(layoutRes);
            this.laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(context);
            laughSQLiteOpenHelper.getWritableDatabase();
            db = context.openOrCreateDatabase("laughvideo.db", Context.MODE_PRIVATE, null);
            dialog_exit_title = (TextView) findViewById(R.id.dialog_exit_title);
            dialog_exit_title.setText("清空历史");
            dialog_exit_detail = (TextView) findViewById(R.id.dialog_exit_detail);
            dialog_exit_detail.setText("确认要清空播放记录？");
            confirmBtn = (Button) findViewById(R.id.confirm_btn);
            cancelBtn = (Button) findViewById(R.id.cancel_btn);

            confirmBtn.setTextColor(0xff1E90FF);
            cancelBtn.setTextColor(0xff1E90FF);

            confirmBtn.setOnClickListener(this);
            cancelBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.confirm_btn:
                    db = laughSQLiteOpenHelper.getWritableDatabase();
                    db.execSQL("DELETE FROM " + "t_record");
                    db.close();
                    Message message = Message.obtain();
                    message.what = 0;
                    emptyHandler.sendMessage(message);
                    EmptyRecordDialog.this.dismiss();
                    break;
                case R.id.cancel_btn:
                    EmptyRecordDialog.this.dismiss();
                    break;
            }
        }

    }

    Handler emptyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    initLocalRecord(0);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_record, menu);
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
