package com.cj.dreams.video.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fanyafeng on 2015/7/24/0024.
 */
public class LaughSQLiteOpenHelper extends BaseSQLiteOpenHelper {
    private static final String DATABASENAME = "laughvideo.db";
    private static final int DATABASEVERSION = 1;
    //三个数据库
    private static final String table_record = "t_record";
    private static final String table_collect = "t_collect";
    private static final String table_good = "t_good";
    private static final String table_user="t_user";

    public LaughSQLiteOpenHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_record_sql = "CREATE TABLE " + table_record + "("
                + "_id        INTEGER      PRIMARY KEY,"
                + "v_id       VARCHAR(50)   NOT NULL,"
                + "v_image    VARCHAR(255)  NOT NULL,"
                + "v_title    VARCHAR(255)  NOT NULL,"
                + "v_url      VARCHAR(255)  NOT NULL"
                + ")";

        String create_collect_sql = "CREATE TABLE " + table_collect + "("
                + "_id        INTEGER      PRIMARY KEY,"
                + "v_id       VARCHAR(50)   NOT NULL,"
                + "v_image    VARCHAR(255)  NOT NULL,"
                + "v_title    VARCHAR(255)  NOT NULL,"
                + "v_url      VARCHAR(255)  NOT NULL"
                + ")";

        String create_good_sql = "CREATE TABLE " + table_good + "("
                + "_id        INTEGER      PRIMARY KEY,"
                + "v_id       VARCHAR(50)   NOT NULL"
                + ")";

        String create_user_sql = "CREATE TABLE " + table_user + "("
                + "_id           INTEGER      PRIMARY KEY,"
                + "user_id       VARCHAR(50)   NOT NULL,"
                + "user_name     VARCHAR(50)   NOT NULL,"
                + "user_image    VARCHAR(50)   NOT NULL"
                + ")";

        db.execSQL(create_record_sql);
        db.execSQL(create_collect_sql);
        db.execSQL(create_good_sql);
        db.execSQL(create_user_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String create_record_sql = "DROP TABLE IF EXISTS" + table_record;
        String create_collect_sql = "DROP TABLE IF EXISTS" + table_collect;
        String create_good_sql = "DROP TABLE IF EXISTS" + table_good;
        String create_user_sql = "DROP TABLE IF EXISTS" + table_user;
        db.execSQL(create_record_sql);
        db.execSQL(create_collect_sql);
        db.execSQL(create_good_sql);
        db.execSQL(create_user_sql);
        this.onCreate(db);
    }
}
