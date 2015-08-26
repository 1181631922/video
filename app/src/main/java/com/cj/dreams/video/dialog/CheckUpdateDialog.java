package com.cj.dreams.video.dialog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.util.HttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class CheckUpdateDialog extends BaseDialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    Boolean isNewVersion;
    String downloadUrl;
    String updateContent;
    String packageName;
    private Button confirmBtn;
    private Button cancelBtn;
    private TextView dialog_exit_title;
    private TextView dialog_exit_detail;
    private int icon_download = android.R.drawable.stat_sys_download;
    private String tickerText = "开始下载爆笑女神";
    private PendingIntent contentIntent;
    private int fileLength;
    private int DownedFileLength = 0;
    private NotificationManager manager;
    private InputStream inputStream;
    private URLConnection connection;
    private OutputStream outputStream;
    private ProgressBar notificationProgress;
    private RemoteViews contentView;
    private float temp;
    Notification mNotification = new Notification(icon_download, tickerText, System.currentTimeMillis());

    /**
     * @param context
     */
    public CheckUpdateDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * @param context
     * @param resLayout
     */
    public CheckUpdateDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param resLayout
     */
    public CheckUpdateDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param layoutRes
     * @param isNewVersion
     * @param downloadUrl
     * @param updateContent
     * @param packageName
     */
    public CheckUpdateDialog(Context context, int theme, int layoutRes, Boolean isNewVersion, String downloadUrl, String updateContent, String packageName) {
        super(context, theme);
        this.layoutRes = layoutRes;
        this.isNewVersion = isNewVersion;
        this.downloadUrl = downloadUrl;
        this.updateContent = updateContent;
        this.packageName = packageName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        manager = (NotificationManager) getContext().getSystemService(context.NOTIFICATION_SERVICE);
        notificationProgress = (ProgressBar) findViewById(R.id.notificationProgress);
        dialog_exit_title = (TextView) findViewById(R.id.dialog_exit_title);
        dialog_exit_title.setText(newVersion);
        dialog_exit_detail = (TextView) findViewById(R.id.dialog_exit_detail);

        if (isNewVersion) {
            String content = updateContent.replace('n', '\n');
//            String content1=content.replace("",);
            dialog_exit_detail.setText(content);
        } else {
            dialog_exit_detail.setText(nowText);
        }

        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        confirmBtn.setTextColor(0xff1E90FF);
        cancelBtn.setTextColor(0xff1E90FF);

        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_btn:
                if (dialog_exit_title.getText().toString().equals(newVersion)) {
                    updateApp();
                }
                CheckUpdateDialog.this.dismiss();
                break;
            case R.id.cancel_btn:
                CheckUpdateDialog.this.dismiss();
                break;
        }
    }

    private void updateApp() {
        DownLoadThread downLoadThread = new DownLoadThread();
        downLoadThread.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Notification mNotification = new Notification(icon_download, tickerText, System.currentTimeMillis());
                    mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                    contentView = new RemoteViews(packageName, R.layout.notification_view_download);
                    contentView.setTextViewText(R.id.notificationTitle, "Download:正在下载中...");
                    // notification显示
                    contentView.setTextViewText(R.id.notificationPercent, "下载中");

                    contentView.setProgressBar(R.id.notificationProgress, fileLength, DownedFileLength, false);
                    mNotification.contentView = contentView;
                    mNotification.contentIntent = contentIntent;
                    manager.notify(1, mNotification);
                    break;

            }
        }
    };

    private class DownLoadThread extends Thread {

        @Override
        public void run() {
            DownFile(downloadUrl);
            installApk(HttpUtil.downloadPath + "/laugh.apk");
        }

        // 安装apk文件
        private void installApk(String filename) {
            File file = new File(filename);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW); // 浏览网页的Action(动作)
            String type = "application/vnd.android.package-archive";
            intent.setDataAndType(Uri.fromFile(file), type); // 设置数据类型
            getContext().startActivity(intent);
        }

    }

    private void DownFile(String durl) {
        try {
            URL url = new URL(durl);
            connection = url.openConnection();
            inputStream = connection.getInputStream();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String downloadDir = Environment.getExternalStorageDirectory().getPath() + "/download";
        File file1 = new File(downloadDir);
        if (!file1.exists()) {
            file1.mkdir();
        }
        String filePath = downloadDir + "/laugh.apk";
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Message message = Message.obtain();
        try {
            outputStream = new FileOutputStream(file);
            fileLength = connection.getContentLength();
            int bufferLen = 1024;
            byte[] buffer = new byte[bufferLen];
            message.what = 0;
            progressHandler.sendMessage(message);
            int count = 0;
            while ((count = inputStream.read(buffer)) != -1) {
                DownedFileLength += count;
//                temp = (float) this.notificationProgress.getProgress() / (float) this.notificationProgress.getMax();
                outputStream.write(buffer, 0, count);
                Message message1 = new Message();
                message1.what = 1;
                progressHandler.sendMessage(message1);
            }
            Message message2 = new Message();
            message2.what = 2;
            progressHandler.sendMessage(message2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                        contentView = new RemoteViews(packageName, R.layout.notification_view_download);
                        contentView.setTextViewText(R.id.notificationTitle, "爆笑女神:开始下载...");
                        contentView.setTextViewText(R.id.notificationPercent, "下载开始");
                        contentView.setProgressBar(R.id.notificationProgress, fileLength, DownedFileLength, false);
                        mNotification.contentView = contentView;
                        mNotification.contentIntent = contentIntent;
                        manager.notify(1, mNotification);
                        break;
                    case 1:
                        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                        contentView = new RemoteViews(packageName, R.layout.notification_view_download);
                        contentView.setTextViewText(R.id.notificationTitle, "爆笑女神:正在下载中...");
                        // notification显示
                        contentView.setTextViewText(R.id.notificationPercent, "下载中");

                        contentView.setProgressBar(R.id.notificationProgress, fileLength, DownedFileLength, false);
                        mNotification.contentView = contentView;
                        mNotification.contentIntent = contentIntent;
                        manager.notify(1, mNotification);
                        break;
                    case 2:
                        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                        contentView = new RemoteViews(packageName, R.layout.notification_view_download);
                        contentView.setTextViewText(R.id.notificationTitle, "Download:爆笑女神安装包下载完成");
                        // notification显示
                        contentView.setTextViewText(R.id.notificationPercent, "");
                        contentView.setImageViewResource(R.id.notificationImage, R.drawable.my_ic_launcher);
                        mNotification.icon = R.drawable.my_ic_launcher;
                        contentView.setProgressBar(R.id.notificationProgress, fileLength, DownedFileLength, false);
                        mNotification.contentView = contentView;
                        mNotification.contentIntent = contentIntent;
                        manager.notify(1, mNotification);
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }
        }
    };

}