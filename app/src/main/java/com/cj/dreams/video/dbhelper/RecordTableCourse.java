package com.cj.dreams.video.dbhelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cj.dreams.video.bean.LocalRecordBean;
import com.cj.dreams.video.util.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanyafeng on 2015/7/27/0027.
 */
public class RecordTableCourse {
    private static final String RECORDTABLE = "t_record";
    private SQLiteDatabase db = null;

    public RecordTableCourse(SQLiteDatabase db) {
        this.db = db;
    }

    public Boolean searchRecord(String v_id) {
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
