package xyz.imxqd.wearmusic.helpers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import xyz.imxqd.wearmusic.receivers.HeadSetReceiver;

/**
 * 耳机线控管理助手类
 * 单例
 */
public class HeadSetHelper {

    private static HeadSetHelper headSetHelper;

    private OnHeadSetListener headSetListener = null;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (headSetListener != null) {
                    if (intent.getIntExtra("state", 0) == 0){
                        headSetListener.onHeadSetPullOut();
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        headSetListener.onHeadSetPushIn();
                    }
                }
            }
        }
    };

    public static HeadSetHelper getInstance() {
        if (headSetHelper == null) {
            headSetHelper = new HeadSetHelper();
        }
        return headSetHelper;
    }

    /**
     * 设置耳机单击双击监听接口
     * 必须在open前设置此接口，否则设置无效
     *
     * @param headSetListener
     */
    public void setOnHeadSetListener(OnHeadSetListener headSetListener) {
        this.headSetListener = headSetListener;
    }

    /**
     * 开启耳机线控监听,
     * 请务必在设置接口监听之后再调用此方法，否则接口无效
     *
     * @param context
     */
    public void open(Context context) {

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName name = new ComponentName(context.getPackageName(),
                HeadSetReceiver.class.getName());
        audioManager.registerMediaButtonEventReceiver(name);
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    /**
     * 关闭耳机线控监听
     *
     * @param context
     */
    public void close(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ComponentName name = new ComponentName(context.getPackageName(),
                HeadSetReceiver.class.getName());
        audioManager.unregisterMediaButtonEventReceiver(name);
        context.unregisterReceiver(receiver);
    }

    /**
     * 删除耳机单机双击监听接口
     */
    public void removeHeadSetListener() {
        this.headSetListener = null;
    }

    /**
     * 获取耳机单击双击接口
     *
     * @return
     */
    public OnHeadSetListener getOnHeadSetListener() {
        return headSetListener;
    }

    /**
     * 耳机按钮单双击监听
     *
     * @author
     */
    public interface OnHeadSetListener {
        /**
         * 单击触发,主线程。
         * 此接口真正触发是在单击操作1秒后
         * 因为需要判断1秒内是否仍监听到点击，有的话那就是双击了。
         */
        void onClick();

        /**
         * 双击触发，此接口在主线程，可以放心使用
         */
        void onDoubleClick();

        /**
         * 耳机拔出
         */
        void onHeadSetPullOut();

        /**
         * 耳机插入
         */
        void onHeadSetPushIn();
    }
}
