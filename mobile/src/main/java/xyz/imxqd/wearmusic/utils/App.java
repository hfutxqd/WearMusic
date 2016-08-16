package xyz.imxqd.wearmusic.utils;

import android.app.Application;

import com.orm.SugarContext;

import xyz.imxqd.wearmusic.helpers.CrashHandler;
import xyz.imxqd.wearmusic.helpers.MediaScanerHelper;
import xyz.imxqd.wearmusic.models.SongList;

/**
 * Created by imxqd on 2016/8/9.
 *
 */
public class App extends Application {
    private static App app = null;
    @Override
    public void onCreate() {
        CrashHandler.init();
        super.onCreate();
        app = this;
        SugarContext.init(this);
        SongList.init();
    }

    @Override
    public void onTerminate() {
        SugarContext.terminate();
        super.onTerminate();
    }

    public static App getApp() {
        return app;
    }
}
