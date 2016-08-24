package xyz.imxqd.wearmusic.ui.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.ui.adapters.AllListAdapter;
import xyz.imxqd.wearmusic.services.PlayService;
import xyz.imxqd.wearmusic.utils.App;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private AllListAdapter mAdapter;

    private PlayService.PlayBinder playBinder = null;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isConnected = true;
            playBinder = (PlayService.PlayBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isConnected = false;
            playBinder = null;
        }
    };
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViews();
        initView();
        setupEvents();
        initService();
    }

    private void initService() {
        Intent intent = new Intent(App.getApp(), PlayService.class);
        intent.putExtra(PlayService.ARG_MUSIC_LIST, mAdapter.getList());
        startService(intent);
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

    private void findViews() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
    }

    private void initView() {
        mAdapter = new AllListAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupEvents() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WatchActivity.class);
                startActivity(intent);
            }
        });
        mAdapter.setOnItemClickListener(new AllListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AllListAdapter.MusicViewHolder holder) {
                int pos = holder.getAdapterPosition();
                playBinder.load(mAdapter.getItem(pos).getSongId());
                playBinder.play();
            }

            @Override
            public boolean onPopupMenuItemSelected(MenuItem item, AllListAdapter.MusicViewHolder holder) {
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
