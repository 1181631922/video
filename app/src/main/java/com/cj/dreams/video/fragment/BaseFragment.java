package com.cj.dreams.video.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.dreams.video.R;
import com.cj.dreams.video.dialog.CheckTheNetDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class BaseFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    protected String UserType, appVersion;
    protected static String BaseUrl;
    //    protected String BaseUrl = "http://www.baoxiaons.com/";
//    protected String BaseUrl="http://video.ktdsp.com/";
//protected String BaseUrl = "http://test.baoxiaons.com/";
    protected String GetHomeInfo = "get_home_videoInfo.php";
    protected String GetRealUrl = "video_api/url_to_m3u8.php";
    protected String GetMoreVideoInfo = "get_more_videoInfo.php";
    protected String GetTopVideo = "playtop100.php";
    protected String GetRecommendVideo = "recommended_video.php";
    protected String GetUserEvaluate = "get_user_comment.php";
    protected String PostVideoInfo = "update_videoInfo.php";
    protected String PostVideoComment = "user_comment.php";
    protected String PostShareId = "share.php?id=";
    protected String GetShareUrl = "/share.php";
    protected String GetServerVersion = "get_android_version.php";
    protected String SendUserInfo = "set_userinfo.php";
    protected String GetVideoInfo = "get_videobyvideoid.php";
    protected String mobileState = "mobileState", wifiState = "wifiState", noState = "noState";

    public static BaseFragment newInstance(String param1, String param2) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getPropertyFileContent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onClick(View v) {

    }

    protected String CheckNetworkState() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
            return mobileState;
        }
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            return wifiState;
        }
        return noState;
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }

    private void getPropertyFileContent() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = getActivity().getAssets().open("fanyafeng.properties");
            properties.load(inputStream);
            UserType = properties.getProperty("usertype");
            BaseUrl = properties.getProperty("baseurl");
        } catch (IOException e) {
            e.printStackTrace();
        }

        PackageManager manager = getActivity().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            appVersion = info.versionName;   //版本名
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
