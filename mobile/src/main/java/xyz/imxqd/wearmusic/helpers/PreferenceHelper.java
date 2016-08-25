package xyz.imxqd.wearmusic.helpers;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import xyz.imxqd.wearmusic.utils.App;

/**
 * Created by imxqd on 2016/8/14.
 * SharedPreference的辅助操作类
 */
public class PreferenceHelper {

    private static String PREFERENCE_LAST_MUSIC_NAME = "last_music";

    public static String getFilterDuration() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return settings.getString("filter_duration", "B90");
    }

    public static boolean getHeadSetEnable() {
        SharedPreferences settings =
        PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return settings.getBoolean("drive_by_wire", true);
    }

    public static boolean getAutoPauseEnable() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(App.getApp());
        return settings.getBoolean("auto_pause", true);
    }


}
