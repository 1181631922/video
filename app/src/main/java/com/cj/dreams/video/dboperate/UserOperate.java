package com.cj.dreams.video.dboperate;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.cj.dreams.video.util.L;

/**
 * Created by fanyafeng on 2015/7/27/0027.
 */
public class UserOperate {
    private static final String TABLENAME = "t_user";
    private SQLiteDatabase db = null;

    public UserOperate(SQLiteDatabase db) {
        this.db = db;
    }

    //插入数据库
    public void insert(String user_id, String user_name, String user_image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", user_id);
        contentValues.put("user_name", user_name);
        contentValues.put("user_image", user_image);
        this.db.insert(TABLENAME, null, contentValues);
        L.d("数据库插入，执行到此");
        this.db.close();
    }

    //更新数据库
    public void update(String user_id, String user_name, String user_image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", user_id);
        contentValues.put("user_name", user_name);
        contentValues.put("user_image", user_image);
        String whereClause = "user_id=?";
        String whereArgs[] = new String[]{String.valueOf(user_id)};
        this.db.update(TABLENAME, contentValues, whereClause, whereArgs);
        this.db.close();
    }

    //删除数据库
    public void delete(String user_id) {
        String whereClause = "user_id=?";
        String whereArgs[] = new String[]{String.valueOf(user_id)};
        this.db.delete(TABLENAME, whereClause, whereArgs);
        this.db.close();
    }
}
