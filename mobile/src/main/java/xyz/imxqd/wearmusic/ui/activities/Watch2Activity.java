package xyz.imxqd.wearmusic.ui.activities;

import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.HashMap;

import xyz.imxqd.wearmusic.R;

public class Watch2Activity extends AppCompatActivity {
    private static final String TAG = "WatchActivity";

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1000;

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
        setContentView(R.layout.activity_watch2);
        findViews();
        initViews();
        setupEvents();
    }

    private void findViews() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mProgressBar = (CircleProgressBar) findViewById(R.id.line_progress);
        mTvWatchStorage = (TextView) findViewById(R.id.watch_storage);

    }

    private void initViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupEvents() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog = new BottomSheetDialog(Watch2Activity.this);
                dialog.setContentView(R.layout.nav_header_navigation);
                dialog.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
