package com.cj.dreams.video.util;

import android.os.Environment;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class HttpUtil {
    public static String downloadPath = Environment.getExternalStorageDirectory().getPath() + "/download";

    public static void downLoad(String downloadUrl) {

        File file = new File(downloadPath);
        if (!file.exists())
            file.mkdir();
        HttpGet httpGet = new HttpGet(downloadUrl);
        try {
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                InputStream is = httpResponse.getEntity().getContent();
                // 开始下载apk文件
                FileOutputStream fos = new FileOutputStream(downloadPath+ "/laugh.apk");
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
