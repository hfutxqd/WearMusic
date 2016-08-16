package xyz.imxqd.wearmusic.services;

import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.android.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static xyz.imxqd.wearmusic.utils.Constants.WatchCMD.*;


public class DataService extends WearableListenerService {
    private static final String TAG = "DataService";

    private MobvoiApiClient mMobvoiApiClient;


    public DataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mMobvoiApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LOGD(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        if(!mMobvoiApiClient.isConnected()) {
            ConnectionResult connectionResult = mMobvoiApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                return;
            }
        }

        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        LOGD(TAG, "onMessageReceived: " + messageEvent);
        Toast.makeText(this, messageEvent.getPath(), Toast.LENGTH_LONG).show();
        if (GET_STORAGE_INFO_PATH.equals(messageEvent.getPath())) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            JsonObject object = new JsonObject();
            object.addProperty("Available", sf.getAvailableBytes());
            object.addProperty("Total", sf.getTotalBytes());
            String json = object.toString();
            byte[] data = json.getBytes(Charset.forName("UTF-8"));
            Wearable.MessageApi.sendMessage(mMobvoiApiClient, null, GET_STORAGE_INFO_PATH, data);
        } else if (GET_FILE_LIST_PATH.equals(messageEvent.getPath())) {
            String json = new String(messageEvent.getData(), Charset.forName("UTF-8"));
            Toast.makeText(this, json, Toast.LENGTH_LONG).show();
            try {
                JSONObject object = new JSONObject(json);
                String path = object.getString("path");
                File dir = new File(path);
                File[] files = dir.listFiles();
                Gson gson = new Gson();
                String res = gson.toJson(files);

                JsonObject ob = new JsonObject();
                ob.addProperty("path", path);
                ob.addProperty("filesJson", res);
                byte[] data = ob.toString().getBytes(Charset.forName("UTF-8"));
                Toast.makeText(this, "sending file list...", Toast.LENGTH_LONG).show();
                Wearable.MessageApi.sendMessage(mMobvoiApiClient, null, GET_FILE_LIST_PATH, data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (GET_SDCARD_PATH.equals(messageEvent.getPath())) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            JsonObject object = new JsonObject();
            object.addProperty("path", path);
            String json = object.toString();
            byte[] data = json.getBytes(Charset.forName("UTF-8"));
            Wearable.MessageApi.sendMessage(mMobvoiApiClient, null, GET_SDCARD_PATH, data);
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        System.out.println(peer.getDisplayName());
        LOGD(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        System.out.println(peer.getDisplayName());
        LOGD(TAG, "onPeerDisconnected: " + peer);
    }

    public static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }
}
