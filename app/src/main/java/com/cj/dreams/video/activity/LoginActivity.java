package com.cj.dreams.video.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface.OnClickListener;
import com.cj.dreams.video.R;
import com.cj.dreams.video.thirdpartylogin.OnLoginListener;
import com.cj.dreams.video.thirdpartylogin.ThirdPartyLogin;
import com.cj.dreams.video.thirdpartylogin.UserInfo;

import java.util.HashMap;

public class LoginActivity extends BaseNoActionbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 显示提示
//        Builder builder = new Builder(LoginActivity.this);
//        builder.setTitle(R.string.if_register_needed);
//        builder.setMessage(R.string.after_auth);
//        builder.setPositiveButton(R.string.tpl_ok, new OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
                showDemo();
                finish();
//            }
//        });
//        builder.create().show();
    }

    private void showDemo() {
        ThirdPartyLogin tpl = new ThirdPartyLogin(this);
        tpl.setOnLoginListener(new OnLoginListener() {
            public boolean onSignin(String platform, HashMap<String, Object> res) {
                // 在这个方法填写尝试的代码，返回true表示还不能登录，需要注册
                // 此处全部给回需要注册
                return true;
            }

            public boolean onSignUp(UserInfo info) {
                // 填写处理注册信息的代码，返回true表示数据合法，注册页面可以关闭
                return true;
            }
        });
        tpl.show(this);
    }

    private void initView() {
    }

    private void initData() {

    }




    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
