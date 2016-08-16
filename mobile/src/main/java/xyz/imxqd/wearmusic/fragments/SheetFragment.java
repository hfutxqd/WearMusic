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
 */
public class SheetFragment extends Fragment {

    private View mRoot;
    private RecyclerView mSheetList;


    public SheetFragment() {
        // Required empty public constructor
    }

    public static SheetFragment newInstance() {
        return new SheetFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_sheet_fragments, container, false);
        mSheetList = (RecyclerView) mRoot.findViewById(R.id.sheet_list);
        return mRoot;
    }

}
