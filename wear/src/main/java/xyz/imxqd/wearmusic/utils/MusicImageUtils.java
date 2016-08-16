package xyz.imxqd.wearmusic.utils;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

/**
 * Created by imxqd on 2016/8/7.
 * 音乐图片获取工具类
 */
public class MusicImageUtils {
    public static final Uri ALBUMART_URI = Uri.parse("content://media/external/audio/albumart");

    public static Bitmap getByAlbumId(long albumId) {
        Uri uri = ContentUris.withAppendedId(ALBUMART_URI, albumId);
        ParcelFileDescriptor pfd = null;
        try {
            pfd = App.getApp().getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (pfd != null) {
            FileDescriptor fd = pfd.getFileDescriptor();
            return BitmapFactory.decodeFileDescriptor(fd);
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
            e.printStackTrace();
        }
        if (pfd != null) {
            FileDescriptor fd = pfd.getFileDescriptor();
            bitmap = BitmapFactory.decodeFileDescriptor(fd);
        }
        return bitmap;
    }

}
