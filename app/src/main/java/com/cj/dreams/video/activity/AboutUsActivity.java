package com.cj.dreams.video.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.dialog.CopyEmailDialog;
import com.cj.dreams.video.dialog.CopyWechatDialog;
import com.cj.dreams.video.dialog.TeamTestDialog;
import com.cj.dreams.video.util.FileManagerUtil;
import com.cj.dreams.video.util.SP;
import com.cj.dreams.video.util.T;

public class AboutUsActivity extends BaseCustomActivity {
    private TextView about_wechat_text, about_email_text, about_test_text, about_us_version;
    private String ourEmail, ourChat;
    private LinearLayout about_test_linearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.base_custom_title_bar);
        title = "关于";
        initView();
        initData();
    }

    private void initView() {
        about_test_linearlayout = (LinearLayout) findViewById(R.id.about_test_linearlayout);
        about_us_version = (TextView) findViewById(R.id.about_us_version);
        about_us_version.setText("爆笑女神 V" + appVersion);
        about_wechat_text = (TextView) findViewById(R.id.about_wechat_text);
        about_wechat_text.setOnClickListener(this);
        about_email_text = (TextView) findViewById(R.id.about_email_text);
        about_email_text.setOnClickListener(this);
        about_test_text = (TextView) findViewById(R.id.about_test_text);
        about_test_text.setOnClickListener(this);
        if (!(boolean) SP.get(this, SP.TeamTestType, false)) {
            about_test_text.setText("正常浏览模式");
        } else {
            about_test_text.setText("团队测试模式");
        }
        if (channelId.equals("test")) {
            about_test_linearlayout.setVisibility(View.VISIBLE);
        } else {
            about_test_linearlayout.setVisibility(View.GONE);
        }
    }

    private void initData() {
        ourEmail = "cj_dreams@163.com";
        ourChat = "goddesslaugh";
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.about_wechat_text:
                CopyWechatDialog wechatDialog1 = new CopyWechatDialog(this, R.style.mystyle, R.layout.dialog_exit_main);
                wechatDialog1.show();
                break;
            case R.id.about_email_text:
                CopyEmailDialog emailDialog1 = new CopyEmailDialog(this, R.style.mystyle, R.layout.dialog_exit_main);
                emailDialog1.show();
                break;
            case R.id.about_test_text:
                TeamTestDialog teamTestDialog = new TeamTestDialog(this, R.style.mystyle, R.layout.dialog_exit_main);
                teamTestDialog.show();
//                T.showLong(this, SP.get(this, SP.TeamTestType, false).toString());
//                T.showLong(this, appVersion);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed_back, menu);
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
