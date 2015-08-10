package com.cj.dreams.video.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cj.dreams.video.ABaseActivity;
import com.cj.dreams.video.R;


/**
 * 自定义actionbar的父类
 */
public class BaseCustomActivity extends ABaseActivity {
    protected boolean isShowShare = false;
    protected String title;
    public TextView base_custom_titlebar_title;
    public ImageButton base_custom_titlebar_back;
    public ImageButton base_custom_titlebar_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        base_custom_titlebar_back = (ImageButton) findViewById(R.id.base_custom_titlebar_back);
        base_custom_titlebar_share = (ImageButton) findViewById(R.id.base_custom_titlebar_share);
        base_custom_titlebar_title = (TextView) findViewById(R.id.base_custom_titlebar_title);

        if (base_custom_titlebar_back != null) {
            base_custom_titlebar_back.setVisibility(View.VISIBLE);
            base_custom_titlebar_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
//                    onBackPressed();
                    finish();
                }
            });
        }

        if (base_custom_titlebar_share != null) {
            if (isShowShare) {
                base_custom_titlebar_share.setVisibility(View.VISIBLE);
                base_custom_titlebar_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        onShare();
                    }
                });
            } else {
                base_custom_titlebar_share.setVisibility(View.GONE);
            }
        }
        if (title != null && !title.equals("")) {
            setTitle(title);
        }
    }

    /**
     * 为子类的分享提供重写方法
     */
    protected void onShare() {

    }

    @Override
    public void setTitle(CharSequence title) {
        if (base_custom_titlebar_title != null)
            base_custom_titlebar_title.setText(title);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
