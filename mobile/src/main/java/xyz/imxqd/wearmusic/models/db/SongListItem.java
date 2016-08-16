package xyz.imxqd.wearmusic.models.db;

import com.orm.SugarRecord;

/**
 * Created by imxqd on 2016/8/13.
 * 歌单中歌曲列表的数据存储模型类
 */
public class SongListItem extends SugarRecord{
    long listId; // 歌单在数据库中的Id
    long musicId; // 歌曲在数据库中的Id
    int position;// 歌曲中歌单中的位置

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public long getMusicId() {
        return musicId;
    }

    public void setMusicId(long musicId) {
        this.musicId = musicId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
