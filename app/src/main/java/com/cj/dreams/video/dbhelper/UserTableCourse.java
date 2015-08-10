package com.cj.dreams.video.dbhelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by fanyafeng on 2015/7/27/0027.
 */
public class UserTableCourse {
    private static final String RECORDTABLE = "t_user";
    private SQLiteDatabase db = null;

    public UserTableCourse(SQLiteDatabase db) {
        this.db = db;
    }

    public Boolean searchRecord(String user_id) {
        String sql = "SELECT user_id FROM " + RECORDTABLE + " WHERE user_id = ?";
        String searchField[] = new String[]{user_id};
        Cursor result = this.db.rawQuery(sql, searchField);
        result.moveToFirst();
        if (result.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }


}
