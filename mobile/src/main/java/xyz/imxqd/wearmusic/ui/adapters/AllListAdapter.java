package xyz.imxqd.wearmusic.ui.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.models.MusicInfo;
import xyz.imxqd.wearmusic.utils.TimeUtils;

/**
 * Created by imxqd on 2016/8/7.
 * 音乐列表的适配器
 */
public class AllListAdapter extends RecyclerView.Adapter<AllListAdapter.MusicViewHolder> {
    private Context mContext;
    private ArrayList<MusicInfo> mList;
    private OnItemClickListener mListener = null;

    public AllListAdapter(Context context) {
        super();
        mContext = context;
        mList = new ArrayList<>();
        updateData();
    }

    public AllListAdapter(Context context, ArrayList<MusicInfo> list) {
        mContext = context;
        mList = list;
    }

    public void updateData() {
        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, "duration > 30000", null,
                null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MusicInfo info = new MusicInfo();
                int index = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                info.setTitle(cursor.getString(index));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                info.setDuration(cursor.getInt(index));
                info.setDurationString(TimeUtils.getTimeString(cursor.getInt(index)));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                String url = cursor.getString(index);
                info.setSongUrl(Uri.parse(url));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                info.setAlbumName(cursor.getString(index));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                info.setArtist(cursor.getString(index));

                index = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                info.setSongId(cursor.getLong(index));

                index = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                long albumId = cursor.getLong(index);
                info.setAlbumId(albumId);
                mList.add(info);
            }
            cursor.close();
        }
    }



    public ArrayList<MusicInfo> getList() {
        return mList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MusicViewHolder holder, int position) {
        final MusicInfo info = mList.get(position);
        Glide.with(mContext)
                .loadFromMediaStore(info.getAlbumIconUri())
                .asBitmap()
                .placeholder(R.drawable.music)
                .into(holder.icon);

        holder.title.setText(info.getTitle());
        holder.artist.setText(info.getArtist());
        holder.time.setText(info.getDurationString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onItemClick(holder);
                }
            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(mContext, holder.menu);
                menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (mListener != null) {
                            return mListener.onPopupMenuItemSelected(item, holder);
                        } else {
                            return false;
                        }
                    }
                });
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public MusicInfo getItem(int position) {
        return mList.get(position);
    }



    public interface OnItemClickListener {
        void onItemClick(MusicViewHolder holder);
        boolean onPopupMenuItemSelected(MenuItem item, MusicViewHolder holder);
    }
    public class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView icon, menu;
        TextView title, artist, time;
        public MusicViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.music_icon);
            menu = (ImageView) itemView.findViewById(R.id.music_menu);
            title = (TextView) itemView.findViewById(R.id.music_title);
            artist = (TextView) itemView.findViewById(R.id.music_artist);
            time = (TextView) itemView.findViewById(R.id.music_time);
        }
    }
}
