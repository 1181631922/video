package com.cj.dreams.video.thirdpartylogin;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cj.dreams.video.R;
import com.cj.dreams.video.fragment.PersonalFragment;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.SP;
import com.cj.dreams.video.util.T;
import com.mob.tools.FakeActivity;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 中文注释
 * ShareSDK 官网地址 ： http://www.mob.com </br>
 * 1、这是用2.38版本的sharesdk，一定注意  </br>
 * 2、如果要咨询客服，请加企业QQ 4006852216 </br>
 * 3、咨询客服时，请把问题描述清楚，最好附带错误信息截图 </br>
 * 4、一般问题，集成文档中都有，请先看看集成文档；减少客服压力，多谢合作  ^_^</br></br></br>
 * <p/>
 * The password of demokey.keystore is 123456
 * *ShareSDK Official Website ： http://www.mob.com </br>
 * 1、Be carefully, this sample use the version of 2.11 sharesdk  </br>
 * 2、If you want to ask for help，please add our QQ whose number is 4006852216 </br>
 * 3、Please describe detail of the question , if you have the picture of the bugs or the bugs' log ,that is better </br>
 * 4、Usually, the answers of some normal questions is exist in our user guard pdf, please read it more carefully,thanks  ^_^
 */
public class ThirdPartyLogin extends FakeActivity implements OnClickListener, Callback, PlatformActionListener {
    private static final int MSG_SMSSDK_CALLBACK = 1;
    private static final int MSG_AUTH_CANCEL = 2;
    private static final int MSG_AUTH_ERROR = 3;
    private static final int MSG_AUTH_COMPLETE = 4;

    private String smssdkAppkey;
    private String smssdkAppSecret;
    private OnLoginListener signupListener;
    private Handler handler;
    //短信验证的对话框
    private Dialog msgLoginDlg;


    /**
     * 填写从短信SDK应用后台注册得到的APPKEY和APPSECRET
     */
    public void setSMSSDKAppkey(String appkey, String appSecret) {
        smssdkAppkey = appkey;
        smssdkAppSecret = appSecret;
    }

    private Context context;

    public ThirdPartyLogin(Context context) {
        this.context = context;
    }

    /**
     * 设置授权回调，用于判断是否进入注册
     */
    public void setOnLoginListener(OnLoginListener l) {
        this.signupListener = l;
    }

    public void onCreate() {
        // 初始化ui
        handler = new Handler(this);
        activity.setContentView(R.layout.activity_login);
        (activity.findViewById(R.id.login_weichat_icon)).setOnClickListener(this);
        (activity.findViewById(R.id.login_sina_icon)).setOnClickListener(this);
        (activity.findViewById(R.id.login_qq_icon)).setOnClickListener(this);
        (activity.findViewById(R.id.login_back)).setOnClickListener(this);

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_weichat_icon: {
                //微信登录
                //测试时，需要打包签名；sample测试时，用项目里面的demokey.keystore
                //打包签名apk,然后才能产生微信的登录
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                authorize(wechat);
            }
            break;
            case R.id.login_sina_icon: {
                //新浪微博
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(sina);
            }
            break;
            case R.id.login_qq_icon: {
                //QQ空间
                Platform qzone = ShareSDK.getPlatform(QZone.NAME);
                authorize(qzone);
            }
            break;
            case R.id.login_back:
                finish();
                break;
        }
    }


    //执行授权,获取用户信息
    //文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }

        plat.setPlatformActionListener(this);
        //关闭SSO授权
//        QQ需要网页授权
//        plat.SSOSetting(true);
//        QQ直接授权
        plat.SSOSetting(false);
        plat.showUser(null);
    }


    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
        if (action == Platform.ACTION_USER_INFOR) {
            Message msg = new Message();
            msg.what = MSG_AUTH_COMPLETE;
            msg.obj = new Object[]{platform.getName(), res};
            handler.sendMessage(msg);
        }
    }

    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_ERROR);
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTH_CANCEL: {
                //取消授权
                Toast.makeText(activity, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_ERROR: {
                //授权失败
                Toast.makeText(activity, R.string.auth_error, Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_AUTH_COMPLETE: {
                //授权成功
                Toast.makeText(activity, R.string.auth_complete, Toast.LENGTH_SHORT).show();
                Object[] objs = (Object[]) msg.obj;
                String platform = (String) objs[0];
                HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                if (signupListener != null && signupListener.onSignin(platform, res)) {
                    Platform platformInfo = ShareSDK.getPlatform(platform);
                    SP.put(context, SP.USER_DATA_USERNAME, platformInfo.getDb().getUserName());
                    SP.put(context, SP.USER_DATA_USERID, platformInfo.getDb().getUserId());
                    SP.put(context, SP.USER_DATA_USERICONURL, platformInfo.getDb().getUserIcon());
                    SP.put(context, SP.USER_DATA_LOGINED, true);
//                    PersonalFragment.isLoginListener

                    L.d("用户姓名", platformInfo.getDb().getUserName());
                    L.d("用户的id", platformInfo.getDb().getUserId());
                    L.d("用户的icon", platformInfo.getDb().getUserIcon());

//                    SignupPage signupPage = new SignupPage();
//                    signupPage.setOnLoginListener(signupListener);
//                    signupPage.setPlatform(platform);
//                    signupPage.show(activity, null);
                    finish();
                }
            }
            break;
        }
        return false;
    }

    public void show(Context context) {
        initSDK(context);
        super.show(context, null);
    }

    private void initSDK(Context context) {
        //初始化sharesdk,具体集成步骤请看文档：
        //http://wiki.mob.com/Android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
        ShareSDK.initSDK(context);

    }
}
