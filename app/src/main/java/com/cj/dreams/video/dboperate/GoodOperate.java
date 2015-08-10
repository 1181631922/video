package com.cj.dreams.video.dboperate;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by fanyafeng on 2015/7/27/0027.
 */
public class GoodOperate {
    private static final String TABLENAME = "t_good";
    private SQLiteDatabase db = null;

    public GoodOperate(SQLiteDatabase db) {
        this.db = db;
    }

    //插入数据库
    public void insert(String v_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("v_id", v_id);
        this.db.insert(TABLENAME, null, contentValues);
        this.db.close();
    }

    //更新数据库
    public void update(String v_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("v_id", v_id);
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
