package com.cj.dreams.video.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.cj.dreams.video.R;
import com.cj.dreams.video.dbhelper.CollectTableCourse;
import com.cj.dreams.video.dbhelper.GoodTableCourse;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;
import com.cj.dreams.video.dboperate.CollectOperate;
import com.cj.dreams.video.dboperate.GoodOperate;
import com.cj.dreams.video.fragment.EvaluateFragment;
import com.cj.dreams.video.fragment.RecommendFragment;
import com.cj.dreams.video.util.CommonUtil;
import FanYaFeng.L;
import FanYaFeng.PostUtil;
import com.cj.dreams.video.util.T;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class VideoViewPlayingActivity extends BaseFragmentActivity implements OnPreparedListener, OnCompletionListener, OnErrorListener, OnInfoListener, OnPlayingBufferCacheListener, OnClickListener, SensorEventListener, GestureDetector.OnGestureListener {
    private final String TAG = "VideoViewPlayingActivity";
    private String mVideoSource = null;
    private ImageButton mPlaybtn = null;
    private ImageButton mPrebtn = null;
    private ImageButton mForwardbtn = null;
    private LinearLayout mController = null;
    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;
    private TextView time_rest;
    private String url_info, id_info, image_info, title_info, good_info, collect_info;
    private String GetRealUrl = "video_api/url_to_m3u8.php";
    private LinearLayout video_down_linearlayout;
    private RelativeLayout root1;
    private int count;
    private double firClick, secClick;
    private SensorManager mSensorManager;
    private Vibrator mVibrator;
    private final int ROCKPOWER = 15;
    private int mLastPos = 0;

    private enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }

    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
    private BVideoView mVV = null;
    private EventHandler mEventHandler;
    private HandlerThread mHandlerThread;
    private final Object SYNC_Playing = new Object();
    private WakeLock mWakeLock = null;
    private static final String POWER_LOCK = "VideoViewPlayingActivity";
    private boolean mIsHwDecode = false;
    //    private  int EVENT_PLAY = 0;
    private final int EVENT_PLAY = 0;
    private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
    private ViewPager video_playing_viewpager;
    private List<Fragment> fragmentList;
    private TextView video_recommend, video_evaluate, video_palying_title, video_view_title;
    private int selectedColor, unSelectedColor, redColor, grayColor;
    private ImageView video_evaluate_view, video_recommend_view;
    private ImageView video_recommend_img, video_evaluate_img;
    private ImageButton full_screen_btn, video_playing_back, video_playing_back_normal;
    private RelativeLayout video_playing_titlebar;
    private RelativeLayout video_playing_back_relativelayout, play_btn_relativelayout;
    private TextView video_play_collect_text, video_play_good_text;
    private ImageView video_play_share, video_play_collect, video_play_good;
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
    private CollectOperate collectOperate;
    private GoodOperate goodOperate;
    private String type;
    private int state = 0;
    private int orientation = ActivityInfo.SCREEN_ORIENTATION_USER;
    private ImageView video_playing_back_normal_iv, video_playing_back_iv;
    private LinearLayout video_share_linearlayout;

    private GestureDetector gestureDetector;
    MyOrientationEventListener myOrientationEventListener;

    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 0:
                    state = 0;
                    T.showLong(VideoViewPlayingActivity.this, "视频解析失败");
                    break;
                case 1:
                    state = 1;
                    mVideoSource = bundle.getString("m3u8");
                    mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
                    mHandlerThread.start();
                    mEventHandler = new EventHandler(mHandlerThread.getLooper());
                    mEventHandler.sendEmptyMessage(EVENT_PLAY);
                    break;
            }
        }
    };

    class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_PLAY:
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        synchronized (SYNC_Playing) {
                            try {
                                SYNC_Playing.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    mVV.setVideoPath(mVideoSource);
                    if (mLastPos > 0) {
                        mVV.seekTo(mLastPos);
                        mLastPos = 0;
                    }
                    mVV.showCacheInfo(true);
                    mVV.start();
                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                    break;
                default:
                    break;
            }
        }
    }

    Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UI_EVENT_UPDATE_CURRPOSITION:
                    int currPosition = mVV.getCurrentPosition();
                    int duration = mVV.getDuration();
                    updateTextViewWithTimeFormat(mCurrPostion, currPosition);
                    updateTextViewWithTimeFormat(mDuration, duration);
                    updateTextViewWithTimeFormat(time_rest, duration - currPosition);
                    mProgress.setMax(duration);
                    mProgress.setProgress(currPosition);
                    mUIHandler.sendEmptyMessageDelayed(UI_EVENT_UPDATE_CURRPOSITION, 200);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_view_playing);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);
        laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(this);
        laughSQLiteOpenHelper.getWritableDatabase();
        gestureDetector = new GestureDetector(this, this);
        myOrientationEventListener = new MyOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
        if (myOrientationEventListener.canDetectOrientation()) {
            myOrientationEventListener.enable();
        } else {
            Toast.makeText(VideoViewPlayingActivity.this, "Can't Detect Orientation!", Toast.LENGTH_LONG).show();
        }

        Configuration configuration = this.getResources().getConfiguration();
        int ori = configuration.orientation;
        initRock();

        Intent intent = this.getIntent();
        url_info = intent.getStringExtra("url_info");
        id_info = intent.getStringExtra("id_info");
        image_info = intent.getStringExtra("image_info");
        title_info = intent.getStringExtra("title_info");
        good_info = intent.getStringExtra("good_info");
        collect_info = intent.getStringExtra("collect_info");
        if (intent.getStringExtra("type_info") != null) {
            type = intent.getStringExtra("type_info");
        }

        mIsHwDecode = getIntent().getBooleanExtra("isHW", false);
        initTextView();
        initUI();
        video_palying_title.setText(intent.getStringExtra("title_info"));
        initViewPager();
        Thread loadThread = new Thread(new LoadMore());
        loadThread.start();

    }

    protected void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
