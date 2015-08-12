package com.cj.dreams.video.Test;

import android.util.Base64;

import com.cj.dreams.video.util.EncryptUtil;
import com.cj.dreams.video.util.L;
import com.cj.dreams.video.util.S;

import java.io.UnsupportedEncodingException;

/**
 * Created by fanyafeng on 2015/08/04/0004.
 */
public class TestClass {
    public static void main(String[] args) {
        System.out.println("测试主程序");
        System.out.println("-------------------------------------------------------------------");
//        testSplit();
        testEncode();
//        Base64
    }


    private static void testEncode() {
        String i=" "+1+1;
//        S.pl("测试加密64", EncryptUtil.encryptBASE64("password"));
    }

    private static void testSplit() {
        String version = "1.0.0";
        String appVersion = "0.9.0";
        if ((int) version.charAt(0) > (int) appVersion.charAt(0) && (int) version.charAt(1) > (int) appVersion.charAt(1) && (int) version.charAt(2) > (int) appVersion.charAt(2)) {
            S.pl("version比appversion要大");
        }
        S.l();
        if ((int) version.charAt(0) > (int) appVersion.charAt(0)) {
            S.pl("version比appversion要大");
        } else if ((int) version.charAt(0) == (int) appVersion.charAt(0)) {
            if ((int) version.charAt(1) > (int) appVersion.charAt(1)) {
                S.pl("version比appversion要大");
            } else if ((int) version.charAt(1) == (int) appVersion.charAt(1)) {
                if ((int) version.charAt(2) > (int) appVersion.charAt(2)) {
                    S.pl("version比appversion要大");
                }
            } else {

            }
        } else {

        }

    }
}
