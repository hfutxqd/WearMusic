package xyz.imxqd.wearmusic.ui.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.models.MusicInfo;
import xyz.imxqd.wearmusic.utils.TimeUtils;

/**
 * Created by imxqd on 2016/8/7.
 * 音乐列表的适配器
 */
public class ListAdapter extends WearableListView.Adapter {
    private Activity mContext;
    private ArrayList<MusicInfo> mList;
    private OnItemClickListener mListener = null;

    public ListAdapter(Activity context) {
        super();
        mContext = context;
        mList = new ArrayList<>();
        updateData();
    }

    public void updateData() {
        if (PermissionChecker.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(mContext, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return;
        } else if (PermissionChecker.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_DENIED_APP_OP) {
            return;
        }
        ContentResolver cr = mContext.getContentResolver();
        Cursor cursor = cr.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
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
    public ListAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder vholder, int position) {
        final MusicViewHolder holder = (MusicViewHolder) vholder;
        final MusicInfo info = mList.get(position);
        Glide.with(mContext)
                .loadFromMediaStore(info.getAlbumIconUri())
                .asBitmap()
                .placeholder(R.drawable.music)
                .into(holder.icon);
        holder.title.setText(info.getTitle());
        holder.time.setText(info.getDurationString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onItemClick(holder);
                }
            }
        });
        holder.itemView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(mListener != null) {
                            mListener.onItemLongClick(holder);
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public MusicInfo getItem(int postion) {
        return mList.get(postion);
    }



    public interface OnItemClickListener {
        void onItemClick(MusicViewHolder holder);
        void onItemLongClick(MusicViewHolder holder);
    }
    public class MusicViewHolder extends WearableListView.ViewHolder {
        CircleImageView icon;
        TextView title, time;
        public MusicViewHolder(View itemView) {
            super(itemView);
            icon = (CircleImageView) itemView.findViewById(R.id.music_icon);
            title = (TextView) itemView.findViewById(R.id.music_title);
            time = (TextView) itemView.findViewById(R.id.music_time);
        }
    }
}
