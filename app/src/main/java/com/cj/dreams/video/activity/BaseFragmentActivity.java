package com.cj.dreams.video.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cj.dreams.video.R;
import com.cj.dreams.video.dialog.CheckNetDialog;
import com.cj.dreams.video.dialog.CheckTheNetDialog;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.SP;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener {
    protected String UserType, appVersion;
    protected String appPackageName;
    protected String channelId;
    protected static String BaseUrl;
    //    protected String BaseUrl = "http://video.ktdsp.com/";
//    protected String BaseUrl = "http://test.baoxiaons.com/";
//    protected String BaseUrl = "http://www.baoxiaons.com/";
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
    protected String mobileState = "mobileState", wifiState = "wifiState", noState = "noState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPropertyFileContent();
    }

    // 检查网络状态
    protected String CheckNetworkState() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        // 如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
            return mobileState;
        }
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            return wifiState;
        }
        return noState;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void getPropertyFileContent() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = getAssets().open("fanyafeng.properties");
            properties.load(inputStream);
            UserType = properties.getProperty("usertype");
            BaseUrl = properties.getProperty("baseurl");
        } catch (IOException e) {
            e.printStackTrace();
        }

        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            appVersion = info.versionName;   //版本名
            appPackageName = info.packageName;
//            SP.put(this,SP.PackageName,appPackageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            channelId = appInfo.metaData.getString("UMENG_CHANNEL");
            L.d("Tag", " app channelId : " + channelId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }


}
