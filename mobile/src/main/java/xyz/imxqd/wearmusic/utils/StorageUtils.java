package xyz.imxqd.wearmusic.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by imxqd on 2016/8/7.
 * 存储相关工具类
 */
public class StorageUtils {
    public static File getPublicMusicDirectory () {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    }

    public static String getString(long size) {
        return String.format("%.2fGB", size * 1.0 / 1024 / 1024 / 1024);
    }
}
