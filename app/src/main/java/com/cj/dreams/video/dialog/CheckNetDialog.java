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
import com.cj.dreams.video.activity.AppStartActivity;
import com.cj.dreams.video.activity.GuideActivity;
import com.cj.dreams.video.activity.MainActivity;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class CheckNetDialog extends BaseDialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    String title;
    String detail;
    String state;
    String id;
    private Button confirmBtn;
    private Button cancelBtn;
    private TextView dialog_exit_title;
    private TextView dialog_exit_detail;

    public CheckNetDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * @param context
     * @param resLayout
     */
    public CheckNetDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param resLayout
     */
    public CheckNetDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }

    public CheckNetDialog(Context context, int theme, int layoutRes, String title, String detail, String state, String id) {
        super(context, theme);
        this.layoutRes = layoutRes;
        this.context = context;
        this.title = title;
        this.detail = detail;
        this.state = state;
        this.id = id;
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
                CheckNetDialog.this.dismiss();
                break;
            case R.id.cancel_btn:
                if (state.equals(noState) && id.equals(guideActivity)) {
                    Intent it_my_activity = new Intent(Intent.ACTION_CALL);
                    it_my_activity.setClass(context, GuideActivity.class);
                    ((Activity) context).finish();
                }
                if (state.equals(noState) && id.equals(mainActivity)) {
                    Intent it_my_activity = new Intent(Intent.ACTION_CALL);
                    it_my_activity.setClass(context, MainActivity.class);
                    ((Activity) context).finish();
                }
                CheckNetDialog.this.dismiss();
                break;
        }
    }

}