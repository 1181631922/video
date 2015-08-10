package com.cj.dreams.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.util.FileManagerUtil;
import com.cj.dreams.video.util.T;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class CopyWechatDialog extends Dialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    private Button confirmBtn;
    private Button cancelBtn;
    private TextView dialog_exit_title, dialog_exit_detail;

    public CopyWechatDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * @param context
     * @param resLayout
     */
    public CopyWechatDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param resLayout
     */
    public CopyWechatDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        dialog_exit_title = (TextView) findViewById(R.id.dialog_exit_title);
        dialog_exit_title.setText("确定");
        dialog_exit_detail = (TextView) findViewById(R.id.dialog_exit_detail);
        dialog_exit_detail.setText("是否确定复制微信公众账号到粘贴板");
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
                FileManagerUtil.copy("goddesslaugh", context);
                Message message=Message.obtain();
                message.what=0;
                handler.sendMessage(message);
                CopyWechatDialog.this.dismiss();
                break;
            case R.id.cancel_btn:
                CopyWechatDialog.this.dismiss();
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    T.showLong(context,"微信公众账号已经复制到剪切板");
                    break;
                case 1:
                    break;
            }
        }
    };

}