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
import com.cj.dreams.video.dialog.CheckNetDialog;
import com.cj.dreams.video.dialog.CheckTheNetDialog;
import com.cj.dreams.video.dialog.CheckUpdateDialog;
import com.cj.dreams.video.dialog.MainExitDialog;
import com.cj.dreams.video.fragment.HomePageFragment;
import com.cj.dreams.video.fragment.PersonalFragment;
import com.cj.dreams.video.fragment.RankingFragment;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.PostUtil;
import com.cj.dreams.video.util.S;
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

public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

    //定义3个Fragment的对象
    private HomePageFragment fg1;
    private RankingFragment fg2;
    private PersonalFragment fg3;
    //帧布局对象,就是用来存放Fragment的容器
    private FrameLayout flayout;
    //定义底部导航栏的三个布局
    private RelativeLayout course_layout;
    private RelativeLayout found_layout;
    private RelativeLayout settings_layout;
    //定义底部导航栏中的ImageView与TextView
    private ImageView course_image;
    private ImageView found_image;
    private ImageView settings_image;
    private TextView course_text;
    private TextView settings_text;
    private TextView found_text;
    //定义要用的颜色值
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xFF7597B3;
    private int blue = 0xFF000000;
    //定义FragmentManager对象
    FragmentManager fManager;
    private String mainActivity;
    private InputStream inputStream;
    private URLConnection connection;
    private OutputStream outputStream;
    private int fileLength, DownedFileLength;
    private String version, downloadUrl, updateContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        fManager = getSupportFragmentManager();
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

        try {
            String backMsg = PostUtil.postData(BaseUrl + GetServerVersion, null);
            L.d(backMsg.toString());
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                version = jsonObject.optString("Version");
                downloadUrl = jsonObject.optString("DownloadUrl");
                updateContent = jsonObject.optString("UpdateContent");
                Message message = Message.obtain();
                message.what = 0;

                if ((int) version.charAt(0) > (int) appVersion.charAt(0)) {
                    message.what = 1;
                    updateHandler.sendMessage(message);
                } else if ((int) version.charAt(0) == (int) appVersion.charAt(0)) {
                    if ((int) version.charAt(1) > (int) appVersion.charAt(1)) {
                        message.what = 1;
                        updateHandler.sendMessage(message);
                    } else if ((int) version.charAt(1) == (int) appVersion.charAt(1)) {
                        if ((int) version.charAt(2) > (int) appVersion.charAt(2)) {
                            message.what = 1;
                            updateHandler.sendMessage(message);
                        }
                    } else {

                    }
                } else {

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

    //定义一个选中一个item后的处理
    public void setChioceItem(int index) {
        //重置选项+隐藏所有Fragment
        FragmentTransaction transaction = fManager.beginTransaction();
        clearChioce();
        hideFragments(transaction);
        switch (index) {
            case 0:
                course_image.setImageResource(R.drawable.index_home_press);
                course_text.setTextColor(this.getResources().getColor(R.color.red));
                if (fg1 == null) {
                    // 如果fg1为空，则创建一个并添加到界面上
                    fg1 = new HomePageFragment();
                    transaction.add(R.id.content, fg1);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fg1);
                }
                break;

            case 1:
                found_image.setImageResource(R.drawable.index_rank_press);
                found_text.setTextColor(this.getResources().getColor(R.color.red));
                if (fg2 == null) {
                    // 如果fg1为空，则创建一个并添加到界面上
                    fg2 = new RankingFragment();
                    transaction.add(R.id.content, fg2);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fg2);
                }
                break;

            case 2:
                settings_image.setImageResource(R.drawable.index_personal_press);
                settings_text.setTextColor(this.getResources().getColor(R.color.red));
                if (fg3 == null) {
                    // 如果fg1为空，则创建一个并添加到界面上
                    fg3 = new PersonalFragment();
                    transaction.add(R.id.content, fg3);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fg3);
                }
                break;
        }
        transaction.commit();
    }

    //隐藏所有的Fragment,避免fragment混乱
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

    //定义一个重置所有选项的方法
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
                ConfirmExit();
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void ConfirmExit() {
        MainExitDialog dialog = new MainExitDialog(this, R.style.mystyle, R.layout.dialog_exit_main);
        dialog.show();
    }

    private void DownFile(String durl) {
        try {
            URL url = new URL(durl);
            connection = url.openConnection();
            inputStream = connection.getInputStream();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String downloadDir = Environment.getExternalStorageDirectory().getPath() + "/download";
        File file1 = new File(downloadDir);
        if (!file1.exists()) {
            file1.mkdir();
        }
        String filePath = downloadDir + "/laugh.apk";
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Message message = new Message();
        try {
            outputStream = new FileOutputStream(file);
            fileLength = connection.getContentLength();
            int bufferLen = 1024;
            byte[] buffer = new byte[bufferLen];
            message.what = 0;
            progressHandler.sendMessage(message);
            int count = 0;
            while ((count = inputStream.read(buffer)) != -1) {
                DownedFileLength += count;
                outputStream.write(buffer, 0, count);
                Message message1 = new Message();
                message1.what = 1;
                progressHandler.sendMessage(message1);
            }

            Message message2 = new Message();
            message2.what = 2;
            progressHandler.sendMessage(message2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
//                      notificationProgress.setMax(fileLength);
                        break;
                    case 1:
//                      notificationProgress.setProgress(DownedFileLength);
                        int x = DownedFileLength * 100 / fileLength;
                        break;
                    case 2:
//                      progressBar.setVisibility(View.GONE);
                        break;
                    case 3:
//                      if (alertDialog.isShowing()) {
//                          alertDialog.cancel();
//                      }
                        break;
                    default:
                        break;
                }
            }
        }
    };

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
