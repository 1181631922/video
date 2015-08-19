package com.cj.dreams.video.app;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.GetBackViewPlayingActivity;
import com.cj.dreams.video.activity.TestActivity;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by fanyafeng on 2015/7/22/0022.
 */
public class UmengApplication extends BaseApplication {
    private static final String TAG = UmengApplication.class.getName();

    private PushAgent mPushAgent;

    public static final String CALLBACK_RECEIVER_ACTION = "callback_receiver_action";

    public static IUmengRegisterCallback mRegisterCallback;

    public static IUmengUnregisterCallback mUnregisterCallback;

    @Override
    public void onCreate() {
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        mPushAgent.getMessageHandler();

        /**
         * 该Handler是在IntentService中被调用，故
         * 1. 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 2. IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
         * 	      或者可以直接启动Service
         * */
        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }


            @Override
            public Notification getNotification(Context context,UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.app_notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView);
                        builder.setAutoCancel(true);
                        Notification mNotification = builder.build();
                        //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                        mNotification.contentView = myNotificationView;
                        return mNotification;
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);




        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
//                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//            }

            @Override
            public void openActivity(Context context, UMessage uMessage) {
                super.openActivity(context, uMessage);
                Intent i = new Intent(context, GetBackViewPlayingActivity.class);  //自定义打开的界面
//                Intent i = new Intent(context, TestActivity.class);  //自定义打开的界面
//                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        mRegisterCallback = new IUmengRegisterCallback() {

            @Override
            public void onRegistered(String registrationId) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
                sendBroadcast(intent);

            }

        };
        mPushAgent.setRegisterCallback(mRegisterCallback);

        mUnregisterCallback = new IUmengUnregisterCallback() {

            @Override
            public void onUnregistered(String registrationId) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
                sendBroadcast(intent);
            }
        };
        mPushAgent.setUnregisterCallback(mUnregisterCallback);
    }
}
