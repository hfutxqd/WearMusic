package xyz.imxqd.wearmusic.utils;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Created by imxqd on 2016/8/7.
 * 音乐图片获取工具类
 */
public class MusicImageUtils {
    public static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private static HashMap<Long, Bitmap> mCache = new HashMap<>();

    public static Bitmap getByAlbumId(long albumId) {
        if(mCache.containsKey(albumId)) {
            return mCache.get(albumId);
        }
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
        ParcelFileDescriptor pfd = null;
        try {
            pfd = App.getApp().getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            return null;
        }
        if (pfd != null) {
            FileDescriptor fd = pfd.getFileDescriptor();
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
            mCache.put(albumId, bitmap);
            return bitmap;
        }
        return null;
    }

    public static Bitmap getBySongId(long songId) {
        Uri uri = Uri.parse("content://media/external/audio/media/"
                + songId + "/albumart");
        ParcelFileDescriptor pfd = null;
        Bitmap bitmap = null;
        try {
            pfd = App.getApp().getContentResolver()
                    .openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            if(Config.isDebug) {
                e.printStackTrace();
            }
        }
        if (pfd != null) {
            FileDescriptor fd = pfd.getFileDescriptor();
            bitmap = BitmapFactory.decodeFileDescriptor(fd);
        }
        return bitmap;
    }

}
