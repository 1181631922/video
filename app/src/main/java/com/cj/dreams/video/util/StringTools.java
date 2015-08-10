package com.cj.dreams.video.util;

import android.util.Log;

public class StringTools {

	private static final String TAG = "StringTools";
	
	/**
	 * 从url中获取文件名称包括扩展名
	 * @param path url路径
	 * @return
	 */
	public static String getFileNameFromUrl(String path)
	{
		try
		{
			int start=path.lastIndexOf("/");
			if(start!=-1){  
	            return path.substring(start+1);    
	        }else{  
	            return "";  
	        }  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "getFileName-------错误信息：" + e.toString());
			return path;
		}
	}
	
	/**
	 * 从文件名（包含扩展名）截取名称（不包含扩展名）
	 * @param fileName文件名（包含扩展名）
	 * @return
	 */
	public static String getNameFromFileName(String fileName)
	{
		try
		{
			int end=fileName.lastIndexOf(".");
			if(end!=-1){  
	            return fileName.substring(0,end);    
	        }else{  
	            return "";  
	        }  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "getFile-------错误信息：" + e.toString());
			return fileName;
		}
	}
}
