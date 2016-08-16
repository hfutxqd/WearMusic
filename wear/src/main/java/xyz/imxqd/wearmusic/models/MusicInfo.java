package xyz.imxqd.wearmusic.models;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import xyz.imxqd.wearmusic.utils.MusicImageUtils;

/**
 * Created by imxqd on 2016/8/7.
 * 音乐信息的模型类
 */
public class MusicInfo extends SugarRecord implements Parcelable {
    String title, artist, durationString, albumName;
    Uri songUrl;
    int duration;
    long songId, albumId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Uri getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(Uri songUrl) {
        this.songUrl = songUrl;
    }

    public Bitmap getAlbumIcon() {
        return MusicImageUtils.getByAlbumId(albumId);
    }

    public Uri getAlbumIconUri() {
        return ContentUris.withAppendedId(MusicImageUtils.ALBUMART_URI, albumId);
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.durationString);
        dest.writeString(this.albumName);
        dest.writeInt(this.duration);
        dest.writeLong(this.songId);
        dest.writeLong(this.albumId);
        dest.writeParcelable(this.songUrl, flags);
    }

    public MusicInfo() {
    }

    protected MusicInfo(Parcel in) {
        this.title = in.readString();
        this.artist = in.readString();
        this.durationString = in.readString();
        this.albumName = in.readString();
        this.duration = in.readInt();
        this.songId = in.readLong();
        this.albumId = in.readLong();
        this.songUrl = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel source) {
            return new MusicInfo(source);
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };
}
