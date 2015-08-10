package com.cj.dreams.video.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.cj.dreams.video.ABaseActivity;
import com.cj.dreams.video.R;


/**
 */
public class BaseActivity extends ABaseActivity {
    protected String title;
    protected String subtitle;
    protected boolean ishide = false;


    /**
     * getActionBar只能在onCreate进行get
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*左上角icon显示
        getActionBar().setDisplayShowHomeEnabled(false);
        左上角返回键显示
        ActionBar actionBar=getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        isShowIcon();
        isShowBack();
        isShowBackIcon();
        isShowTitle();
        setTitleIcon();
        setTitleBackground();

    }


    /**
     * actionbar为顶部菜单布局
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    /**
     * 每个顶部menu的监听，如果横向添加的话android会自定义下拉编写，不用进行方法的重写实现
     *
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                BaseBack();
                break;
//            case R.id.base_action_setting1:
//                finish();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 为返回监听提供重写的方法
     */
    protected void BaseBack() {
        finish();
    }

    /**
     * 为子activity的icon初始化提供重写方法
     */
    protected void isShowIcon() {
        getActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * 为子activity的back按钮初始化提供重写方法
     */
    protected void isShowBack() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 为子activity的title标题是否显示提供重写方法
     */
    protected void isShowTitle() {
        getActionBar().setDisplayShowTitleEnabled(true);
    }

    /**
     * 为子activity的返回图标是否显示提供重写方法
     */
    protected void isShowBackIcon() {
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * 为子activity的icon图标提供私人定制的重写方法
     */
    protected void setTitleIcon() {
        getActionBar().setIcon(getResources().getDrawable(R.drawable.app_back));
    }

    /**
     * 为子activity的title背景提供私人定制的重写方法
     */
    protected void setTitleBackground() {
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.customthemebackground));
    }

    /**
     * 一级标题
     *
     * @param title
     */
    protected void setTitleContent(String title) {
        getActionBar().setTitle(title);
    }

    /**
     * 二级标题
     *
     * @param subtitle
     */
    protected void setSubtitleContent(String subtitle) {
        getActionBar().setSubtitle(subtitle);
    }

    protected void isActionBaiHide(boolean ishide) {
        getActionBar().hide();
    }

    /**
     * 对重新赋值的字段进行判断
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (title != null && !title.equals("")) {
            setTitleContent(title);
        }
        if (subtitle != null && !subtitle.equals("")) {
            setSubtitleContent(subtitle);
        }
        if (ishide) {
            isActionBaiHide(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
