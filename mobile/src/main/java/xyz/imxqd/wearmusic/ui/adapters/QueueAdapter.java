package xyz.imxqd.wearmusic.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.models.MusicInfo;

/**
 * Created by imxqd on 2016/8/17.
 * 播放队列的适配器
 */
public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueHolder> {

    private ArrayList<MusicInfo> mList;

    public QueueAdapter(ArrayList<MusicInfo> list) {
        mList = list;
    }

    @Override
    public QueueHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QueueHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.queue_item, parent, false));
    }

    @Override
    public void onBindViewHolder(QueueHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class QueueHolder extends RecyclerView.ViewHolder {
        public QueueHolder(View itemView) {
            super(itemView);
        }
    }
}
