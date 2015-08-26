package com.cj.dreams.video.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.MainActivity;
import com.cj.dreams.video.bean.AppGradeBean;
import FanYaFeng.L;
import FanYaFeng.SP;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class GradeDialog extends Dialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    private RelativeLayout dialog_appshop;
    private Button appshop_name;
    private Button appshop_cancel;
    private AppGradeBean appGradeBean;


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
        dialog_appshop = (RelativeLayout) findViewById(R.id.dialog_appshop);
        appshop_name = (Button) findViewById(R.id.appshop_name);
        appshop_cancel = (Button) findViewById(R.id.appshop_cancel);
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d("-------------------------------------------------");
        L.d((String) SP.get(getContext(), SP.Appshopname, ""));
        appshop_name.setText((String) SP.get(getContext(), SP.Appshopname, ""));

        appshop_name.setTextColor(0xff1E90FF);
        appshop_cancel.setTextColor(0xff1E90FF);

        dialog_appshop.setOnClickListener(this);
        appshop_name.setOnClickListener(this);
        appshop_cancel.setOnClickListener(this);

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
            case R.id.appshop_name:
                Uri uri = Uri.parse((String) SP.get(getContext(), SP.Appshopurl, ""));
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(it);
                GradeDialog.this.dismiss();
                break;
            case R.id.appshop_cancel:
                GradeDialog.this.dismiss();
                break;
            case R.id.dialog_appshop:
                GradeDialog.this.dismiss();
                break;
        }
    }

}