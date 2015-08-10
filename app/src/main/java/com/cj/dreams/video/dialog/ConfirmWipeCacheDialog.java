package com.cj.dreams.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.util.CommonUtil;
import com.cj.dreams.video.util.T;

import java.io.File;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class ConfirmWipeCacheDialog extends Dialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    private TextView dialog_confirm_title;
    private TextView dialog_confirm_detail;
    private Button dialog_confirm_confirm_btn;
    private Button dialog_confirm_cancel_btn;
    private ProgressBar dialog_confirm_progressbar;
    private String cachePathString;

    public ConfirmWipeCacheDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * @param context
     * @param resLayout
     */
    public ConfirmWipeCacheDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param resLayout
     */
    public ConfirmWipeCacheDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);

        dialog_confirm_title = (TextView) findViewById(R.id.dialog_confirm_title);
        dialog_confirm_detail = (TextView) findViewById(R.id.dialog_confirm_detail);
        dialog_confirm_progressbar = (ProgressBar) findViewById(R.id.dialog_confirm_progressbar);
        dialog_confirm_confirm_btn = (Button) findViewById(R.id.dialog_confirm_confirm_btn);
        dialog_confirm_cancel_btn = (Button) findViewById(R.id.dialog_confirm_cancel_btn);

        dialog_confirm_confirm_btn.setTextColor(0xff1E90FF);
        dialog_confirm_cancel_btn.setTextColor(0xff1E90FF);

        dialog_confirm_confirm_btn.setOnClickListener(this);
        dialog_confirm_cancel_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_confirm_confirm_btn:
                dialog_confirm_detail.setText("正在清除缓存。。。");
                dialog_confirm_progressbar.setVisibility(View.VISIBLE);
                dialog_confirm_confirm_btn.setClickable(false);
                dialog_confirm_cancel_btn.setClickable(false);

                if (CommonUtil.hasSDCard()) {
                    cachePathString = CommonUtil.getRootFilePath() + "fanyafeng/files/";
                } else {
                    cachePathString = CommonUtil.getRootFilePath() + "fanyafeng/files";
                }
                File file = new File(cachePathString);
                DeleteFile(file);
                mHandler.sendEmptyMessage(1);
                break;
            case R.id.dialog_confirm_cancel_btn:
                ConfirmWipeCacheDialog.this.dismiss();
                break;
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dialog_confirm_cancel_btn.setClickable(true);
                    dialog_confirm_progressbar.setVisibility(View.GONE);
                    dialog_confirm_detail.setText("文件或文件夹已经清除！");
                    break;
                case 1:
                    dialog_confirm_progressbar.setVisibility(View.GONE);
                    dialog_confirm_detail.setText("缓存已经清除");
                    ConfirmWipeCacheDialog.this.dismiss();
                    T.showShort(context, "缓存已经清除");
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public void DeleteFile(File file) {
        if (file.exists() == false) {
            mHandler.sendEmptyMessage(0);
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
            }
        }
    }

}