package xyz.imxqd.wearmusic.utils;

import java.util.Random;

/**
 * Created by imxqd on 2016/8/25.
 * 随机数生成类
 */
public class RandomUtil {
    private static Random mRandom = new Random(System.currentTimeMillis());

    public static int getInt(int n) {
        return mRandom.nextInt(n);
    }
}
