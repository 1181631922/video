package com.cj.dreams.video.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.MainActivity;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.PostUtil;
import com.cj.dreams.video.util.SP;
import com.cj.dreams.video.util.T;
import com.umeng.fb.FeedbackAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class ExitChooseDialog extends BaseDialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    private RelativeLayout dialog_out;
    private Button main_tab_retroaction;
    private Button main_tab_bottom;
    private Button main_tab_exit;
    private Button main_tab_cancel;
    private String version, downloadUrl, updateContent, Appshopurl, Appshopname;
    private String versionString = "", appVersionString = "";
    private int versionInt, appVersionInt;

    public ExitChooseDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * @param context
     * @param resLayout
     */
    public ExitChooseDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param resLayout
     */
    public ExitChooseDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        dialog_out = (RelativeLayout) findViewById(R.id.dialog_out);
        main_tab_retroaction = (Button) findViewById(R.id.main_tab_retroaction);
        main_tab_bottom = (Button) findViewById(R.id.main_tab_bottom);
        main_tab_exit = (Button) findViewById(R.id.main_tab_exit);
        main_tab_cancel = (Button) findViewById(R.id.main_tab_cancel);

        main_tab_retroaction.setTextColor(0xff1E90FF);
        main_tab_bottom.setTextColor(0xff1E90FF);
        main_tab_exit.setTextColor(0xff1E90FF);
        main_tab_cancel.setTextColor(0xff1E90FF);

        dialog_out.setOnClickListener(this);
        main_tab_retroaction.setOnClickListener(this);
        main_tab_bottom.setOnClickListener(this);
        main_tab_exit.setOnClickListener(this);
        main_tab_cancel.setOnClickListener(this);

        Window window = this.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        this.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        ExitChooseDialog.this.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tab_retroaction:
                T.showShort(getContext(), "正在检测升级");
                getPropertyFileContent();
                Thread loadThread = new Thread(new LoadThread());
                loadThread.start();
                ExitChooseDialog.this.dismiss();
                break;
            case R.id.main_tab_bottom:
                FeedbackAgent agent = new FeedbackAgent(getContext());
                agent.startFeedbackActivity();
                ExitChooseDialog.this.dismiss();
                break;
            case R.id.main_tab_exit:
                Intent it_my_activity = new Intent(Intent.ACTION_CALL);
                it_my_activity.setClass(context, MainActivity.class);
                ((Activity) context).finish();
                ExitChooseDialog.this.dismiss();
                break;
            case R.id.main_tab_cancel:
                ExitChooseDialog.this.dismiss();
                break;
            case R.id.dialog_out:
                ExitChooseDialog.this.dismiss();
                break;
        }
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
                SP.put(getContext(), SP.Appshopname, Appshopname);
                SP.put(getContext(), SP.Appshopurl, Appshopurl);
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
                }else {
                    message.what = 0;
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
                    T.showShort(getContext(), "现在已经是最新版本");
                    break;
                case 1:
                    CheckUpdateDialog dialog = new CheckUpdateDialog(getContext(), R.style.mystyle, R.layout.dialog_exit_main, true, downloadUrl, updateContent, getContext().getPackageName());
                    dialog.show();
                    break;
            }
        }
    };

    private void getPropertyFileContent() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = getContext().getAssets().open("fanyafeng.properties");
            properties.load(inputStream);
            UserType = properties.getProperty("usertype");
            BaseUrl = properties.getProperty("baseurl");

        } catch (IOException e) {
            e.printStackTrace();
        }

        PackageManager manager = getContext().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            appVersion = info.versionName;   //版本名
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ApplicationInfo appInfo = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            channelId = appInfo.metaData.getString("UMENG_CHANNEL");
            L.d("Tag", " app channelId : " + channelId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}