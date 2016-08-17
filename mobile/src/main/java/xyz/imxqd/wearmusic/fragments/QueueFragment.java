package xyz.imxqd.wearmusic.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.imxqd.wearmusic.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QueueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QueueFragment extends Fragment {

    private View mRoot;
    private RecyclerView mQueueList;

    public QueueFragment() {
        // Required empty public constructor
    }

    public static QueueFragment newInstance() {
        return new QueueFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_queue, container, false);
        mQueueList = (RecyclerView) mRoot.findViewById(R.id.queue_list);
        return mRoot;
    }

}
