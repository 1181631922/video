package com.cj.dreams.video.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.dreams.video.R;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.PostUtil;
import com.cj.dreams.video.util.T;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class VideoDisplayActivity extends BaseNoActionbarActivity {

    private Button bt_play;
    private Button bt_pause;
    private Button bt_stop;
    private int currentPosition;

    private Button bt_replay;
    private SurfaceView sv_vedio;
    private MediaPlayer mediaplayer;
    private SeekBar sb_progress;
    private boolean flag;
    private TextView tv_time;
    private int time;
    private Handler handler;
    private String url_info, m3u8;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);
        Intent intent = this.getIntent();
        url_info = intent.getStringExtra("url_info");
        L.d("得到的url播放地址", url_info);

        initView();
        initData();

        bt_play = (Button) findViewById(R.id.bt_play);
        bt_pause = (Button) findViewById(R.id.bt_pause);
        bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_replay = (Button) findViewById(R.id.bt_replay);
        bt_replay = (Button) findViewById(R.id.bt_replay);
        tv_time = (TextView) findViewById(R.id.tv_time);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        int a = msg.getData().getInt("1");
                        int b = msg.getData().getInt("2");
                        set(b, a);
                }
            }

            private void set(int progress, int max) {
                tv_time.setText(toTime(progress) + "/" + toTime(max));
            }

            private String toTime(int progress) {
                StringBuffer sb = new StringBuffer();
                int s = (progress / 1000) % 60;
                int m = progress / 60000;
                sb.append(m).append(":");
                if (s < 10) {
                    sb.append(0);
                }
                sb.append((progress / 1000) % 60);
                return sb.toString();
            }
        };
        bt_pause.setEnabled(false);
        bt_stop.setEnabled(false);
        sb_progress = (SeekBar) findViewById(R.id.sb_progress);
        sb_progress.setEnabled(false);
        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaplayer != null) {
                    int progress = seekBar.getProgress();
                    mediaplayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
        sv_vedio = (SurfaceView) findViewById(R.id.sv_video);
        sv_vedio.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        sv_vedio.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                System.out.println("销毁了");
                if (mediaplayer != null && mediaplayer.isPlaying()) {
                    currentPosition = mediaplayer.getCurrentPosition();
                }
                stop();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                System.out.println("创建了");
                if (currentPosition > 0) {
                    play(currentPosition);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                System.out.println("大小改变了");
            }
        });
        bt_play.setOnClickListener(this);
        bt_pause.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_replay.setOnClickListener(this);

    }

    private void initView() {

    }

    private void initData() {
        Thread loadThread = new Thread(new LoadThread());
        loadThread.start();
    }

    class LoadThread implements Runnable {
        @Override
        public void run() {
            getRealUrl();
        }
    }

    private void getRealUrl() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("url", url_info);
        try {
            String backMsg = PostUtil.postData(BaseUrl + GetRealUrl, map);
            L.d(backMsg);
            try {
                JSONObject jsonObject = new JSONObject(backMsg);
                Message message = Message.obtain();
                if (jsonObject.getInt("result") == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String img = data.getString("img");
                    String title = data.getString("title");
                    m3u8 = data.getString("m3u8");
                    Bundle bundle = new Bundle();
                    bundle.putString("img", img);
                    bundle.putString("title", title);
                    bundle.putString("m3u8", m3u8);
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

    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 0:
                    T.showLong(VideoDisplayActivity.this, "视频解析失败");
                    break;
                case 1:
                    bundle.getString("img");
                    L.d(bundle.getString("m3u8"));
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_play:
                play(0);
                break;
            case R.id.bt_pause:
                pause();
                break;
            case R.id.bt_stop:
                stop();
                break;
            case R.id.bt_replay:
                replay();
                break;
        }
    }

    private void replay() {
        if (mediaplayer != null && mediaplayer.isPlaying()) {
            mediaplayer.seekTo(0);
        } else {
            play(0);
        }
    }

    private void stop() {
        if (mediaplayer != null) {
            mediaplayer.stop();
            mediaplayer.release();
            mediaplayer = null;
            bt_play.setEnabled(true);
            bt_pause.setEnabled(false);
            bt_stop.setEnabled(false);
            flag = false;
            bt_pause.setText("暂停");
        } else {
            Toast.makeText(this, "请先播放视频！", 1).show();
        }
    }

    private void pause() {
        if (bt_pause.getText().toString().trim().equals("继续")) {
            mediaplayer.start();
            bt_pause.setText("暂停");
            return;
        } else {
            if (mediaplayer != null && mediaplayer.isPlaying()) {
                mediaplayer.pause();
                bt_pause.setText("继续");
            } else {
                Toast.makeText(this, "请先播放视频！", 1).show();
            }
        }
    }

    private void play(final int currentPosition2) {
        String path = "http://pl.youku.com/playlist/m3u8?ts=1435824360&keyframe=0&vid=XMjcwNjYwNTIw&type=mp4&ep=dSaXH0GPUs0G5SfYjj8bMSXnIXZZXJZ3rEzC%2F4gLR8VAMa%2FQnTbQww%3D%3D&sid=743582436047612f95e1f&token=4814&ctype=12&ev=1&oip=1931268481";

        File file = new File(m3u8);
        L.d(m3u8);

        try {
            mediaplayer = new MediaPlayer();
            mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaplayer.reset();
            mediaplayer.setDisplay(sv_vedio.getHolder());

            mediaplayer.setDataSource(m3u8);
            mediaplayer.prepareAsync();

            bt_play.setEnabled(false);
            bt_pause.setEnabled(true);
            bt_stop.setEnabled(true);
            sb_progress.setEnabled(true);

            mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaplayer.start();
                    final int max = mediaplayer.getDuration();

                    sb_progress.setMax(max);
                    mediaplayer.seekTo(currentPosition2);
                    new Thread() {
                        public void run() {
                            flag = true;
                            while (flag) {
                                int progress = mediaplayer.getCurrentPosition();
                                sb_progress.setProgress(progress);
                                Message message = new Message();

                                Bundle bundle = new Bundle();
                                message.setData(bundle);
                                bundle.putInt("1", max);
                                bundle.putInt("2", progress);
                                message.what = 0;
                                handler.sendMessage(message);

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }.start();
                }
            });
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    bt_play.setEnabled(true);
                }
            });
            mediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    bt_play.setEnabled(true);
                    flag = false;
                    return false;

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "播放失败！", 1).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_display, menu);
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

    @Override
    protected void onDestroy() {
        if (mediaplayer != null) {
            mediaplayer.stop();
            mediaplayer.release();
            mediaplayer = null;
        }
        super.onDestroy();
    }
}
