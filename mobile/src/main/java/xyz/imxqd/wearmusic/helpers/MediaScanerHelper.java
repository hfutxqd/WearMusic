package xyz.imxqd.wearmusic.helpers;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import xyz.imxqd.wearmusic.utils.App;
import xyz.imxqd.wearmusic.utils.Config;

/**
 * Created by imxqd on 2016/8/14.
 * 媒体扫描辅助类
 */
public class MediaScanerHelper {

    private static final String TAG = "MediaScanerHelper";

    public static final String MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";

    public static void scanAll() {

    }

    public static void scanFile() {
    }

    /**
     * 按照给定路径搜索媒体文件
     * @param path 指定路径
     */
    public static void scanPath(String ... path) {
        MediaScannerConnection.scanFile(App.getApp(), path, null,
                new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                if (Config.isDebug) {
                    Log.d(TAG, "onScanCompleted:");
                    Log.d(TAG, "path: " + path);
                    Log.d(TAG, "uri: " + uri.toString());
                }
            }
        });
    }

    /**
     * 从媒体库中删除歌曲
     * @param songId 歌曲id
     * @return 是否成功
     */
    public static boolean remove(long songId) {
        ContentResolver resolver = App.getApp().getContentResolver();
        int res = resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Audio.Media._ID + "=" + songId, null);
        return res > 0;
    }
}
