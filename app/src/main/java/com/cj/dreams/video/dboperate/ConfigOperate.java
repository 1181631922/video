package com.cj.dreams.video.dboperate;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import FanYaFeng.L;

/**
 * Created by fanyafeng on 2015/7/27/0027.
 */
public class ConfigOperate {
    private static final String TABLENAME = "t_config";
    private SQLiteDatabase db = null;

    public ConfigOperate(SQLiteDatabase db) {
        this.db = db;
    }

    //插入数据库
    public void insert(String config_appname, String config_appurl) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("config_appname", config_appname);
        contentValues.put("config_appurl", config_appurl);
        this.db.insert(TABLENAME, null, contentValues);
        L.d("数据库插入，执行到此");
        this.db.close();
    }

    //更新数据库
    public void update(String config_appname, String config_appurl) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("config_appname", config_appname);
        contentValues.put("config_appurl", config_appurl);
        String whereClause = "config_appname=?";
        String whereArgs[] = new String[]{String.valueOf(config_appname)};
        this.db.update(TABLENAME, contentValues, whereClause, whereArgs);
        this.db.close();
    }

    //删除数据库
    public void delete(String user_id) {
        String whereClause = "config_appname=?";
        String whereArgs[] = new String[]{String.valueOf(user_id)};
        this.db.delete(TABLENAME, whereClause, whereArgs);
        this.db.close();
    }
}
