package xyz.imxqd.wearmusic.utils;

import android.app.Application;

/**
 * Created by imxqd on 2016/8/7.
 * 应用的Application类
 */
public class App extends Application {
    private static App app = null;
    @Override
    public void onCreate() {
        app = this;
        super.onCreate();
    }

    public static App getApp() {
        return app;
    }
}
