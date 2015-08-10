package com.cj.dreams.video.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.MainActivity;
import com.cj.dreams.video.util.StringTools;
import com.cj.dreams.video.util.T;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class CheckTheNetDialog extends BaseDialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    String title;
    String detail;
    String state;
    private Button confirmBtn;
    private Button cancelBtn;
    private TextView dialog_exit_title;
    private TextView dialog_exit_detail;

    public CheckTheNetDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * @param context
     * @param resLayout
     */
    public CheckTheNetDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param resLayout
     */
    public CheckTheNetDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }

    public CheckTheNetDialog(Context context, int theme, int layoutRes, String title, String detail, String state) {
        super(context, theme);
        this.layoutRes = layoutRes;
        this.context = context;
        this.title = title;
        this.detail = detail;
        this.state = state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        dialog_exit_title = (TextView) findViewById(R.id.dialog_exit_title);
        dialog_exit_title.setText(title);
        dialog_exit_detail = (TextView) findViewById(R.id.dialog_exit_detail);
        dialog_exit_detail.setText(detail);
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
                if (state.equals(noState)) {
                    Intent it_my_activity = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    context.startActivity(it_my_activity);
                }
                if (state.equals(mobileState)) {
                    T.showLong(context, "您将使用流量进行浏览！！！");
                }
                CheckTheNetDialog.this.dismiss();
                break;
            case R.id.cancel_btn:
                CheckTheNetDialog.this.dismiss();
                break;
        }
    }

}