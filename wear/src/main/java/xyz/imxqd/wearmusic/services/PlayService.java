package xyz.imxqd.wearmusic.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import xyz.imxqd.wearmusic.models.MusicInfo;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "PlayService";
    public static final String ARG_MUSIC_LIST = "arg_music_list";

    private MediaPlayer player;
    private PlayBinder binder;

    private ArrayList<Long> mIdList;
    private ArrayList<MusicInfo> mMusicInfoList;
    private long currentId = -1;

    private boolean isFirstStart = true;
    private OnSongChangedListener mListener = null;

    public PlayService() {
        binder = new PlayBinder();
        player = new MediaPlayer();
        mIdList = new ArrayList<>();
        mMusicInfoList = new ArrayList<>();
        player.setOnCompletionListener(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isFirstStart) {
            mMusicInfoList = intent.getParcelableArrayListExtra(ARG_MUSIC_LIST);
            for(MusicInfo info: mMusicInfoList) {
                mIdList.add(info.getSongId());
            }
            Log.d(TAG, "mMusicInfoList size is " + mMusicInfoList.size());
            isFirstStart = false;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isFirstStart = true;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "song #"+ currentId + " completed.");
        binder.next();
    }

    public class PlayBinder extends Binder {
        public void setList(ArrayList<MusicInfo> musicInfoList) {
            mIdList.clear();
            mMusicInfoList = musicInfoList;
            for(MusicInfo info: musicInfoList) {
                mIdList.add(info.getSongId());
            }
            currentId = mIdList.get(0);
        }

        public void setOnSongChangeListener(OnSongChangedListener listener) {
            mListener = listener;
        }

        public void load(long id) {
            Log.d(TAG, "loading song #" + id);
            if (currentId == id) {
                return;
            }

            try {
                int pos = mIdList.indexOf(id);
                Uri uri = mMusicInfoList.get(pos).getSongUrl();
                player.stop();
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(getApplicationContext(), uri);
                player.prepare();
                currentId = id;
                if(mListener != null) {
                    mListener.onSongChanged(mMusicInfoList.get(pos));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "song #" + id + " loaded.");
        }

        public void play() {
            if(!player.isPlaying()) {
                player.start();
            }
        }

        public boolean isPlaying() {
            return player.isPlaying();
        }

        public void pause() {
            if(player.isPlaying()) {
                player.pause();
            }
        }

        public void seekTo(int duration) {
            if(player.isPlaying()) {
                player.seekTo(duration);
            }
        }

        public void next() {
            int pos = mIdList.indexOf(currentId);
            long id = mIdList.get((pos + 1) % mIdList.size());
            load(id);
            play();
        }

        public void previous() {
            int pos = mIdList.indexOf(currentId);
            if(pos == 0) {
                pos = mIdList.size();
            }
            long id = mIdList.get(pos - 1);
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

    public interface OnSongChangedListener {
        void onSongChanged(MusicInfo info);
    }
}
