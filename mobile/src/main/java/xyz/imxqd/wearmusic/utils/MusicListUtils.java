package xyz.imxqd.wearmusic.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

import xyz.imxqd.wearmusic.helpers.PreferenceHelper;
import xyz.imxqd.wearmusic.models.MusicInfo;

/**
 * Created by imxqd on 2016/8/11.
 * 获取音乐列表的工具类
 */
public class MusicListUtils {

    public static final String WHERE_ALL = null;
    public static final String WHERE_ABOVE_30S = "duration > 30000";
    public static final String WHERE_ABOVE_60S = "duration > 60000";
    public static final String WHERE_ABOVE_90S = "duration > 90000";
    public static final String WHERE_ABOVE_120S = "duration > 120000";
    public static final String WHERE_BELOW_600S = "duration < 600000";
    public static final String WHERE_BELOW_1200S = "duration < 1200000";
    public static final String WHERE_BELOW_1800S = "duration < 1800000";

    public static ArrayList<MusicInfo> getMusicList(String where) {
        ArrayList<MusicInfo> list = new ArrayList<>();
        ContentResolver cr = App.getApp().getContentResolver();
        Cursor cursor = cr.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, where, null,
                "title");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MusicInfo info = new MusicInfo();
                int index = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                info.setTitle(cursor.getString(index));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                info.setDuration(cursor.getInt(index));
                info.setDurationString(TimeUtils.getTimeString(cursor.getInt(index)));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                String url = cursor.getString(index);
                info.setSongUrl(Uri.parse(url));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                info.setAlbumName(cursor.getString(index));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                info.setArtist(cursor.getString(index));

                index = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                info.setSongId(cursor.getLong(index));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                long albumId = cursor.getLong(index);
                info.setAlbumId(albumId);
                list.add(info);
            }
            cursor.close();
        }
        return list;
    }

    public static ArrayList<MusicInfo> getMusicList() {
        String dur = PreferenceHelper.getFilterDuration();
        return getMusicList(getWhereCase(dur));
    }

    public static String getWhereCase(String settingString) {
        String res = WHERE_ABOVE_90S;
        switch (settingString) {
            case "B30":
                res = WHERE_ABOVE_30S;
                break;
            case "B60":
                res = WHERE_ABOVE_60S;
                break;
            case "B90":
                res = WHERE_ABOVE_90S;
                break;
            case "B120":
                res = WHERE_ABOVE_120S;
                break;
            case "A600":
                res = WHERE_BELOW_600S;
                break;
            case "A1200":
                res = WHERE_BELOW_1200S;
                break;
            case "A1800":
                res = WHERE_BELOW_1800S;
                break;
            case "N":
                res = WHERE_ALL;
                break;
            default:
        }
        return res;
    }


}
