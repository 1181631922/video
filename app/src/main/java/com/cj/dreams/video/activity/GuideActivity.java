package com.cj.dreams.video.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.cj.dreams.video.R;
import com.cj.dreams.video.app.UmengApplication;
import com.cj.dreams.video.dialog.CheckNetDialog;
import com.cj.dreams.video.dialog.MainExitDialog;
import com.cj.dreams.video.service.UmengPushIntentService;
import FanYaFeng.L;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseNoActionbarActivity implements View.OnTouchListener {

    private ViewPager viewPager;
    private List<View> listView;
    private List<View> listDots;
    private int thePos = 0;
    private int oldPosition;
    private int count = 0;
    private long firstTime = 0;
    private long secondTime = 0;

    private Context context;
    private PushAgent mPushAgent;
    private String guideActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        String device_token = UmengRegistrar.getRegistrationId(this);
        L.d("设备的token", device_token);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        PushAgent.getInstance(this).onAppStart();
        L.d(channelId);
        L.d("----------------------------------------------------");
        AnalyticsConfig.setChannel(channelId);
//        友盟统计
        context = this;
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
        AnalyticsConfig.enableEncrypt(true);
        if (isFirstStart()) {
            setTarget();
            setContentView(R.layout.activity_guide);
            initViewPager();
            initDots();
        } else {
            Intent it = new Intent();
            it.setClass(this, AppStartActivity.class);
            startActivity(it);
            finish();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(context);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     */
    private boolean isFirstStart() {
        SharedPreferences share = getSharedPreferences("fs", MODE_PRIVATE);
        String target = share.getString("isfs", "0");
        if (target.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     */
    private void setTarget() {
        SharedPreferences share = getSharedPreferences("fs", MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putString("isfs", "no");
        editor.commit();
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        listView = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        listView.add(inflater.inflate(R.layout.app_guide_first, null));
        listView.add(inflater.inflate(R.layout.app_guide_second, null));
        listView.add(inflater.inflate(R.layout.app_guide_third, null));
        listView.add(inflater.inflate(R.layout.app_guide_fourth, null));

        for (int i = 0; i < listView.size(); i++) {
            View view = (View) listView.get(i);
            view.setOnTouchListener(this);
        }

        viewPager.setAdapter(new MyPagerAdapter(listView));
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());

    }

    private void initDots() {
        listDots = new ArrayList<View>();
        listDots.add(findViewById(R.id.dot01));
        listDots.add(findViewById(R.id.dot02));
        listDots.add(findViewById(R.id.dot03));
        listDots.add(findViewById(R.id.dot04));
    }

    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        public void onPageSelected(int position) {
            ((View) listDots.get(position))
                    .setBackgroundResource(R.drawable.dot_focused);
            ((View) listDots.get(oldPosition))
                    .setBackgroundResource(R.drawable.dot_normal);
            oldPosition = position;
            thePos = position;
            System.out.println(thePos);
        }

    }

    public class MyPagerAdapter extends PagerAdapter {

        private List<View> list;

        public MyPagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public void destroyItem(View view, int index, Object arg2) {
            // TODO Auto-generated method stub
            ((ViewPager) view).removeView(list.get(index));
        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object instantiateItem(View view, int index) {
            ((ViewPager) view).addView(list.get(index), 0);
            return list.get(index);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            // TODO Auto-generated method stub
            return view == (object);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

    }

    public boolean onTouch(View arg0, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction() && thePos == 3) {
            count++;
            if (count == 1) {
                firstTime = System.currentTimeMillis();
            } else {
                secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 0) {
                    Intent it = new Intent();
                    it.setClass(this, MainActivity.class);
                    startActivity(it);
                    finish();
                }
                count = 0;
                firstTime = 0;
                secondTime = 0;
            }

        }
        return true;
    }
}
