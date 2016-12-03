package com.fade.drmmusic.utils;

import android.util.Log;

/**
 * Created by SnailSet on 2016/11/29.
 */

public class FLog {
    public final static String TGA = "DRMMusic";
    public final static boolean ALLOW = true;

    public static void i(String msg) {
        if (!ALLOW) return;
        Log.i(TGA, msg);
    }

    public static void e(String msg) {
        if (!ALLOW) return;
        Log.e(TGA, msg);
    }

    public static void d(String msg) {
        if (!ALLOW) return;
        Log.d(TGA, msg);
    }

    public static void v(String msg) {
        if (!ALLOW) return;
        Log.v(TGA, msg);
    }

    public static void w(String msg) {
        if (!ALLOW) return;
        Log.w(TGA, msg);
    }
}
