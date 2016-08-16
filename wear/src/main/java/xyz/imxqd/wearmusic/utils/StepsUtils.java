package xyz.imxqd.wearmusic.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

/**
 * Created by imxqd on 2016/8/7.
 * 获取系统步数的工具类
 */
public class StepsUtils {
    private static final String TAG = "StepsUtils";

    // 获取健康ContentProvider的数据
    private static final Uri STEP_URI = Uri.parse("content://com.mobvoi.ticwear.steps");

    public static int getTicSteps() {
//        Log.d(TAG, Build.DISPLAY);
//        Log.d(TAG, Build.BRAND);
//        Log.d(TAG, Build.DEVICE);

        ContentResolver mResolver = App.getApp().getContentResolver();
        int steps = 0;
        int distance = 0;
        Cursor cursor = mResolver.query(STEP_URI, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    steps = cursor.getInt(0);
                    distance = cursor.getInt(1);
                }
            } finally {
                cursor.close();
            }
        }
        return steps;
    }
}
