package com.cj.dreams.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;

import FanYaFeng.L;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by fanyafeng on 2015/07/31/0031.
 */
public class BaseDialog extends Dialog implements View.OnClickListener {
    protected String mobileState = "mobileState", wifiState = "wifiState", noState = "noState";
    protected String guideActivity = "guideActivity", appStartActivity = "appStartActivity", mainActivity = "mainActivity";
    protected String newVersion = "发现新版本", nowVersion = "已经是最新版本", nowText = "当前已是最新版本";
    protected String UserType, appVersion;
    protected String channelId;

    protected String BaseUrl = "http://www.baoxiaons.com/";
    //    protected String BaseUrl = "http://test.baoxiaons.com/";
//    protected String BaseUrl = "http://video.ktdsp.com/";
    protected String GetHomeInfo = "get_home_videoInfo.php";
    protected String GetRealUrl = "video_api/url_to_m3u8.php";
    protected String GetMoreVideoInfo = "get_more_videoInfo.php";
    protected String GetTopVideo = "playtop100.php";
    protected String GetRecommendVideo = "recommended_video.php";
    protected String GetUserEvaluate = "get_user_comment.php";
    protected String PostVideoInfo = "update_videoInfo.php";
    protected String PostVideoComment = "user_comment.php";
    protected String PostShareId = "share.php?id=";
    protected String GetServerVersion = "get_android_version.php";
    protected String SendUserInfo = "set_userinfo.php";
    protected String GetVideoInfo = "get_videobyvideoid.php";

    public BaseDialog(Context context) {

        super(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void create() {
        getPropertyFileContent();
        super.create();
    }

    @Override
    public void onClick(View v) {

    }

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
