package com.cj.dreams.video.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.MainActivity;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class GradeDialog extends Dialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    private RelativeLayout dialog_out;
    private Button main_tab_retroaction;
    private Button main_tab_bottom;
    private Button main_tab_exit;
    private Button main_tab_cancel;


    public GradeDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * @param context
     * @param resLayout
     */
    public GradeDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param resLayout
     */
    public GradeDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        dialog_out = (RelativeLayout) findViewById(R.id.dialog_out);
        main_tab_retroaction = (Button) findViewById(R.id.main_tab_retroaction);
        main_tab_bottom = (Button) findViewById(R.id.main_tab_bottom);
        main_tab_exit = (Button) findViewById(R.id.main_tab_exit);
        main_tab_cancel = (Button) findViewById(R.id.main_tab_cancel);

        main_tab_retroaction.setTextColor(0xff1E90FF);
        main_tab_bottom.setTextColor(0xff1E90FF);
        main_tab_exit.setTextColor(0xff1E90FF);
        main_tab_cancel.setTextColor(0xff1E90FF);

        dialog_out.setOnClickListener(this);
        main_tab_retroaction.setOnClickListener(this);
        main_tab_bottom.setOnClickListener(this);
        main_tab_exit.setOnClickListener(this);
        main_tab_cancel.setOnClickListener(this);

        Window window = this.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        this.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        GradeDialog.this.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tab_retroaction:
                GradeDialog.this.dismiss();
                break;
            case R.id.main_tab_bottom:
                GradeDialog.this.dismiss();
                break;
            case R.id.main_tab_exit:
                GradeDialog.this.dismiss();
                break;
            case R.id.main_tab_cancel:
                GradeDialog.this.dismiss();
                break;
            case R.id.dialog_out:
                GradeDialog.this.dismiss();
                break;
        }
    }

}