package xyz.imxqd.wearmusic.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.helpers.HeadSetHelper;
import xyz.imxqd.wearmusic.helpers.MediaScanerHelper;
import xyz.imxqd.wearmusic.helpers.PreferenceHelper;
import xyz.imxqd.wearmusic.models.MusicInfo;
import xyz.imxqd.wearmusic.ui.activities.PlayActivity;
import xyz.imxqd.wearmusic.utils.Config;
import xyz.imxqd.wearmusic.utils.Constants;
import xyz.imxqd.wearmusic.utils.RandomUitl;

import static xyz.imxqd.wearmusic.utils.Constants.PlayMode.PLAY_MODE_REPEAT_LIST;
import static xyz.imxqd.wearmusic.utils.Constants.PlayMode.PLAY_MODE_REPEAT_ONE;
import static xyz.imxqd.wearmusic.utils.Constants.PlayMode.PLAY_MODE_REPEAT_RAND;
import static xyz.imxqd.wearmusic.utils.Constants.PlayMode.PlayModeValue;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, HeadSetHelper.OnHeadSetListener {
    private static final String TAG = "PlayService";

    public static final String ARG_MUSIC_LIST = "arg_music_list"; // 初始的音乐列表

    private static final int REQUEST_CODE_FOREGROUND = 1;
    private static final int REQUEST_CODE_NOTIFY_CTRL = 3;

    private MediaPlayer player;
    private PlayBinder binder;

    private ArrayList<Long> mIdList;
    private ArrayList<MusicInfo> mMusicInfoList;
    private long currentId = -1;

    private boolean isFirstStart = true;
    private OnSongStateChangedListener mSongChangeListener = null;
    private OnSongListCompleteListener mListCompetedListener = null;

    private RemoteViews mRemoteViews;

    private Intent mCtrl = null;

    private int mCurrentMode = PLAY_MODE_REPEAT_LIST;

    public PlayService() {
        binder = new PlayBinder();
        player = new MediaPlayer();
        mIdList = new ArrayList<>();
        mMusicInfoList = new ArrayList<>();
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        mCtrl = new Intent(this, PlayService.class);

        if (PreferenceHelper.getHeadSetEnable()) {
            HeadSetHelper.getInstance().open(this);
            HeadSetHelper.getInstance().setOnHeadSetListener(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        Log.d(TAG, "intent action = " + intent.getAction());
        if (intent.getParcelableArrayListExtra(ARG_MUSIC_LIST) != null) {
            mIdList.clear();
            mMusicInfoList = intent.getParcelableArrayListExtra(ARG_MUSIC_LIST);
            for(MusicInfo info: mMusicInfoList) {
                mIdList.add(info.getSongId());
            }
            Log.d(TAG, "mMusicInfoList size is " + mMusicInfoList.size());
            if (isFirstStart) {
                if (mIdList.size() > 0) {
                    binder.load(mIdList.get(0));
                }
            }
            isFirstStart = false;
        } else {
            String action = intent.getAction();
            if(action != null) {
                switch (action) {
                    case Constants.Action.PLAY_ACTION_PAUSE:
                        binder.pause();
                        break;
                    case Constants.Action.PLAY_ACTION_NEXT:
                        binder.next();
                        break;
                    case Constants.Action.PLAY_ACTION_PLAY:
                        binder.play();
                        break;
                }
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        isFirstStart = true;
        if (PreferenceHelper.getHeadSetEnable()) {
            HeadSetHelper.getInstance().removeHeadSetListener();
            HeadSetHelper.getInstance().close(this);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "song #"+ currentId + " completed.");
        if(currentId == mIdList.get(mIdList.size() - 1) && mListCompetedListener != null) {
            mListCompetedListener.onListCompeted(mMusicInfoList);
        }
        binder.next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError");
        Log.d(TAG, "what: " + what);
        Log.d(TAG, "extra: " + extra);
        return true;
    }

    public PendingIntent getPendingIntent(String action) {
        mCtrl.setAction(action);
        return PendingIntent.getService(this, 3, mCtrl, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void NotifyNotification() {
        Intent intent = new Intent(PlayService.this, PlayActivity.class);
        MusicInfo info = binder.getCurrentMusicInfo();
        mRemoteViews.setTextViewText(R.id.music_artist, info.getArtist());
        mRemoteViews.setTextViewText(R.id.music_title, info.getTitle());
        Bitmap icon = info.getAlbumIcon();
        if(icon != null) {
            mRemoteViews.setImageViewBitmap(R.id.music_icon, info.getAlbumIcon());
        } else {
            mRemoteViews.setImageViewResource(R.id.music_icon, R.drawable.music);
        }

        if(!binder.isPlaying()) {
            mRemoteViews.setImageViewResource(R.id.music_play, R.drawable.ic_play_arrow_white_36dp);
            mRemoteViews.setOnClickPendingIntent(R.id.music_play,
                    getPendingIntent(Constants.Action.PLAY_ACTION_PLAY));
        } else {
            mRemoteViews.setImageViewResource(R.id.music_play, R.drawable.ic_pause_white_36dp);
            mRemoteViews.setOnClickPendingIntent(R.id.music_play,
                    getPendingIntent(Constants.Action.PLAY_ACTION_PAUSE));
        }

        mRemoteViews.setOnClickPendingIntent(R.id.music_next,
                getPendingIntent(Constants.Action.PLAY_ACTION_NEXT));

        Notification notification = new NotificationCompat.Builder(PlayService.this)
                .setContent(mRemoteViews)
                .setSmallIcon(R.drawable.ic_music_note_white_24dp)
                .setContentIntent(PendingIntent.
                        getActivity(PlayService.this, REQUEST_CODE_NOTIFY_CTRL,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        startForeground(REQUEST_CODE_FOREGROUND, notification);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared");
    }

    @Override
    public void onClick() {
        if (!PreferenceHelper.getHeadSetEnable()) {
            return;
        }
        if (binder.isPlaying()) {
            binder.pause();
        } else {
            binder.play();
        }
    }

    @Override
    public void onDoubleClick() {
        if (!PreferenceHelper.getHeadSetEnable()) {
            return;
        }
        binder.next();
    }

    @Override
    public void onHeadSetPullOut() {
        if (!PreferenceHelper.getAutoPauseEnable()) {
            return;
        }
        binder.pause();
    }

    @Override
    public void onHeadSetPushIn() {

    }

    public class PlayBinder extends Binder {
        private boolean isChanged = false;

        public void setList(ArrayList<MusicInfo> musicInfoList) {
            mIdList.clear();
            mMusicInfoList = musicInfoList;
            for(MusicInfo info: musicInfoList) {
                mIdList.add(info.getSongId());
            }
            currentId = mIdList.get(0);
        }


        public void append(MusicInfo info) {
            mMusicInfoList.add(info);
            mIdList.add(info.getSongId());
        }

        public void remove(MusicInfo info) {
            mMusicInfoList.remove(info);
            mIdList.remove(info.getSongId());
        }

        public void setMode(@PlayModeValue int mode) {
            mCurrentMode = mode;
        }

        public@PlayModeValue int getMode() {
            return mCurrentMode;
        }

        public void setOnSongChangeListener(OnSongStateChangedListener listener) {
            mSongChangeListener = listener;
        }

        public void setOnSongListCompleteListener(OnSongListCompleteListener listener) {
            mListCompetedListener = listener;
        }

        public void load(long id) {
            Log.d(TAG, "loading song #" + id);
            if (currentId == id) {
                return;
            }

            try {
                int pos = mIdList.indexOf(id);
                if(pos < 0) {
                    // TODO: 2016/8/11 扫描媒体文件
                    return;
                }
                Uri uri = mMusicInfoList.get(pos).getSongUrl();
                currentId = id;

                player.stop();
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(getApplicationContext(), uri);
                player.prepare();
                if(mSongChangeListener != null) {
                    isChanged = true;
                    mSongChangeListener.onSongChange(mMusicInfoList.get(pos));
                }
            } catch (IOException e) {
                mSongChangeListener.onNotFoundError();
                MediaScanerHelper.remove(currentId);
                if(Config.isDebug) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "song #" + id + " loaded.");
        }

        public void play() {
            if(!player.isPlaying() && getCurrentMusicInfo() != null) {
                player.start();
                NotifyNotification();
                if (isChanged) {
                    mSongChangeListener.onSongChanged(getCurrentMusicInfo());
                    isChanged = false;
                }
            }
        }

        public boolean isPlaying() {
            return player.isPlaying();
        }

        public void pause() {
            if(player.isPlaying()) {
                player.pause();
                NotifyNotification();
                stopForeground(false);
            }
        }

        public void seekTo(int duration) {
            if(player.isPlaying()) {
                player.seekTo(duration);
            }
        }

        public void next() {
            if (mIdList.size() == 0) {
                return;
            }
            int pos = mIdList.indexOf(currentId);
            long id = mIdList.get((pos + 1) % mIdList.size());
            switch (mCurrentMode) {
                case PLAY_MODE_REPEAT_ONE:
                    id = mIdList.get(pos);
                    break;
                case PLAY_MODE_REPEAT_RAND:
                    id = mIdList.get(RandomUitl.getInt(mIdList.size()));
                    break;
                case PLAY_MODE_REPEAT_LIST:
                default:
            }

            load(id);
            play();
        }

        public void previous() {
            if (mIdList.size() == 0) {
                return;
            }
            int pos = mIdList.indexOf(currentId);
            if(pos <= 0) {
                pos = mIdList.size();
            }
            long id = mIdList.get(pos - 1);
            switch (mCurrentMode) {
                case PLAY_MODE_REPEAT_ONE:
                    id = mIdList.get(pos);
                    break;
                case PLAY_MODE_REPEAT_RAND:
                    id = mIdList.get(RandomUitl.getInt(mIdList.size()));
                    break;
                case PLAY_MODE_REPEAT_LIST:
                default:
            }

            load(id);
            play();
        }

        public long getDuration() {
            if(player != null) {
                return player.getDuration();
            } else {
                return 0;
            }
        }

        public MusicInfo getCurrentMusicInfo() {
            if (currentId == -1) {
                return null;
            }
            int pos = mIdList.indexOf(currentId);
            return mMusicInfoList.get(pos);
        }

        public MusicInfo getPreviousMusicInfo() {
            if (currentId == -1) {
                return null;
            }
            int pos = mIdList.indexOf(currentId);
            return mMusicInfoList.get(pos);
        }

        public MusicInfo getNextMusicInfo() {
            if (currentId == -1) {
                return null;
            }
            int pos = mIdList.indexOf(currentId);
            return mMusicInfoList.get(pos);
        }

        public int getCurrentPosition() {
            if(player != null) {
                return player.getCurrentPosition();
            } else {
                return 0;
            }
        }
    }

    public interface OnSongStateChangedListener {
        void onSongChange(MusicInfo info);
        void onSongChanged(MusicInfo info);
        void onNotFoundError();
    }

    public interface OnSongListCompleteListener {
        void onListCompeted(ArrayList<MusicInfo> infos);
    }
}
