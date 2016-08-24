package xyz.imxqd.wearmusic.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.ui.fragments.AllMusicFragment;
import xyz.imxqd.wearmusic.ui.fragments.QueueFragment;
import xyz.imxqd.wearmusic.ui.fragments.SheetFragment;
import xyz.imxqd.wearmusic.models.MusicInfo;
import xyz.imxqd.wearmusic.services.PlayService;
import xyz.imxqd.wearmusic.utils.App;
import xyz.imxqd.wearmusic.utils.MusicListUtils;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlayService.OnSongStateChangedListener, AllMusicFragment.OnAllMusicFragmentListener {

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private View mNavHeader;
    private ImageView mNavHeaderIcon;
    private TextView mNavHeaderTitle;
    private TextView mNavHeaderArtist;
    private Toolbar mToolbar;

    private LinearLayout llPlayBar;
    private TextView tvPlayBarTitle;
    private TextView tvPlayBarArtist;
    private ImageView ivPlayBarIcon;
    private ImageView ivPlayBarPre;
    private ImageView ivPlayBarNext;
    private ImageView ivPlayerBarPlay;
    private View vPlayBarProgress;

    private PlayService.PlayBinder playBinder = null;
    private MusicInfo mCurrentMusic;
    private Timer mProgressUpdater = new Timer(true);

    private HashMap<Integer, Fragment> mFragmentList;
    private int mCurrentFragmentId;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isConnected = true;
            playBinder = (PlayService.PlayBinder) iBinder;
            playBinder.setOnSongChangeListener(NavigationActivity.this);
            initPlayUi();
            mProgressUpdater.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (playBinder.getCurrentMusicInfo() != null) {
                        setPlayProgress(playBinder.getCurrentPosition());
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
        setContentView(R.layout.activity_navigation);
        findViews();
        initView();
        initFragments();
        setupEvents();
        initService();
    }

    private void initService() {
        Intent intent = new Intent(App.getApp(), PlayService.class);
        intent.putExtra(PlayService.ARG_MUSIC_LIST, MusicListUtils.getMusicList());
        startService(intent);
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        llPlayBar = (LinearLayout) findViewById(R.id.play_bar_content);
        tvPlayBarTitle = (TextView) findViewById(R.id.play_bar_music_title);
        tvPlayBarArtist = (TextView) findViewById(R.id.play_bar_music_artist);
        ivPlayBarIcon = (ImageView) findViewById(R.id.play_bar_music_icon);
        ivPlayBarPre = (ImageView) findViewById(R.id.play_bar_music_previous);
        ivPlayBarNext = (ImageView) findViewById(R.id.play_bar_music_next);
        ivPlayerBarPlay = (ImageView) findViewById(R.id.play_bar_music_play);
        vPlayBarProgress = findViewById(R.id.play_bar_progress);

        mNavHeader = mNavigationView.getHeaderView(0);
        mNavHeaderIcon = (ImageView) mNavHeader.findViewById(R.id.nav_music_icon);
        mNavHeaderTitle = (TextView) mNavHeader.findViewById(R.id.nav_music_title);
        mNavHeaderArtist = (TextView) mNavHeader.findViewById(R.id.nav_music_artist);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();


    }

    private void initFragments() {
        mFragmentList = new HashMap<>(3);
        mFragmentList.put(R.id.nav_all, AllMusicFragment.newInstance(MusicListUtils.getMusicList()));
        mFragmentList.put(R.id.nav_list, SheetFragment.newInstance());
        mFragmentList.put(R.id.nav_queue, QueueFragment.newInstance());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mFragmentList.get(R.id.nav_all))
                .commit();
        mCurrentFragmentId = R.id.nav_all;
    }

    private void switchFragmentTo(int id) {
        Fragment to = mFragmentList.get(id);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, to)
                .commit();
        mCurrentFragmentId = id;
    }

    private void initPlayUi() {
        if (playBinder.getCurrentMusicInfo() != null) {
            mCurrentMusic = playBinder.getCurrentMusicInfo();
            Glide.with(this)
                    .loadFromMediaStore(mCurrentMusic.getAlbumIconUri())
                    .asBitmap()
                    .placeholder(R.drawable.music)
                    .into(ivPlayBarIcon);
            tvPlayBarTitle.setText(mCurrentMusic.getTitle());
            tvPlayBarArtist.setText(mCurrentMusic.getArtist());

            Glide.with(this)
                    .loadFromMediaStore(mCurrentMusic.getAlbumIconUri())
                    .asBitmap()
                    .placeholder(R.drawable.piano_player)
                    .into(mNavHeaderIcon);
            mNavHeaderTitle.setText(mCurrentMusic.getTitle());
            mNavHeaderArtist.setText(mCurrentMusic.getArtist());
        }
    }

    private void setPlayProgress(final int progress) {
        if (mCurrentMusic == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (playBinder.isPlaying()) {
                    ivPlayerBarPlay.setImageResource(R.drawable.ic_pause_white_36dp);
                } else {
                    ivPlayerBarPlay.setImageResource(R.drawable.ic_play_arrow_white_36dp);
                }

                int width = llPlayBar.getWidth();
                int w = (int) (progress * 1.0 / mCurrentMusic.getDuration() * width);
                ViewGroup.LayoutParams params = vPlayBarProgress.getLayoutParams();
                params.width = w;
                vPlayBarProgress.setLayoutParams(params);
            }
        });
    }

    private void setupEvents() {
        mNavigationView.setNavigationItemSelectedListener(this);
        llPlayBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });
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

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.nav_watch: {
                Intent intent = new Intent(this, WatchActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.nav_all:
            case R.id.nav_list:
            case R.id.nav_queue:
                switchFragmentTo(id);
                break;
            default:
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSongChange(MusicInfo info) {
        if (!isConnected) {
            return;
        }
        initPlayUi();
        setPlayProgress(0);
    }

    @Override
    public void onSongChanged(MusicInfo info) {
        if (!isConnected) {
            return;
        }
        if (playBinder.isPlaying()) {
            ivPlayerBarPlay.setImageResource(R.drawable.ic_pause_white_36dp);
        } else {
            ivPlayerBarPlay.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        }
    }

    @Override
    public void onNotFoundError() {
        Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
    }

    public void onPreviousClick(View view) {
        playBinder.previous();
    }

    public void onPlayClick(View view) {
        if (playBinder.isPlaying()) {
            playBinder.pause();
            ivPlayerBarPlay.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        } else {
            playBinder.play();
            ivPlayerBarPlay.setImageResource(R.drawable.ic_pause_white_36dp);
        }
    }

    public void onNextClick(View view) {
        playBinder.next();
    }

    @Override
    public void onAllMusicFragmentItemClick(MusicInfo item) {
        playBinder.load(item.getSongId());
        playBinder.play();
    }
}
