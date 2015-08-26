package com.cj.dreams.video.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.bean.AppGradeBean;
import com.cj.dreams.video.dialog.CheckNetDialog;
import com.cj.dreams.video.dialog.CheckTheNetDialog;
import com.cj.dreams.video.dialog.CheckUpdateDialog;
import com.cj.dreams.video.dialog.ExitChooseDialog;
import com.cj.dreams.video.dialog.MainExitDialog;
import com.cj.dreams.video.fragment.HomePageFragment;
import com.cj.dreams.video.fragment.PersonalFragment;
import com.cj.dreams.video.fragment.RankingFragment;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.PostUtil;
import com.cj.dreams.video.util.S;
import com.cj.dreams.video.util.SP;
import com.cj.dreams.video.util.T;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

    private HomePageFragment fg1;
    private RankingFragment fg2;
    private PersonalFragment fg3;
    private FrameLayout flayout;
    private RelativeLayout course_layout;
    private RelativeLayout found_layout;
    private RelativeLayout settings_layout;
    private ImageView course_image;
    private ImageView found_image;
    private ImageView settings_image;
    private TextView course_text;
    private TextView settings_text;
    private TextView found_text;
    private int whirt = 0xFFFFFFFF;
    private int gray;
    private int blue = 0xFF000000;
    FragmentManager fManager;
    private String mainActivity;
    private InputStream inputStream;
    private URLConnection connection;
    private OutputStream outputStream;
    private int fileLength, DownedFileLength;
    private String version, downloadUrl, updateContent;
    private String versionString = "", appVersionString = "";
    private int versionInt, appVersionInt;
    private AppGradeBean appGradeBean;
    private String Appshopname, Appshopurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        fManager = getSupportFragmentManager();
        gray=getResources().getColor(R.color.maintext);
        initViews();
        Thread loadThread = new Thread(new LoadThread());
        loadThread.start();
    }

    class LoadThread implements Runnable {
        @Override
        public void run() {
            getNewVersion();
        }
    }

    public void getNewVersion() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("channelid", channelId);
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetServerVersion, map);
            L.d(backMsg.toString());
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                version = jsonObject.optString("Version");
                downloadUrl = jsonObject.optString("DownloadUrl");
                updateContent = jsonObject.optString("UpdateContent");
                Appshopurl = jsonObject.optString("Appshopurl");
                Appshopname = jsonObject.optString("Appshopname");
                SP.put(this, SP.Appshopname, Appshopname);
                SP.put(this, SP.Appshopurl, Appshopurl);
                L.d((String) SP.get(this, SP.Appshopname, ""));
                L.d((String) SP.get(this, SP.Appshopurl, ""));
                Message message = Message.obtain();
                message.what = 0;
                for (int i = 0; i < version.length(); i++) {
                    if (version.charAt(i) != '.') {
                        versionString += version.charAt(i);
                    }
                }
                for (int i = 0; i < appVersion.length(); i++) {
                    if (appVersion.charAt(i) != '.') {
                        appVersionString += appVersion.charAt(i);
                    }
                }
                versionInt = Integer.parseInt(versionString);
                L.d("获取版本号--------------------------------------");
                L.d(versionInt);
                appVersionInt = Integer.parseInt(appVersionString);
                L.d(appVersionInt);
                if (versionInt > appVersionInt) {
                    message.what = 1;
                    updateHandler.sendMessage(message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    CheckUpdateDialog dialog = new CheckUpdateDialog(MainActivity.this, R.style.mystyle, R.layout.dialog_exit_main, true, downloadUrl, updateContent, MainActivity.this.getPackageName());
                    dialog.show();
                    break;
            }
        }
    };

    //完成组件的初始化
    public void initViews() {
        course_image = (ImageView) findViewById(R.id.course_image);
        found_image = (ImageView) findViewById(R.id.found_image);
        settings_image = (ImageView) findViewById(R.id.setting_image);
        course_text = (TextView) findViewById(R.id.course_text);
        found_text = (TextView) findViewById(R.id.found_text);
        settings_text = (TextView) findViewById(R.id.setting_text);
        course_layout = (RelativeLayout) findViewById(R.id.course_layout);
        found_layout = (RelativeLayout) findViewById(R.id.found_layout);
        settings_layout = (RelativeLayout) findViewById(R.id.setting_layout);
        course_layout.setOnClickListener(this);
        found_layout.setOnClickListener(this);
        settings_layout.setOnClickListener(this);
        setChioceItem(0);
    }

    public void setChioceItem(int index) {
        FragmentTransaction transaction = fManager.beginTransaction();
        clearChioce();
        hideFragments(transaction);
        switch (index) {
            case 0:
                course_image.setImageResource(R.drawable.index_home_press);
                course_text.setTextColor(this.getResources().getColor(R.color.red));
                if (fg1 == null) {
                    fg1 = new HomePageFragment();
                    transaction.add(R.id.content, fg1);
                } else {
                    transaction.show(fg1);
                }
                break;

            case 1:
                found_image.setImageResource(R.drawable.index_rank_press);
                found_text.setTextColor(this.getResources().getColor(R.color.red));
                if (fg2 == null) {
                    fg2 = new RankingFragment();
                    transaction.add(R.id.content, fg2);
                } else {
                    transaction.show(fg2);
                }
                break;

            case 2:
                settings_image.setImageResource(R.drawable.index_personal_press);
                settings_text.setTextColor(this.getResources().getColor(R.color.red));
                if (fg3 == null) {
                    fg3 = new PersonalFragment();
                    transaction.add(R.id.content, fg3);
                } else {
                    transaction.show(fg3);
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fg1 != null) {
            transaction.hide(fg1);
        }
        if (fg2 != null) {
            transaction.hide(fg2);
        }
        if (fg3 != null) {
            transaction.hide(fg3);
        }
    }

    public void clearChioce() {
        course_image.setImageResource(R.drawable.index_home_normal);
        course_text.setTextColor(gray);
        found_image.setImageResource(R.drawable.index_rank_normal);
        found_text.setTextColor(gray);
        settings_image.setImageResource(R.drawable.index_personal_normal);
        settings_text.setTextColor(gray);
    }


    private void checkTheNet() {
        if (CheckNetworkState().equals(mobileState)) {
            CheckNetDialog dialog = new CheckNetDialog(this, R.style.mystyle, R.layout.dialog_exit_main, "移动网络", "您确定继续使用移动网络？", mobileState, mainActivity);
            dialog.show();
        }
//        if (CheckNetworkState().equals(wifiState)){
//            CheckNetDialog dialog = new CheckNetDialog(this, R.style.mystyle, R.layout.dialog_exit_main, "wifi网络", "请放心使用wifi网络！", mobileState, mainActivity);
//            dialog.show();
//        }
        if (CheckNetworkState().equals(noState)) {
            CheckNetDialog dialog = new CheckNetDialog(this, R.style.mystyle, R.layout.dialog_exit_main, "无网络", "是否去设置网络？", mobileState, mainActivity);
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_layout:
                setChioceItem(0);
                break;
            case R.id.found_layout:
                setChioceItem(1);
                break;
            case R.id.setting_layout:
                setChioceItem(2);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ConfirmExit();
                break;
            case KeyEvent.KEYCODE_MENU:
                ExitChooseDialog gradedialog = new ExitChooseDialog(this, R.style.transparentFrameWindowStyle, R.layout.dialog_exit_choose);
                gradedialog.show();
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void ConfirmExit() {
        MainExitDialog dialog = new MainExitDialog(this, R.style.mystyle, R.layout.dialog_exit_main);
        dialog.show();
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
