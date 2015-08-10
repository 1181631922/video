package com.cj.dreams.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Created by fanyafeng on 2015/07/31/0031.
 */
public class BaseDialog extends Dialog implements View.OnClickListener {
    protected String mobileState = "mobileState", wifiState = "wifiState", noState = "noState";
    protected String guideActivity="guideActivity",appStartActivity="appStartActivity",mainActivity="mainActivity";
    protected String newVersion="发现新版本",nowVersion="已经是最新版本",nowText="当前已是最新版本";

    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void create() {
        super.create();
    }

    @Override
    public void onClick(View v) {

    }
}
