package xyz.imxqd.wearmusic.activities;

import android.animation.ObjectAnimator;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.utils.StorageUtils;

import static xyz.imxqd.wearmusic.utils.Constants.WatchCMD.*;

public class WatchActivity extends AppCompatActivity implements MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener, DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener {

    private static final String TAG = "WatchActivity";

    private MobvoiApiClient mMobvoiApiClient;
    private boolean mResolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1000;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private CircleProgressBar mProgressBar;
    private TextView mTvWatchStorage;

    private long mTotalStorage = 0;
    private long mAvailableStorage = 0;
    private String mSdcardPath = null;
    private HashMap<String, File[]> mFilesMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        initWearApi();
        findViews();
        initViews();
        setupEvents();
    }

    private void initWearApi() {
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mProgressBar = (CircleProgressBar) findViewById(R.id.line_progress);
        mTvWatchStorage = (TextView) findViewById(R.id.watch_storage);

    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupEvents() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mResolvingError) {
            mMobvoiApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        if (!mResolvingError) {
            Wearable.DataApi.removeListener(mMobvoiApiClient, this);
            Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
            Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
            mMobvoiApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mMobvoiApiClient, this);
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);

        Wearable.MessageApi.sendMessage(mMobvoiApiClient, null, GET_STORAGE_INFO_PATH, null);

        Wearable.MessageApi.sendMessage(mMobvoiApiClient, null, GET_SDCARD_PATH, null);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mMobvoiApiClient.connect();
            }
        } else {
            mResolvingError = false;
            Wearable.DataApi.removeListener(mMobvoiApiClient, this);
            Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
            Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);
        dataEventBuffer.close();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (GET_STORAGE_INFO_PATH.equals(messageEvent.getPath())) {
            String json = new String(messageEvent.getData(), Charset.forName("UTF-8"));
            onReceivedStorageInfo(json);
        } else if (GET_SDCARD_PATH.equals(messageEvent.getPath())) {
            String json = new String(messageEvent.getData(), Charset.forName("UTF-8"));
            try {
                JSONObject ob = new JSONObject(json);
                mSdcardPath = ob.getString("path");
                Log.d(TAG, "sdcard path: " + mSdcardPath);
                Wearable.MessageApi.sendMessage(mMobvoiApiClient, null,
                        GET_FILE_LIST_PATH, ob.toString().getBytes(Charset.forName("UTF-8")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (GET_FILE_LIST_PATH.equals(messageEvent.getPath())) {
            String json = new String(messageEvent.getData(), Charset.forName("UTF-8"));
            Log.d(TAG, json);
            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(json);
            String path = object.get("path").getAsString();
            String filesJson = object.get("filesJson").getAsString();
            Gson gson = new Gson();
            File[] files = gson.fromJson(filesJson, File[].class);
            mFilesMap.put(path, files);
        }
    }

    @Override
    public void onPeerConnected(Node node) {
        mTvWatchStorage.setText(R.string.watch_connected);
    }

    @Override
    public void onPeerDisconnected(Node node) {
        mTvWatchStorage.setText(R.string.watch_not_connected);
    }

    private void onReceivedStorageInfo(String json) {
        Log.d(TAG, json);
        try {
            final JSONObject object = new JSONObject(json);
            mTotalStorage = object.getLong("Total");
            mAvailableStorage = object.getLong("Available");
            final String total = StorageUtils.getString(mTotalStorage);
            final String available = StorageUtils.getString(mAvailableStorage);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setMax((int) mTotalStorage);
                    ObjectAnimator.ofInt(mProgressBar, "progress", 0, (int)(mTotalStorage - mAvailableStorage))
                            .setDuration(1000)
                            .start();
                    mTvWatchStorage.setText(getString(R.string.watch_storage, total, available));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
