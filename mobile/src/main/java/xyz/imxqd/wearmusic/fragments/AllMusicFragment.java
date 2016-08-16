package xyz.imxqd.wearmusic.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.adapters.ListAdapter;
import xyz.imxqd.wearmusic.models.MusicInfo;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllMusicFragment extends Fragment {
    public static final String PARAM_MUSIC_LIST = "param_music_list";

    private View mRoot;
    private RecyclerView mRecyclerView;
    private ListAdapter mAdapter;
    private ArrayList<MusicInfo> mMusicList;
    private OnAllMusicFragmentListener listener = null;




    public AllMusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AllMusicFragment.
     */
    public static AllMusicFragment newInstance(ArrayList<MusicInfo> musicList) {
        AllMusicFragment fragment = new AllMusicFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PARAM_MUSIC_LIST, musicList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicList = getArguments().getParcelableArrayList(PARAM_MUSIC_LIST);
        mAdapter = new ListAdapter(getContext(), mMusicList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_all_music, container, false);
        findViews();
        initView();
        setupEvents();
        return mRoot;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnAllMusicFragmentListener) getActivity();
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.list);
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupEvents() {
        mAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListAdapter.MusicViewHolder holder) {
                int pos = holder.getAdapterPosition();
                listener.onAllMusicFragmentItemClick(mAdapter.getItem(pos));
            }
        });
    }

    public interface OnAllMusicFragmentListener {
        void onAllMusicFragmentItemClick(MusicInfo item);
    }

}
