package com.cj.dreams.video.Test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.BaseNoActionbarActivity;
import com.cj.dreams.video.thirdpartylogin.OnLoginListener;
import com.cj.dreams.video.thirdpartylogin.UserInfo;
import com.mob.tools.utils.UIHandler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;


public class TestLoginActivity extends BaseNoActionbarActivity {
    private TextView test_user_name,test_user_note;
    private ImageView test_user_icon;
    private OnLoginListener signupListener;
    private Platform platform;
    private String picturePath;
    private UserInfo userInfo = new UserInfo();
    /** 设置授权回调，用于判断是否进入注册 */
    public void setOnLoginListener(OnLoginListener l) {
        this.signupListener = l;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);
        initView();
        initData();
    }

    public void setPlatform(String platName) {
        platform = ShareSDK.getPlatform(platName);
    }

    private void initView(){
        test_user_icon=(ImageView)findViewById(R.id.test_user_icon);
        test_user_name=(TextView)findViewById(R.id.test_user_name);
        test_user_note=(TextView)findViewById(R.id.test_user_note);
    }

    private void initData(){
        if(platform != null){
            userInfo.setUserIcon(platform.getDb().getUserIcon());
            userInfo.setUserName(platform.getDb().getUserName());
            userInfo.setUserNote(platform.getDb().getUserId());
        }
        test_user_name.setText(userInfo.getUserName());
        test_user_note.setText("USER ID : " + userInfo.getUserNote());
        if(!TextUtils.isEmpty(userInfo.getUserIcon())){
            loadIcon();
        }

    }

    /**
     * 加载头像
     */
    public void loadIcon() {
        final String imageUrl = platform.getDb().getUserIcon();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL picUrl = new URL(imageUrl);
                    Bitmap userIcon = BitmapFactory.decodeStream(picUrl.openStream());
                    FileOutputStream b = null;
                    try {
                        b = new FileOutputStream(picturePath);
                        userIcon.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            b.flush();
                            b.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    userInfo.setUserIcon(picturePath);

                    Message msg = new Message();
//                    msg.what = LOAD_USER_ICON;
//                    UIHandler.sendMessage(msg, TestLoginActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_login, menu);
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
