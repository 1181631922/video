package com.cj.dreams.video.app;

import android.os.Environment;

import java.io.File;

public class MyGlobal {

	//本地保存图片目录
	public static final String mLocalSavePath = Environment.getExternalStorageDirectory() + "/SyncImage/";

    //获取sd卡路径
    public static String sdCardPaht=android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

    //定义sdcard存放数据库文件
    public static final String dbFile="/laugh";
    public static final String dbPath=sdCardPaht+dbFile;
    public static final String dbName="laughvideo.db";
    public static final String dbFilePath=dbPath+"/"+dbName;
    public static File path=new File(dbPath);
    public static File file=new File(dbFilePath);
}
