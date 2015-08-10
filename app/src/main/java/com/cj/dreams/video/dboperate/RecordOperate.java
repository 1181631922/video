package com.cj.dreams.video.dboperate;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.cj.dreams.video.util.L;

/**
 * Created by fanyafeng on 2015/7/27/0027.
 */
public class RecordOperate {
    private static final String TABLENAME = "t_record";
    private SQLiteDatabase db = null;

    public RecordOperate(SQLiteDatabase db) {
        this.db = db;
    }

    //插入数据库
    public void insert(String v_id, String v_image, String v_title, String v_url) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("v_id", v_id);
        contentValues.put("v_image", v_image);
        contentValues.put("v_title", v_title);
        contentValues.put("v_url", v_url);
        this.db.insert(TABLENAME, null, contentValues);
        L.d("数据库插入，执行到此");
        this.db.close();
    }

    //更新数据库
    public void update(String v_id, String v_image, String v_title, String v_url) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("v_id", v_id);
        contentValues.put("v_image", v_image);
        contentValues.put("v_title", v_title);
        contentValues.put("v_url", v_url);
        String whereClause = "v_id=?";
        String whereArgs[] = new String[]{String.valueOf(v_id)};
        this.db.update(TABLENAME, contentValues, whereClause, whereArgs);
        this.db.close();
    }

    //删除数据库
    public void delete(String v_id) {
        String whereClause = "v_id=?";
        String whereArgs[] = new String[]{String.valueOf(v_id)};
        this.db.delete(TABLENAME, whereClause, whereArgs);
        this.db.close();
    }
}
