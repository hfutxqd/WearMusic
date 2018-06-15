package xyz.imxqd.wearmusic.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.models.MusicInfo;
import xyz.imxqd.wearmusic.services.PlayService;
import xyz.imxqd.wearmusic.utils.App;
import xyz.imxqd.wearmusic.utils.TimeUtils;
import xyz.imxqd.wearmusic.views.LrcView;

import static xyz.imxqd.wearmusic.utils.Constants.PlayMode.PLAY_MODE_REPEAT_LIST;
import static xyz.imxqd.wearmusic.utils.Constants.PlayMode.PLAY_MODE_REPEAT_ONE;
import static xyz.imxqd.wearmusic.utils.Constants.PlayMode.PLAY_MODE_REPEAT_RAND;

public class PlayActivity extends AppCompatActivity implements PlayService.OnSongStateChangedListener, SeekBar.OnSeekBarChangeListener {

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private ImageView mMusicIcon;
    private ImageView mPlayBtn;
    private ImageView mModeBtn;
    private AppCompatSeekBar mSeekBar;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    private ViewGroup mPlayRoot;

    private LrcView mLrcView;

    private PlayService.PlayBinder playBinder = null;
    private MusicInfo mCurrentMusic;
    private Timer mProgressUpdater = new Timer(true);

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isConnected = true;
            playBinder = (PlayService.PlayBinder) iBinder;
            mCurrentMusic = playBinder.getCurrentMusicInfo();
            initMusicUi();
            initModeUi();
            playBinder.setOnSongChangeListener(PlayActivity.this);

            mProgressUpdater.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isTracking) {
                        setMusicProgress(playBinder.getCurrentPosition());
                    }
                }
            }, 0, 200);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isConnected = false;
            mProgressUpdater.cancel();
            playBinder = null;
        }
    };
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initSysUi();
        initActionBar();
        findViews();
        setupEvents();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(App.getApp(), PlayService.class);
        if (!isConnected) {
            bindService(intent, connection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        if (isConnected) {
            unbindService(connection);
            isConnected = false;
        }
        super.onStop();
    }

    private void initSysUi() {
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
    }

    private void initActionBar() {
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void findViews() {
        mMusicIcon = (ImageView) findViewById(R.id.play_music_icon);
        mPlayBtn = (ImageView) findViewById(R.id.play_music_play);
        mModeBtn = (ImageView) findViewById(R.id.play_music_mode);
        mSeekBar = (AppCompatSeekBar) findViewById(R.id.play_music_seek_bar);
        mCurrentTime = (TextView) findViewById(R.id.play_music_current_time);
        mTotalTime = (TextView) findViewById(R.id.play_music_total_time);
        mPlayRoot = (ViewGroup) findViewById(R.id.play_root);

        mLrcView = (LrcView) findViewById(R.id.play_music_lrc);

    }

    private void initModeUi() {
        switch (playBinder.getMode()) {
            case PLAY_MODE_REPEAT_ONE:
                mModeBtn.setImageResource(R.drawable.play_icn_one_selector);
                break;
            case PLAY_MODE_REPEAT_RAND:
                mModeBtn.setImageResource(R.drawable.play_icn_shuffle_selector);
                break;
            case PLAY_MODE_REPEAT_LIST:
                mModeBtn.setImageResource(R.drawable.play_icn_loop_selector);
            default:
        }
    }

    private void setupEvents() {
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void initMusicUi() {
        if (mCurrentMusic == null) {
            return;
        }
        Bitmap bitmap = mCurrentMusic.getAlbumIcon();
        if (bitmap != null) {
            Palette palette = new Palette.Builder(bitmap)
                    .generate();
            int color = palette.getDarkVibrantColor(getResources().getColor(R.color.play_bg_color));
            mPlayRoot.setBackgroundColor(color);
            mMusicIcon.setImageBitmap(bitmap);
        } else {
            mPlayRoot.setBackgroundResource(R.color.play_bg_color);
            mMusicIcon.setImageResource(R.drawable.music);
        }

        mActionBar.setTitle(mCurrentMusic.getTitle());
        mActionBar.setSubtitle(mCurrentMusic.getArtist());
        mSeekBar.setMax(mCurrentMusic.getDuration());
        mCurrentTime.setText(TimeUtils.getTimeString(playBinder.getCurrentPosition()));
        mTotalTime.setText(mCurrentMusic.getDurationString());
        if (playBinder.isPlaying()) {
            mPlayBtn.setImageResource(R.drawable.ic_pause_white_36dp);
        } else {
            mPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_love) {
            item.setIcon(R.drawable.ic_favorite_red_400_24dp);
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void setMusicProgress(final int progress) {
        if (playBinder.isPlaying()) {
            mSeekBar.setProgress(progress);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (playBinder.isPlaying()) {
                    mPlayBtn.setImageResource(R.drawable.ic_pause_white_36dp);
                } else {
                    mPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_36dp);
                }
            }
        });
    }

    @Override
    public void onSongChange(MusicInfo info) {
        mCurrentMusic = playBinder.getCurrentMusicInfo();
        initMusicUi();
    }

    @Override
    public void onSongChanged(MusicInfo info) {
        if (playBinder.isPlaying()) {
            mPlayBtn.setImageResource(R.drawable.ic_pause_white_36dp);
        } else {
            mPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        }
    }

    @Override
    public void onNotFoundError() {
        Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
    }

    public void onModeClick(View view) {
        int mode = (playBinder.getMode() + 1) % 3;
        playBinder.setMode(mode);
        initModeUi();
    }

    public void onListClick(View view) {

    }

    public void onPreviousClick(View view) {
        playBinder.previous();

    }

    public void onPlayClick(View view) {
        if (playBinder.isPlaying()) {
            playBinder.pause();
            mPlayBtn.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        } else {
            playBinder.play();
            mPlayBtn.setImageResource(R.drawable.ic_pause_white_36dp);
        }
    }

    public void onNextClick(View view) {
        playBinder.next();
    }

    private boolean isTracking = false;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mCurrentTime.setText(TimeUtils.getTimeString(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTracking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTracking = false;
        playBinder.seekTo(seekBar.getProgress());
    }
}
