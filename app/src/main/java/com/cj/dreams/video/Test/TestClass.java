package com.cj.dreams.video.Test;

//import android.content.Intent;
import FanYaFeng.S;

import FanYaFeng.EncryptUtil;
//import com.cj.dreams.video.util.S;

//import java.io.UnsupportedEncodingException;

/**
 * Created by fanyafeng on 2015/08/04/0004.
 */
public class TestClass {
    public static void main(String[] args) {
        System.out.println("");
//        S.pl("-------------------------------------------------------------------");

        testSplit();
        testEncode();
//        S.l();
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
        System.out.print(testString);
        testi = Integer.parseInt(testString);
        System.out.print(testi);

        for (int i = 0; i < appVersion.length(); i++) {
            if (appVersion.charAt(i) != '.') {
                appTestString += appVersion.charAt(i);
            }
        }
        System.out.print(appTestString);
        System.out.print(Integer.parseInt(appTestString));
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
        System.out.print(item);

        int t = k.length;
        System.out.print(t);
        System.out.print(k.toString());
        if ((int) version.charAt(0) > (int) appVersion.charAt(0) && (int) version.charAt(1) > (int) appVersion.charAt(1) && (int) version.charAt(2) > (int) appVersion.charAt(2)) {
            System.out.print("version appversion ");
        }
        System.out.print("");
        if ((int) version.charAt(0) > (int) appVersion.charAt(0)) {
            System.out.print("version appversion ");
        } else if ((int) version.charAt(0) == (int) appVersion.charAt(0)) {
            if ((int) version.charAt(2) > (int) appVersion.charAt(2)) {
                System.out.print("version appversion ");
            } else if ((int) version.charAt(2) == (int) appVersion.charAt(2)) {
                if ((int) version.charAt(4) > (int) appVersion.charAt(4)) {

                    System.out.print("version appversion ");
                }
            } else {

            }
        } else {

        }

    }
}
