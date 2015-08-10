package com.cj.dreams.video.dbhelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by fanyafeng on 2015/7/28/0028.
 */
public class GoodTableCourse {
    private static final String RECORDTABLE = "t_good";
    private SQLiteDatabase db = null;

    public GoodTableCourse(SQLiteDatabase db) {
        this.db = db;
    }

    public Boolean searchGood(String v_id) {
        String sql = "SELECT v_id FROM " + RECORDTABLE + " WHERE v_id = ?";
        String searchField[] = new String[]{v_id};
        Cursor result = this.db.rawQuery(sql, searchField);
        result.moveToFirst();
        if (result.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
