package com.cj.dreams.video.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cj.dreams.video.R;
import com.cj.dreams.video.activity.MainActivity;
import com.cj.dreams.video.dbhelper.LaughSQLiteOpenHelper;


/**
 * <p>
 * Title: MainExitDialog
 * </p>
 */
public class EmptyRecordDialog extends Dialog implements View.OnClickListener {
    int layoutRes;
    Context context;
    private TextView dialog_exit_title;
    private TextView dialog_exit_detail;
    private Button confirmBtn;
    private Button cancelBtn;
    private LaughSQLiteOpenHelper laughSQLiteOpenHelper;
    private SQLiteDatabase db = null;

    public EmptyRecordDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * @param context
     * @param resLayout
     */
    public EmptyRecordDialog(Context context, int resLayout) {
        super(context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    /**
     * @param context
     * @param theme
     * @param resLayout
     */
    public EmptyRecordDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        this.laughSQLiteOpenHelper = new LaughSQLiteOpenHelper(context);
        laughSQLiteOpenHelper.getWritableDatabase();
        db = context.openOrCreateDatabase("laughvideo.db", Context.MODE_PRIVATE, null);
        dialog_exit_title = (TextView) findViewById(R.id.dialog_exit_title);
        dialog_exit_detail = (TextView) findViewById(R.id.dialog_exit_detail);
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
                SQLiteDatabase sqLiteDatabase = laughSQLiteOpenHelper.getWritableDatabase();
                sqLiteDatabase.execSQL("DELETE FROM " + "t_record");
                sqLiteDatabase.close();
                EmptyRecordDialog.this.dismiss();
                break;
            case R.id.cancel_btn:
                EmptyRecordDialog.this.dismiss();
                break;
        }
    }

}