package xyz.imxqd.wearmusic.helpers;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.os.Build;
import android.os.Environment;

import static xyz.imxqd.wearmusic.utils.Config.isDebug;

/**
 * Created by imxqd on 2016/8/14.
 * FC的处理
 */

public class CrashHandler implements UncaughtExceptionHandler {


    private static CrashHandler instance;


    public static synchronized void init() {
        if (isDebug) {
            return;
        }
        if (instance == null) {
            instance = new CrashHandler();
        }
        Thread.setDefaultUncaughtExceptionHandler(instance);
    }

    /**
     * 当程序crash 会回调此方法， Throwable中存放这错误日志
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = Environment.getExternalStorageDirectory();
            try {
                File log = new File(dir, "wear_music_crash_" + System.currentTimeMillis() + ".log");
                if (!log.exists()) {
                    log.createNewFile();
                }
                PrintStream stream = new PrintStream(log);
                stream.println("Timestamp: " + System.nanoTime());
                stream.println("Brand: " + Build.BRAND);
                stream.println("Device: " + Build.DEVICE);
                stream.println("Model: " + Build.MODEL);
                stream.println("Display: " + Build.DISPLAY);
                stream.println("Version: " + Build.VERSION.INCREMENTAL);
                stream.println("SDK Int: " + Build.VERSION.SDK_INT);
                stream.println("Fingerprint: " + Build.FINGERPRINT);
                stream.println("Thread: " + Thread.currentThread().getName());
                throwable.printStackTrace(stream);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throwable.printStackTrace();

        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
