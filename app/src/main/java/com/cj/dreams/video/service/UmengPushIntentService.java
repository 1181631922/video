package com.cj.dreams.video.service;

import android.content.Context;
import android.content.Intent;

import com.umeng.common.message.Log;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

/**
 * Created by fanyafeng on 2015/7/22/0022.
 */
public class UmengPushIntentService extends UmengBaseIntentService {
    private static final String TAG = UmengPushIntentService.class.getName();

    @Override
    protected void onMessage(Context context, Intent intent) {
        super.onMessage(context, intent);
        try {
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            Log.d(TAG, "message=" + message);
            Log.d(TAG, "custom="+msg.custom);
            // code  to handle message here
            // ...
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
