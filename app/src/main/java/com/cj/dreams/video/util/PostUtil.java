package com.cj.dreams.video.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class PostUtil {
    public static String SESSIONID = null;

    public static String postData(String address, Map<String, String> params) throws IOException {
        String paramStr = "";
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() != null)
                    paramStr += "&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
            }
            if (paramStr.length() == 0)
                return "";
            paramStr = paramStr.substring(1, paramStr.length());
            Log.d("------------------------------",address);
            Log.d("------------------------------", paramStr);
        }
        byte[] data = paramStr.getBytes();
        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //第一次一般是还未被赋值，若有值则将SessionId发给服务器
        if (null != SESSIONID) {//"JSESSIONID="+
            conn.setRequestProperty("Cookie", SESSIONID);
        }
        conn.setConnectTimeout(10000);
        // 这是请求方式为POST
        conn.setRequestMethod("POST");
        // 设置post请求必要的请求头
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// 请求头, 必须设置
        conn.setRequestProperty("Content-Length", data.length + "");// 注意是字节长度,不是字符长度
        conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setDoOutput(true);// 准备写出
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(data);// 写出数据
        outputStream.close();

        // 获取session
        //String cookieVal =conn.getHeaderField("Set-Cookie");
        Map map = conn.getHeaderFields();
        List<String> jsession = (List) map.get("Set-Cookie");
        if (jsession != null)
            for (String cookieVal : jsession) {
                if (cookieVal.indexOf("JSESSION") != -1) {
                    SESSIONID = cookieVal.substring(0, cookieVal.indexOf(";"));
                    break;
                }
            }
        int responseCode = conn.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
            // 当正确响应时处理数据
            StringBuffer sb = new StringBuffer();
            String readLine;
            BufferedReader responseReader;
            // 处理响应流，必须与服务器响应流输出的编码一致
            responseReader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine).append("\n");
            }


            responseReader.close();
            return sb.toString();
        } else {
            System.out.println("post error:responseCode=" + responseCode);
            System.out.println("url:" + address);
        }
        return "";
    }

}
