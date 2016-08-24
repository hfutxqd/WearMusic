package xyz.imxqd.wearmusic.ui.activities;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WearableListView;

import ticwear.design.app.AlertDialog;
import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.ui.adapters.ListAdapter;
import xyz.imxqd.wearmusic.models.MusicInfo;
import xyz.imxqd.wearmusic.services.DataService;
import xyz.imxqd.wearmusic.services.PlayService;
import xyz.imxqd.wearmusic.utils.StorageUtils;

public class ListActivity extends WearableActivity implements ListAdapter.OnItemClickListener,
        PlayService.OnSongChangedListener {
    private static String TAG = "ListActivity";
    public static final String MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";

    private BoxInsetLayout mContainerView;
    private WearableListView mListView;
    private ListAdapter mAdapter;

    private boolean isConnected = false;
    private PlayService.PlayBinder mPlayService = null;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isConnected = true;
            mPlayService = (PlayService.PlayBinder) iBinder;
            mPlayService.setOnSongChangeListener(ListActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isConnected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mListView = (WearableListView) findViewById(R.id.list);
        mAdapter = new ListAdapter(this);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        sendBroadcast(new Intent(MEDIA_SCANNER_SCAN_DIR, Uri.fromFile(StorageUtils.getPublicMusicDirectory())));
        initService();
    }

    public void initService() {
        Intent intent = new Intent(this, PlayService.class);
        intent.putExtra(PlayService.ARG_MUSIC_LIST, mAdapter.getList());
        startService(intent);
        intent = new Intent(this, DataService.class);
        startService(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    public void onItemClick(ListAdapter.MusicViewHolder holder) {
        int pos = holder.getAdapterPosition();
        MusicInfo info = mAdapter.getItem(pos);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.ARG_MUSIC_INFO, info);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(ListAdapter.MusicViewHolder holder) {
        int pos = holder.getAdapterPosition();
        MusicInfo info = mAdapter.getItem(pos);
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_delete)
                .setMessage(getString(R.string.delete_message, info.getTitle()))
                .setPositiveButton(getDrawable(R.drawable.ic_done_white_24dp), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(getDrawable(R.drawable.ic_close_white_24dp), null)
                .show();
    }

    @Override
    public void onSongChanged(MusicInfo info) {

    }
}