//        oks.setTitle("爆笑女神");
//        oks.setTitle(title_info);
        oks.setTitleUrl(BaseUrl + PostShareId + id_info);
//        oks.setText(title_info + '\n' + "猛戳☞" + BaseUrl + PostShareId + id_info);
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                System.out.println(platform.getName().toString());
                if ("SinaWeibo".equals(platform.getName())) {
                    paramsToShare.setText(title_info + "\r\n" + "猛戳☞" + BaseUrl + PostShareId + id_info);
                } else {
                    paramsToShare.setText(title_info);
                }
                if ("WechatMoments".equals(platform.getName())){
                    paramsToShare.setTitle(title_info);
                }else {
                    paramsToShare.setTitle("爆笑女神");
                }

            }
        });
        oks.setImageUrl(image_info);
        oks.setUrl(BaseUrl + PostShareId + id_info);
        oks.setComment("爆笑女神");
        oks.setInstallUrl(BaseUrl + PostShareId + id_info);
        oks.setSite("爆笑女神");
        oks.setSiteUrl("http://video.ktdsp.com/index.php");
        oks.show(this);
    }


    private void initTextView() {
        video_evaluate_view = (ImageView) findViewById(R.id.video_evaluate_view);
        video_recommend_view = (ImageView) findViewById(R.id.video_recommend_view);
        redColor = getResources().getColor(R.color.red);
        grayColor = getResources().getColor(R.color.white);
        unSelectedColor = getResources().getColor(R.color.videolisttext);
        selectedColor = getResources().getColor(R.color.red);

        video_recommend = (TextView) findViewById(R.id.video_recommend);
        video_evaluate = (TextView) findViewById(R.id.video_evaluate);
        video_recommend_img = (ImageView) findViewById(R.id.video_recommend_img);
        video_evaluate_img = (ImageView) findViewById(R.id.video_evaluate_img);
        video_recommend.setTextColor(selectedColor);
        video_recommend_view.setBackgroundColor(redColor);
        video_evaluate.setTextColor(unSelectedColor);
        video_evaluate_view.setBackgroundColor(grayColor);
        video_recommend.setOnClickListener(new MyOnClickListener(0));
        video_evaluate.setOnClickListener(new MyOnClickListener(1));
    }

    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            video_playing_viewpager.setCurrentItem(index);
        }
    }

    private void initViewPager() {
        video_playing_viewpager = (ViewPager) findViewById(R.id.video_playing_viewpager);
        fragmentList = new ArrayList<Fragment>();
        EvaluateFragment evaluateFragment = new EvaluateFragment();
        Bundle bundle = new Bundle();
        bundle.putString("videoid", id_info);
        evaluateFragment.setArguments(bundle);
        fragmentList.add(new RecommendFragment());
        fragmentList.add(evaluateFragment);
        video_playing_viewpager.setAdapter(new OurPagerAdapter(getSupportFragmentManager(), fragmentList));
        video_playing_viewpager.setCurrentItem(0);
        video_playing_viewpager.setOnPageChangeListener(new VideoListOnPageChangeListener());
    }

    public class VideoListOnPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int index) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int index) {

            switch (index) {
                case 0:
                    video_recommend.setTextColor(selectedColor);
                    video_recommend_view.setBackgroundColor(redColor);
                    video_evaluate.setTextColor(unSelectedColor);
                    video_evaluate_view.setBackgroundColor(grayColor);
                    break;
                case 1:
                    video_recommend.setTextColor(unSelectedColor);
                    video_recommend_view.setBackgroundColor(grayColor);
                    video_evaluate.setTextColor(selectedColor);
                    video_evaluate_view.setBackgroundColor(redColor);
                    break;
            }
        }
    }

    class OurPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;
        private FragmentManager fragmentManager;

        public OurPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

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

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    class LoadMore implements Runnable {
        @Override
        public void run() {
            getRealUrl(url_info);
        }
    }

    private void getRealUrl(String url_info) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("url", url_info);
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetRealUrl, map);
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                Message message = Message.obtain();
                if (jsonObject.getInt("result") == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    Bundle bundle = new Bundle();
                    bundle.putString("m3u8", data.getString("m3u8"));
                    message.setData(bundle);
                    message.what = 1;
                    uiHandler.sendMessage(message);

                } else {
                    message.what = 0;
                    uiHandler.sendMessage(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initUI() {
        video_share_linearlayout=(LinearLayout)findViewById(R.id.video_share_linearlayout);
        video_share_linearlayout.setOnClickListener(this);
        video_playing_back_iv = (ImageView) findViewById(R.id.video_playing_back_iv);
        video_playing_back_iv.setOnClickListener(this);
        video_playing_back_normal_iv = (ImageView) findViewById(R.id.video_playing_back_normal_iv);
        video_playing_back_normal_iv.setOnClickListener(this);

        video_view_title = (TextView) findViewById(R.id.video_view_title);
        root1 = (RelativeLayout) findViewById(R.id.root1);
        root1.setOnTouchListener(new onDoubleClick());
        root1.setOnClickListener(this);
        video_down_linearlayout = (LinearLayout) findViewById(R.id.video_down_linearlayout);
        video_play_collect = (ImageView) findViewById(R.id.video_play_collect);
        if (new CollectTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchCollect(id_info)) {
            video_play_collect.setImageResource(R.drawable.icon_sc_bf);
        } else {
            video_play_collect.setImageResource(R.drawable.icon_sc_bfh);
        }
        video_play_collect.setOnClickListener(this);
        video_play_good = (ImageView) findViewById(R.id.video_play_good);
        if (new GoodTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchGood(id_info)) {
            video_play_good.setImageResource(R.drawable.icon_zan);
        } else {
            video_play_good.setImageResource(R.drawable.icon_zanh);
        }
        video_play_good.setOnClickListener(this);
        video_play_share = (ImageView) findViewById(R.id.video_play_share);
        video_play_share.setOnClickListener(this);
        video_play_collect_text = (TextView) findViewById(R.id.video_play_collect_text);
        video_play_collect_text.setText(collect_info);
        video_play_good_text = (TextView) findViewById(R.id.video_play_good_text);
        video_play_good_text.setText(good_info);
        video_playing_back_relativelayout = (RelativeLayout) findViewById(R.id.video_playing_back_relativelayout);
        video_playing_back_relativelayout.setVisibility(View.GONE);
        video_playing_back_relativelayout.setOnClickListener(this);
        video_palying_title = (TextView) findViewById(R.id.video_palying_title);
        full_screen_btn = (ImageButton) findViewById(R.id.full_screen_btn);
        full_screen_btn.setOnClickListener(this);
        video_playing_titlebar = (RelativeLayout) findViewById(R.id.video_playing_titlebar);
        video_playing_titlebar.bringToFront();
        video_playing_titlebar.setOnClickListener(this);
        video_playing_back = (ImageButton) findViewById(R.id.video_playing_back);
        video_playing_back.bringToFront();
        video_playing_back.setOnClickListener(this);
        video_playing_back_normal = (ImageButton) findViewById(R.id.video_playing_back_normal);
        video_playing_back_normal.bringToFront();
        video_playing_back_normal.setOnClickListener(this);

        mPlaybtn = (ImageButton) findViewById(R.id.play_btn);
        play_btn_relativelayout = (RelativeLayout) findViewById(R.id.play_btn_relativelayout);

        mPrebtn = (ImageButton) findViewById(R.id.pre_btn);
        mForwardbtn = (ImageButton) findViewById(R.id.next_btn);
        mController = (LinearLayout) findViewById(R.id.controlbar);
        mProgress = (SeekBar) findViewById(R.id.media_progress);
        mDuration = (TextView) findViewById(R.id.time_total);
        mCurrPostion = (TextView) findViewById(R.id.time_current);
        time_rest = (TextView) findViewById(R.id.time_rest);

        if (type != null && type.equals("notnone")) {
            video_play_collect.setVisibility(View.GONE);
            video_play_collect_text.setVisibility(View.GONE);
            video_play_good.setVisibility(View.GONE);
            video_play_good_text.setVisibility(View.GONE);
        }

        registerCallbackForControl();
        BVideoView.setAKSK("j5wt2l88mbhlO2qcUONvfI1j", "aQ6WG4w2KMIF37hr");
        mVV = (BVideoView) findViewById(R.id.video_view);
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);
        mVV.setDecodeMode(mIsHwDecode ? BVideoView.DECODE_HW : BVideoView.DECODE_SW);
    }


    private void registerCallbackForControl() {
        mPlaybtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if (mVV.isPlaying()) {
                    mPlaybtn.setImageResource(R.drawable.play_btn_style);
                    mPlaybtn.setAdjustViewBounds(true);
                    mPlaybtn.setMaxHeight(68);
                    mPlaybtn.setMaxWidth(68);
                    mVV.pause();
                } else {
                    mPlaybtn.setImageResource(R.drawable.pause_btn_style);
                    mPlaybtn.setAdjustViewBounds(true);
                    mPlaybtn.setMaxHeight(68);
                    mPlaybtn.setMaxWidth(68);
                    mVV.resume();
                }
//                if (mLastPos!=0){
//                    mVV.seekTo(mLastPos);
//                }
            }
        });

        play_btn_relativelayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if (mVV.isPlaying()) {
                    mPlaybtn.setImageResource(R.drawable.play_btn_style);
                    mPlaybtn.setAdjustViewBounds(true);
                    mPlaybtn.setMaxHeight(48);
                    mPlaybtn.setMaxWidth(48);
                    mVV.pause();
                } else {
                    mPlaybtn.setImageResource(R.drawable.pause_btn_style);
                    mPlaybtn.setAdjustViewBounds(true);
                    mPlaybtn.setMaxHeight(48);
                    mPlaybtn.setMaxWidth(48);
                    mVV.resume();
                }
            }

        });
        mPrebtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                    mVV.stopPlayback();
                }
                if (mEventHandler.hasMessages(EVENT_PLAY))
                    mEventHandler.removeMessages(EVENT_PLAY);
                mEventHandler.sendEmptyMessage(EVENT_PLAY);
            }
        });
        mForwardbtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

            }
        });
        OnSeekBarChangeListener osbc1 = new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTextViewWithTimeFormat(mCurrPostion, progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                int iseekPos = seekBar.getProgress();
                mVV.seekTo(iseekPos);
                mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
            }
        };
        mProgress.setOnSeekBarChangeListener(osbc1);
    }

    private void updateTextViewWithTimeFormat(TextView view, int second) {
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        view.setText(strTemp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root1:
                break;
            case R.id.full_screen_btn:
                fullScreen();
                break;
            case R.id.video_play_share:
                showShare();
                break;
            case R.id.video_playing_back:
//                if (CommonUtil.isScreenOriatationPortrait(VideoViewPlayingActivity.this)) {
                finish();
//                } else {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    video_down_linearlayout.setVisibility(View.VISIBLE);
//                }
                break;
            case R.id.video_playing_titlebar:
                if (CommonUtil.isScreenOriatationPortrait(VideoViewPlayingActivity.this)) {
                    finish();
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    video_down_linearlayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.video_playing_back_relativelayout:
                break;
            case R.id.video_playing_back_normal:
                finish();
                break;
            case R.id.video_play_collect:
                Thread loadThread = new Thread(new LoadThread(id_info, "collect"));
                loadThread.start();
                if (new CollectTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchCollect(id_info)) {
                    collectOperate = new CollectOperate(laughSQLiteOpenHelper.getReadableDatabase());
                    collectOperate.insert(id_info, image_info, title_info, url_info);
                    video_play_collect.setImageResource(R.drawable.icon_sc_bfh);
                } else {
                    collectOperate = new CollectOperate(laughSQLiteOpenHelper.getReadableDatabase());
                    collectOperate.delete(id_info);
                    video_play_collect.setImageResource(R.drawable.icon_sc_bf);
                }
                break;
            case R.id.video_play_good:
                Thread loadThread1 = new Thread(new LoadThread(id_info, "praise"));
                loadThread1.start();
                if (new GoodTableCourse(laughSQLiteOpenHelper.getReadableDatabase()).searchGood(id_info)) {
                    goodOperate = new GoodOperate(laughSQLiteOpenHelper.getReadableDatabase());
                    goodOperate.insert(id_info);
                    video_play_good.setImageResource(R.drawable.icon_zanh);
                } else {
                    goodOperate = new GoodOperate(laughSQLiteOpenHelper.getReadableDatabase());
                    goodOperate.delete(id_info);
                    video_play_good.setImageResource(R.drawable.icon_zan);
                }
                break;
            case R.id.video_playing_back_normal_iv:
                finish();
                break;
            case R.id.video_playing_back_iv:
                finish();
                break;
            case R.id.video_share_linearlayout:
                showShare();
                break;
        }
    }

    private void fullScreen() {
        if (CommonUtil.isScreenOriatationPortrait(VideoViewPlayingActivity.this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            video_down_linearlayout.setVisibility(View.GONE);
            video_playing_back_relativelayout.setVisibility(View.VISIBLE);
            video_playing_back_relativelayout.bringToFront();
            video_playing_back.bringToFront();
            video_view_title.setVisibility(View.VISIBLE);
            video_view_title.setText(title_info);
            video_view_title.bringToFront();
            video_playing_back.setVisibility(View.VISIBLE);
            video_playing_back_normal.setVisibility(View.GONE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            video_playing_back.bringToFront();
            video_down_linearlayout.setVisibility(View.VISIBLE);
            video_playing_back_relativelayout.setVisibility(View.GONE);
            video_view_title.setVisibility(View.GONE);
            video_playing_back.setVisibility(View.VISIBLE);
            video_playing_back_normal.setVisibility(View.VISIBLE);
        }
    }

    class LoadThread implements Runnable {
        private String id;
        private String type;

        LoadThread(String id, String type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public void run() {
            loadData(id, type);
        }
    }

    private void loadData(String videoId, String buttonType) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("videoid", videoId);
        map.put("type", buttonType);
        try {
            String backMsg = PostUtil.postData(BaseUrl + PostVideoInfo, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
            mLastPos = mVV.getCurrentPosition();
            mVV.stopPlayback();
        }

        mVV.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d("onResume生命周期");
        if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }

        if (mSensorManager != null) {
            mSensorManager.registerListener(sensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }

        getConfigurationInfo();


        if (mVideoSource != null) {
            mEventHandler.sendEmptyMessage(EVENT_PLAY);
        }
    }

    private void getConfigurationInfo() {
        Configuration configuration = getResources().getConfiguration();
        int l = configuration.ORIENTATION_LANDSCAPE;
        int p = configuration.ORIENTATION_PORTRAIT;
        if (configuration.orientation == l) {
            System.out.println("现在是横屏");
        }
        if (configuration.orientation == p) {
            System.out.println("现在是竖屏");
        }

    }


    private long mTouchTime;
    private boolean barShow = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            mTouchTime = System.currentTimeMillis();
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            long time = System.currentTimeMillis() - mTouchTime;
            if (time < 400) {
                updateControlBar(!barShow);
            }
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        double x1 = e1.getX();
        double x2 = e2.getX();
        if (x2 - x1 > 0) {

        } else {

        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    class onDoubleClick implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                count++;
                if (count == 1) {
                    firClick = System.currentTimeMillis();
                } else if (count == 2) {
                    secClick = System.currentTimeMillis();
                    if (secClick - firClick < 1000) {
                        fullScreen();
                    }
                    count = 0;
                    firClick = 0;
                    secClick = 0;
                }
            }
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                updateControlBar(!barShow);
            }

            return true;
        }

    }

    public void updateControlBar(boolean show) {

        if (show) {
            mController.setVisibility(View.VISIBLE);
            if (CommonUtil.isScreenOriatationPortrait(VideoViewPlayingActivity.this)) {
                video_playing_back_relativelayout.setVisibility(View.VISIBLE);
            } else {
                video_playing_back_relativelayout.setVisibility(View.INVISIBLE);
            }
        } else {
            mController.setVisibility(View.INVISIBLE);
            if (CommonUtil.isScreenOriatationPortrait(VideoViewPlayingActivity.this)) {
                video_playing_back_relativelayout.setVisibility(View.INVISIBLE);
            } else {
                video_playing_back_relativelayout.setVisibility(View.VISIBLE);
            }
        }
        barShow = show;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(sensorEventListener);
        }

        mVV.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //有bug
        if (!CheckNetworkState().equals(noState)) {
            mHandlerThread.quit();
            myOrientationEventListener.disable();
        }
    }

    @Override
    public boolean onInfo(int what, int extra) {
        switch (what) {
            case BVideoView.MEDIA_INFO_BUFFERING_START:
                break;
            case BVideoView.MEDIA_INFO_BUFFERING_END:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onPlayingBufferCache(int percent) {

    }

    @Override
    public boolean onError(int what, int extra) {
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
        return true;
    }

    @Override
    public void onCompletion() {
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
    }

    @Override
    public void onPrepared() {
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initRock() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            int medumValue = 10;
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                Message msg = new Message();
                msg.what = ROCKPOWER;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ROCKPOWER:
                    break;
            }
        }

    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeScreen(newConfig);
    }

    private void changeScreen(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            video_playing_back.bringToFront();
            video_down_linearlayout.setVisibility(View.VISIBLE);
            video_playing_back_relativelayout.setVisibility(View.GONE);
            video_view_title.setVisibility(View.GONE);
            video_playing_back.setVisibility(View.VISIBLE);
            video_playing_back_normal.setVisibility(View.VISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            video_playing_back.bringToFront();
            video_down_linearlayout.setVisibility(View.GONE);
            video_playing_back_relativelayout.setVisibility(View.VISIBLE);
            video_playing_back_relativelayout.bringToFront();
            video_view_title.setVisibility(View.VISIBLE);
            video_view_title.setText(title_info);
            video_view_title.bringToFront();
            video_playing_back.setVisibility(View.VISIBLE);
            video_playing_back_normal.setVisibility(View.GONE);
        }
    }

    class MyOrientationEventListener extends OrientationEventListener {

        public MyOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int arg0) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }
            orientation = ActivityInfo.SCREEN_ORIENTATION_USER;
            VideoViewPlayingActivity.this.setRequestedOrientation(orientation);
            Display display = getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            if (width > height) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            } else {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
        }
    }


}
