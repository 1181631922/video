package com.cj.dreams.video.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.MainActivity;
import com.cj.dreams.video.util.SP;
import com.cj.dreams.video.util.T;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class TeamTestDialog extends BaseDialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    String title;
    String detail;
    private Button confirmBtn;
    private Button cancelBtn;
    private TextView dialog_exit_title;
    private TextView dialog_exit_detail;


    public TeamTestDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * @param context
     * @param resLayout
     */
    public TeamTestDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param resLayout
     */
    public TeamTestDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        dialog_exit_title = (TextView) findViewById(R.id.dialog_exit_title);
        dialog_exit_detail = (TextView) findViewById(R.id.dialog_exit_detail);
        if (!(boolean) SP.get(context, SP.TeamTestType, false)) {
            dialog_exit_title.setText("通知");
            dialog_exit_detail.setText("是否进入测试模式查看发布的期刊");
        } else {
            dialog_exit_title.setText("通知");
            dialog_exit_detail.setText("是否进入正常模式进行浏览");
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
//                Intent it_my_activity = new Intent(Intent.ACTION_CALL);
//                it_my_activity.setClass(context, MainActivity.class);
//                ((Activity) context).finish();
                if (!(boolean) SP.get(context, SP.TeamTestType, false)) {
                    SP.put(context, SP.TeamTestType, true);
                } else {
                    SP.put(context, SP.TeamTestType, false);
                }
                TeamTestDialog.this.dismiss();
                break;
            case R.id.cancel_btn:

                TeamTestDialog.this.dismiss();
                break;
        }
    }

}