package com.cj.dreams.video.util;


/**
 * System统一管理类
 *
 * @author 樊亚风
 */
public class S {

    private static final String TAG = "---| sysout输出:";
    public static boolean isSystem = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private S() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void l() {
        if (isSystem) {
            System.out.println("--------------------------------------");
        }
    }


    // 下面两个是默认sys的函数
    public static void p(Object msg) {
        if (isSystem) {
            System.out.print(TAG + msg + " |---");
        }
    }

    public static void p(String msg) {
        if (isSystem) {
            System.out.print(TAG + msg + " |---");
        }
    }

    public static void p(int msg) {
        if (isSystem) {
            System.out.print(TAG + msg + " |---");
        }
    }

    public static void pl(Object msg) {
        if (isSystem) {
            System.out.println(TAG + msg + " |---");
        }
    }

    public static void pl(String msg) {
        if (isSystem) {
            System.out.println(TAG + msg + " |---");
        }
    }

    public static void pl(int msg) {
        if (isSystem) {
            System.out.println(TAG + msg + " |---");
        }
    }

    // 下面是传入自定义tag的函数
    public static void p(Object tag, Object msg) {
        if (isSystem) {
            System.out.print("---| " + tag + ":" + msg + " |---");
        }
    }

    public static void p(String tag, String msg) {
        if (isSystem) {
            System.out.print("---| " + tag + ":" + msg + " |---");
        }
    }

    public static void p(String tag, int msg) {
        if (isSystem) {
            System.out.print("---| " + tag + ":" + msg + " |---");
        }
    }

    public static void pl(Object tag, Object msg) {
        if (isSystem) {
            System.out.println("---| " + tag + ":" + msg + " |---");
        }
    }

    public static void pl(String tag, String msg) {
        if (isSystem) {
            System.out.println("---| " + tag + ":" + msg + " |---");
        }
    }

    public static void pl(String tag, int msg) {
        if (isSystem) {
            System.out.println("---| " + tag + ":" + msg + " |---");
        }
    }

}