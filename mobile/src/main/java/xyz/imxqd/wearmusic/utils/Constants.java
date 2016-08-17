package xyz.imxqd.wearmusic.utils;

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
    }

    public static class PlayMode {
        public static final int PLAY_MODE_REPEAT_LIST          = 1; // 列表循环
        public static final int PLAY_MODE_REPEAT_RAND          = 2; // 列表随机
        public static final int PLAY_MODE_REPEAT_ONE           = 3; // 单曲循环
    }

    public static class Events {

    }
}
