package com.cj.dreams.video.Test;

import android.content.Intent;
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
        String version = "1.0.1";
        String appVersion = "1.0.0";
        String testString = "";
        int testi = 0;
        String appTestString = "";
        for (int i = 0; i < version.length(); i++) {
            if (version.charAt(i) != '.') {
                testString += version.charAt(i);
            }
        }
        S.pl(testString);
        testi = Integer.parseInt(testString);
        System.out.print(testi);

        for (int i = 0; i < appVersion.length(); i++) {
            if (appVersion.charAt(i) != '.') {
                appTestString += appVersion.charAt(i);
            }
        }
        S.pl(appTestString);
        S.pl(Integer.parseInt(appTestString));
    }

    private static void testSplit() {
        String version = "1.0.1";
        String appVersion = "1.0.0";
        String[] k = version.split(".");
        String ourversion = null;
        char item = ' ';
        for (int i = 0; i < version.length(); i++) {
            if (version.charAt(i) != '.') {
                item += item;
            }
        }
        S.p(item);

        int t = k.length;
        S.p(t);
        S.pl(k.toString());
        if ((int) version.charAt(0) > (int) appVersion.charAt(0) && (int) version.charAt(1) > (int) appVersion.charAt(1) && (int) version.charAt(2) > (int) appVersion.charAt(2)) {
            S.pl("version比appversion要大");
        }
        S.l();
        if ((int) version.charAt(0) > (int) appVersion.charAt(0)) {
            S.pl("version比appversion要大");
        } else if ((int) version.charAt(0) == (int) appVersion.charAt(0)) {
            if ((int) version.charAt(2) > (int) appVersion.charAt(2)) {
                S.pl("version比appversion要大");
            } else if ((int) version.charAt(2) == (int) appVersion.charAt(2)) {
                if ((int) version.charAt(4) > (int) appVersion.charAt(4)) {

                    S.pl("version比appversion要大");
                }
            } else {

            }
        } else {

        }

    }
}
