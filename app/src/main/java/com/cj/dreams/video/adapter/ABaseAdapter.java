package com.cj.dreams.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cj.dreams.video.R;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by fanyafeng on 2015/7/20/0020.
 */
public class ABaseAdapter extends BaseAdapter {
    protected Context context;
//    protected String BaseUrl = "http://test.baoxiaons.com/";
//    protected String BaseUrl = "http://www.baoxiaons.com/";
    protected String BaseUrl = "http://video.ktdsp.com/";

    protected String GetHomeInfo = "get_home_videoInfo.php";
    protected String GetRealUrl = "video_api/url_to_m3u8.php";
    protected String GetMoreVideoInfo = "get_more_videoInfo.php";
    protected String GetTopVideo = "playtop100.php";
    protected String GetRecommendVideo = "recommended_video.php";
    protected String GetUserEvaluate = "get_user_comment.php";
    protected String PostVideoInfo = "update_videoInfo.php";
    protected String PostVideoComment = "user_comment.php";
    protected String PostShareId = "share.php?id=";


    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    protected void showShare() {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(context.getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(context);
    }
}
