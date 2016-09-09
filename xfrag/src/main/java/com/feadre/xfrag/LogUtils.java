package com.feadre.xfrag;

import android.util.Log;


/**
 * Created by zhouke on 2016/7/1.
 */
public class LogUtils {

    private static      String defTag        = "XFrag";
    //日志输出级别V 使用这个级别 全部隐藏
    public static final int    LEVEL_VERBOSE = 1;
    //日志输出级别D
    public static final int    LEVEL_DEBUG   = 2;
    // 日志输出级别I
    public static final int    LEVEL_INFO    = 3;

    //日志输出级别W
    public static final int LEVEL_WARN   = 4;
    //日志输出级别E
    public static final int LEVEL_ERROR  = 5;
    //日志输出级别S,使用这个级别 全部显示
    public static final int LEVEL_SYSTEM = 6;

    public static int mDebuggable = LEVEL_SYSTEM;


    public static void sf(String msg) {
        if (mDebuggable >= LEVEL_SYSTEM) {
            System.out.println("----------" + msg + "----------");
        }
    }

    public static void s(String msg) {
        if (mDebuggable >= LEVEL_SYSTEM) {
            System.out.println(msg);
        }
    }

    public static void d(String... msg1) {
        if (mDebuggable >= LEVEL_DEBUG) {
            StringBuilder builder = new StringBuilder();
            for (String s : msg1) {
                builder.append(s);
                builder.append(" >>>>>>>>> ");
            }
            Log.d(defTag, builder.toString());
        }
    }

    public static void d(String tag, String... msg1) {
        if (mDebuggable >= LEVEL_DEBUG) {
            StringBuilder builder = new StringBuilder();
            for (String s : msg1) {
                builder.append(s);
                builder.append(" >>>>>>>>> ");
            }
            Log.d(tag, builder.toString());
        }
    }

    public static void d(String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(defTag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(defTag, msg);
        }
    }

    public static void w(String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(defTag, msg);
        }
    }

    public static void w(Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(defTag, "", tr);
        }
    }

    public static void w(String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN && null != msg) {
            Log.w(defTag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void v(String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(defTag, msg);
        }
    }

    public static void e(String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(defTag, msg);
        }
    }

    public static void e(Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(defTag, "", tr);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR && null != msg) {
            Log.e(defTag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(tag, msg);
        }
    }
}
