package xyz.imxqd.wearmusic.ui.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CircularButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.models.MusicInfo;
import xyz.imxqd.wearmusic.services.PlayService;
import xyz.imxqd.wearmusic.utils.StepsUtils;
import xyz.imxqd.wearmusic.utils.TimeUtils;
import xyz.imxqd.wearmusic.widgets.CircularSeekBar;

public class MainActivity extends WearableActivity implements CircularSeekBar.OnCircularSeekBarChangeListener, PlayService.OnSongChangedListener {

    public static String ARG_MUSIC_INFO = "music_info_key";

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.CHINESE);

    private BoxInsetLayout mContainerView;
    private Drawable mBackground;
    private CircularButton mBtPlay, mBtPrevious, mBtNext;
    private CircularSeekBar mSeekBar;
    private TextView mTvCurrentTime, mTvMusicTitle, mTvSystemTime, mTvSystemSteps;
    ViewGroup mVDisplayOn, mVDisplayOff;

    private MusicInfo mMusicInfo = null;
    private boolean isConnected = false;
    private PlayService.PlayBinder playBinder = null;
    private Timer mTimer = new Timer(true);
    private Toast mToast = null;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isConnected = true;
            playBinder = (PlayService.PlayBinder) iBinder;
            playBinder.setOnSongChangeListener(MainActivity.this);
            playBinder.load(mMusicInfo.getSongId());
            start();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isConnected = false;
            playBinder = null;
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what > 0) {
                mSeekBar.setProgress(msg.what);
                mTvSystemTime.setText(AMBIENT_DATE_FORMAT.format(new Date()));
                mTvSystemSteps.setText(getString(R.string.current_steps, StepsUtils.getTicSteps()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        mMusicInfo = getIntent().getParcelableExtra(ARG_MUSIC_INFO);
        findViews();
        initViews();
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void findViews() {
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mBtPlay = (CircularButton) findViewById(R.id.bt_play);
        mBtPrevious = (CircularButton) findViewById(R.id.bt_pre);
        mBtNext = (CircularButton) findViewById(R.id.bt_next);
        mSeekBar = (CircularSeekBar) findViewById(R.id.seek_bar);
        mTvCurrentTime = (TextView) findViewById(R.id.current_time);
        mTvMusicTitle = (TextView) findViewById(R.id.music_title);
        mTvSystemTime = (TextView) findViewById(R.id.current_system_time);
        mTvSystemSteps = (TextView) findViewById(R.id.current_system_steps);
        mVDisplayOn = (ViewGroup) findViewById(R.id.display_on);
        mVDisplayOff = (ViewGroup) findViewById(R.id.display_off);
    }

    @SuppressLint("ShowToast")
    private void initViews() {
        mTvMusicTitle.setText(mMusicInfo.getTitle());
        mTvSystemSteps.setText(getString(R.string.current_steps, StepsUtils.getTicSteps()));
        mTvCurrentTime.setText(getString(R.string.current_time, "0:00",mMusicInfo.getDurationString()));
        initBackground();
        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        mSeekBar.setMax(mMusicInfo.getDuration());
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, PlayService.class), connection, BIND_AUTO_CREATE);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(playBinder.isPlaying() && !isTracking) {
                    mHandler.sendEmptyMessage(playBinder.getCurrentPosition());
                }
            }
        }, 500, 500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isConnected) {
            unbindService(connection);
        }
        mTimer.cancel();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mVDisplayOff.setVisibility(View.VISIBLE);
            mVDisplayOn.setVisibility(View.INVISIBLE);
            mContainerView.setBackgroundColor(Color.BLACK);
            mTvMusicTitle.setTextColor(Color.WHITE);
            mTvCurrentTime.setTextColor(Color.WHITE);
            mSeekBar.setCircleColor(Color.WHITE);
            mSeekBar.setCircleProgressColor(Color.WHITE);
            mSeekBar.setPointerColor(Color.WHITE);
            mSeekBar.setPointerHaloColor(Color.WHITE);
        } else {
            initBackground();
            mVDisplayOff.setVisibility(View.INVISIBLE);
            mVDisplayOn.setVisibility(View.VISIBLE);
            mContainerView.setBackground(mBackground);
            mTvMusicTitle.setTextColor(Color.BLACK);
            mTvCurrentTime.setTextColor(Color.BLACK);
            mSeekBar.setCircleColor(Color.BLACK);
            mSeekBar.setCircleProgressColor(getResources().getColor(R.color.colorAccent));
            mSeekBar.setPointerColor(getResources().getColor(R.color.colorAccentDark));
            mSeekBar.setPointerHaloColor(getResources().getColor(R.color.colorAccentDark));
        }
    }

    public void start() {
        mBtPlay.setImageResource(R.drawable.ic_pause_black_36dp);
        playBinder.play();
        playBinder.seekTo(mSeekBar.getProgress());
    }

    public void pause() {
        mBtPlay.setImageResource(R.drawable.ic_play_arrow_black_36dp);
        playBinder.pause();
    }

    public void onPlayClick(View view) {
        if(isConnected) {
            if(playBinder.isPlaying()) {
                pause();
            } else {
                start();
            }
        }
    }

    public void onPreviousClick(View view) {
        if(isConnected) {
            playBinder.previous();
        }
    }

    public void onNextClick(View view) {
        if(isConnected) {
            playBinder.next();
        }
    }

    private boolean isTracking = false;

    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
        mTvCurrentTime.setText(getString(R.string.current_time,
                TimeUtils.getTimeString(progress),mMusicInfo.getDurationString()));
    }

    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {
        isTracking = false;
        if(isConnected) {
            playBinder.seekTo(seekBar.getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {
        isTracking = true;
    }

    @Override
    public void onSongChanged(MusicInfo info) {
        mMusicInfo = info;
        mTvMusicTitle.setText(mMusicInfo.getTitle());
        mSeekBar.setMax(info.getDuration());
        initBackground();
    }

    private void initBackground() {
        if(!isAmbient()) {
            Glide.with(this)
                    .loadFromMediaStore(mMusicInfo.getAlbumIconUri())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable drawable = new BitmapDrawable(resource);
                            mContainerView.setBackground(drawable);
                            mBackground = mContainerView.getBackground();
                        }
                    });
        }
    }


    public boolean onScrollSidePanel(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int vol = currentVolume + (int) distanceY / 10;
        if(vol > maxVolume) {
            vol = maxVolume;
        } else if (vol < 0){
            vol = 0;
        }
        mToast.setText(getString(R.string.volume, vol, maxVolume));
        mToast.show();
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol , 0);
        return true;
    }
}
