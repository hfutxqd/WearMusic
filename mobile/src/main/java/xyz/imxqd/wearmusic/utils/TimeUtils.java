package xyz.imxqd.wearmusic.utils;


/**
 * Created by imxqd on 2016/8/6.
 * 时间工具
 */
public class TimeUtils {
    public static String getTimeString(int duration) {
        double sec = duration / 1000.0;
        int min = (int) sec / 60;
        sec = sec % 60;
        return String.format("%d:%02d", min, Math.round(sec));
    }
}
