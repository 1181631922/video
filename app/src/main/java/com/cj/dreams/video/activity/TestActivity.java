package com.cj.dreams.video.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.gesture.GestureStroke;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.fragment.EvaluateFragment;
import com.cj.dreams.video.fragment.RecommendFragment;
import com.cj.dreams.video.util.CommonUtil;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.T;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends FragmentActivity implements OnClickListener, GestureDetector.OnGestureListener {
    private ViewPager test_viewpager;
    private List<Fragment> fragmentList;
    private int selectedColor, unSelectedColor;
    private Drawable selectedButton, unSelectedButton;
    private View RecommendView, EvaluateView;
    private TextView test_recommend, test_evaluate;
    private ImageView test_recommend_img, test_evaluate_img;
    private Button button, delete;
    private RelativeLayout webView;
    private LinearLayout bottom_linearlayout, linearlayout_01;
    private int count;
    private double firClick, secClick;
    private GestureDetector gestureDetector;

    private final static int MIN_MOVE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        gestureDetector = new GestureDetector(this, this);
        initView();
        initData();
    }

    private void initView() {
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        webView = (RelativeLayout) findViewById(R.id.webView);
//        webView.setOnTouchListener(new onDoubleClick());

        bottom_linearlayout = (LinearLayout) findViewById(R.id.bottom_linearlayout);
        linearlayout_01 = (LinearLayout) findViewById(R.id.linearlayout_01);

        unSelectedColor = getResources().getColor(R.color.black);
        selectedColor = getResources().getColor(R.color.red);
        selectedButton = getResources().getDrawable(R.drawable.video_playing_normal);
        unSelectedButton = getResources().getDrawable(R.drawable.video_playing_pressed);

        test_recommend_img = (ImageView) findViewById(R.id.test_recommend_img);
        test_evaluate_img = (ImageView) findViewById(R.id.test_evaluate_img);
        test_recommend = (TextView) findViewById(R.id.test_recommend);
        test_evaluate = (TextView) findViewById(R.id.test_evaluate);
        test_recommend.setOnClickListener(new MyOnClickListener(0));
        test_evaluate.setOnClickListener(new MyOnClickListener(1));
        initTextView();
        initViewPager();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        L.d("onDown周期");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        L.d("onShowPress周期");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        L.d("onSingleTapUp周期");
        return false;

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        L.d("onScroll周期");
        double x1 = e1.getX();
        double x2 = e2.getX();
        L.d(x2 - x1 + "");


        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        L.d("onLongPress周期");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        L.d("onFling周期");
//        if (e1.getY() - e2.getY() > MIN_MOVE) {
//            startActivity(new Intent(TestActivity.this, MainActivity.class));
//            T.showLong(this, "通过手势启动Activity");
//        } else if (e1.getY() - e2.getY() < MIN_MOVE) {
//            finish();
//            T.showLong(this, "通过手势关闭Activity");
//        }
        return true;
    }

    class onDoubleClick implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x;
            int y;
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                count++;
                if (count == 1) {
                    firClick = System.currentTimeMillis();

                } else if (count == 2) {
                    secClick = System.currentTimeMillis();
                    if (secClick - firClick < 1000) {
                        //双击事件
                        hengpingxianshi();
                    }
                    count = 0;
                    firClick = 0;
                    secClick = 0;

                }
            }
            return true;
        }

    }

    private void initTextView() {
        test_recommend.setTextColor(selectedColor);
//        test_recommend_img.setBackground(selectedButton);
        test_recommend_img.setBackgroundDrawable(selectedButton);
        test_evaluate.setTextColor(unSelectedColor);
//        test_evaluate_img.setBackground(unSelectedButton);
        test_evaluate_img.setBackgroundDrawable(unSelectedButton);
    }

    private void initData() {
        L.d("图片的保存路径", CommonUtil.getRootFilePath() + "fanyafeng/files/");
    }

    private void initViewPager() {
        test_viewpager = (ViewPager) findViewById(R.id.test_viewpager);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new RecommendFragment());
        fragmentList.add(new EvaluateFragment());
        test_viewpager.setAdapter(new OurPagerAdapter(getSupportFragmentManager(), fragmentList));
        test_viewpager.setCurrentItem(0);
        test_viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                bottom_linearlayout.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
                break;
            case R.id.delete:
                hengpingxianshi();
                break;
        }
    }

    private void hengpingxianshi() {
        if (CommonUtil.isScreenOriatationPortrait(TestActivity.this)) {// 当屏幕是竖屏时
            // 点击后变横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置当前activity为横屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // 当横屏时 把除了视频以外的都隐藏
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            //隐藏其他组件的代码
            bottom_linearlayout.setVisibility(View.GONE);
            linearlayout_01.setVisibility(View.GONE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
            //显示其他组件
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            bottom_linearlayout.setVisibility(View.VISIBLE);
            linearlayout_01.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 定义适配器
     */
    class OurPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;
        private FragmentManager fragmentManager;

        public OurPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        /**
         * 得到每个页面
         */
        @Override
        public Fragment getItem(int arg0) {
            return (fragmentList == null || fragmentList.size() == 0) ? null
                    : fragmentList.get(arg0);
        }

        public void setFragments(ArrayList<Fragment> fragments) {
            if (this.fragmentList != null) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                for (Fragment f : this.fragmentList) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fragmentManager.executePendingTransactions();
            }
            this.fragmentList = fragments;
            notifyDataSetChanged();
        }

        /**
         * 每个页面的title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        /**
         * 页面的总个数
         */
        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int index) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int index) {
            switch (index) {
                case 0:
                    test_recommend.setTextColor(selectedColor);
//                    test_recommend_img.setBackground(selectedButton);
                    test_recommend_img.setBackgroundDrawable(selectedButton);
                    test_evaluate.setTextColor(unSelectedColor);
//                    test_evaluate_img.setBackground(unSelectedButton);
                    test_evaluate_img.setBackgroundDrawable(unSelectedButton);
                    break;
                case 1:
                    test_recommend.setTextColor(unSelectedColor);
//                    test_recommend_img.setBackground(unSelectedButton);
                    test_recommend_img.setBackgroundDrawable(unSelectedButton);
                    test_evaluate.setTextColor(selectedColor);
//                    test_evaluate_img.setBackground(selectedButton);
                    test_evaluate_img.setBackgroundDrawable(selectedButton);
                    break;
            }
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            test_viewpager.setCurrentItem(index);
        }

    }

    @Override
    protected void onResume() {
//        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onResume();
    }
}
