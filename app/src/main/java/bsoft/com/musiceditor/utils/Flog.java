package bsoft.com.musiceditor.utils;

import android.util.Log;

/**
 * Created by Adm on 9/7/2016.
 */
public final class Flog {
    private final static String TAG = "VoiceRecorder";
    private final static boolean IS_DEBUG = true; // will set false when publish

    public static void d(String msg) {
        if (IS_DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (IS_DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String msg) {
        if (IS_DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (IS_DEBUG) {
            Log.e(tag, msg);
        }
    }
}
