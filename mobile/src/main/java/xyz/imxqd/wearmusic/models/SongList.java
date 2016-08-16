package xyz.imxqd.wearmusic.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.utils.App;


/**
 * Created by imxqd on 2016/8/13.
 * 歌单的数据库模型类
 */
public class SongList extends SugarRecord{

    String name;
    int musicCount;

    public static void init() {
        if (SongList.findById(SongList.class, 1L) == null) {
            SongList list = new SongList();
            list.setId(1L);
            list.name = App.getApp().getString(R.string.name_song_list_favorites);
            list.musicCount = 0;
            list.save();
        }

        if (SongList.findById(SongList.class, 2L) == null) {
            SongList list2 = new SongList();
            list2.setId(2L);
            list2.name = App.getApp().getString(R.string.name_song_list_watch);
            list2.musicCount = 0;
            list2.save();
        }

    }

    /**
     * 获取该歌单中的歌曲列表
     * @return 该歌单中的歌曲列表
     */
    public List<MusicInfo> getMusicList() {
        List<SongListItem> listItems =
                SongListItem.find(SongListItem.class, "listId = ?",
                        new String[]{"" + getId()}, null, "position", null);
        ArrayList<MusicInfo> list = new ArrayList<>(listItems.size());
        for(SongListItem item : listItems) {
            MusicInfo info =
                    MusicInfo.findById(MusicInfo.class, item.musicId);
            if(info != null) {
                list.add(info);
            }
        }

        return list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(int musicCount) {
        this.musicCount = musicCount;
    }
}
