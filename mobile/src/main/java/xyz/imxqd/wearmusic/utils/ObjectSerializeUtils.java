package xyz.imxqd.wearmusic.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by imxqd on 2016/8/14.
 * Object与byte[]互转
 */
public class ObjectSerializeUtils {

    /**
     * 把byte[]转为Object
     * @param bytes byte数组
     * @return 对象
     */
    public static Object read(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = oi.readObject();
            oi.close();
            bi.close();
        } catch (Exception e) {
            if (Config.isDebug) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * 把对象转为byte数组
     * @param o 对象
     * @return byte数组
     */
    public static byte[] write(Object o) {
        byte[] data = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(o);
            data = os.toByteArray();
            oos.close();
            os.close();
        } catch (IOException e) {
            if (Config.isDebug) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
