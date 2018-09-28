package bsoft.com.musiceditor.utils;

import android.app.Application;
import android.content.Context;


public class MyApplication extends Application {
    private static MyApplication mySelf;

    public static MyApplication self() {
        return mySelf;
    }


    public void onCreate() {
        super.onCreate();
        mySelf = this;

    }

    public static Context getAppContext() {
        return mySelf;
    }

    public static String getUriTree() {
        return SharedPrefs.getInstance().get(Utils.TREE_URI, String.class, null);
    }

}