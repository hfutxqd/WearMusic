package xyz.imxqd.wearmusic.ui.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.models.db.SongSheet;

/**
 * Created by imxqd on 2016/8/17.
 * 歌单列表的适配器
 */
public class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.SheetHolder> {

    private ArrayList<SongSheet> mList;

    public SheetAdapter() {
        mList = (ArrayList<SongSheet>) SongSheet.find(SongSheet.class, null);
    }

    @Override
    public SheetHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new SheetHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.sheet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SheetHolder holder, int position) {
        SongSheet item = mList.get(position);
        holder.name.setText(holder.itemView.getResources().
                getString(R.string.sheet_name, item.getName(), item.getMusicCount()));
        Uri uri = item.getCover();
        if (uri == null) {
            if (item.getId() == 1) { // 我的最爱
                holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.icon.setImageResource(R.drawable.piano_player);
            } else if (item.getId() == 2) { // 我的手表
                holder.icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                holder.icon.setImageResource(R.drawable.wear_music);
            }
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .asBitmap()
                    .placeholder(R.drawable.piano_player)
                    .into(holder.icon);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class SheetHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;

        public SheetHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.sheet_icon);
            name = (TextView) itemView.findViewById(R.id.sheet_name);
        }
    }
}
