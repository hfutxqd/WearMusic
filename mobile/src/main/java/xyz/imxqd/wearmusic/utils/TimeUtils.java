package xyz.imxqd.wearmusic.utils;


import java.util.Locale;

/**
 * Created by imxqd on 2016/8/6.
 * 时间工具
 */
public class TimeUtils {

    /**
     * 根据给定时长获取时间字符串
     * @param duration 时长（秒）
     * @return 时间字符串(分 秒)
     */
    public static String getTimeString(int duration) {
        double sec = duration / 1000.0;
        int min = (int) sec / 60;
        sec = sec % 60;
        return String.format(Locale.getDefault(), "%d:%02d", min, (int)sec);
    }
}
