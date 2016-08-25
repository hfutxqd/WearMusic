package xyz.imxqd.wearmusic.utils;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by imxqd on 2016/8/16.
 * 常量，预定义
 */
public class Constants {
    public static class WatchCMD {

        public static final String START_SYNC_PATH             = "/sync-start";
        public static final String END_SYNC_PATH               = "/sync-end";
        public static final String GET_MEDIA_EXIST_INFO_PATH   = "/get-media-exist-info";
        public static final String GET_MEDIA_PATH              = "/get-media";
        public static final String GET_STORAGE_INFO_PATH       = "/get-storage-info";
        public static final String GET_FILE_LIST_PATH          = "/get-file-list";
        public static final String GET_SDCARD_PATH             = "/get-sdcard-path";
        public static final String DELETE_MEDIA_PATH           = "/delete-media";
        public static final String DELETE_FILE_PATH            = "/delete-file";
        public static final String CTRL_STOP_PATH              = "/ctrl-stop";
        public static final String POST_MEDIA_PATH             = "/post-media";
        public static final String POST_FILE_PATH              = "/post-file";
        public static final String REQUEST_SCAN_MEDIA_PATH     = "/request-scan-media";


        @StringDef({
                START_SYNC_PATH,
                END_SYNC_PATH,
                GET_MEDIA_EXIST_INFO_PATH,
                GET_MEDIA_PATH,
                GET_STORAGE_INFO_PATH,
                GET_FILE_LIST_PATH,
                GET_SDCARD_PATH,
                DELETE_MEDIA_PATH,
                DELETE_FILE_PATH,
                CTRL_STOP_PATH,
                POST_MEDIA_PATH,
                POST_FILE_PATH,
                REQUEST_SCAN_MEDIA_PATH
        })
        @Retention(RetentionPolicy.SOURCE)
        public @interface WatchCMDValue {}
    }

    public static class Action {

        public static final String PLAY_ACTION_PLAY = "PlayService.play"; // 播放
        public static final String PLAY_ACTION_PAUSE= "PlayService.pause"; // 暂停
        public static final String PLAY_ACTION_NEXT= "PlayService.next"; // 下一曲
    }

    public static class PlayMode {


        public static final int PLAY_MODE_REPEAT_LIST          = 0; // 列表循环
        public static final int PLAY_MODE_REPEAT_RAND          = 1; // 列表随机
        public static final int PLAY_MODE_REPEAT_ONE           = 2; // 单曲循环

        @IntDef({PLAY_MODE_REPEAT_LIST, PLAY_MODE_REPEAT_ONE, PLAY_MODE_REPEAT_ONE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface PlayModeValue {}

    }
}
