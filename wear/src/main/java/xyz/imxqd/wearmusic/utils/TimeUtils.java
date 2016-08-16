package xyz.imxqd.wearmusic.utils;


/**
 * Created by imxqd on 2016/8/6.
 * 时间工具
 */
public class TimeUtils {
    public static String getTimeString(int duration) {
        int sec = duration / 1000;
        int min = sec / 60;
        sec = sec % 60;
        return String.format("%d:%02d", min, sec);
    }
}
