package xyz.imxqd.wearmusic.models.db;

import android.net.Uri;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import xyz.imxqd.wearmusic.R;
import xyz.imxqd.wearmusic.models.MusicInfo;
import xyz.imxqd.wearmusic.utils.App;


/**
 * Created by imxqd on 2016/8/13.
 * 歌单的数据库模型类
 */
public class SongSheet extends SugarRecord{

    String name; // 歌单名
    int musicCount; // 歌单里的歌曲数
    Uri cover; // 歌单封面

    public static void init() {
        if (SongSheet.findById(SongSheet.class, 1L) == null) {
            SongSheet list = new SongSheet();
            list.setId(1L);
            list.name = App.getApp().getString(R.string.name_song_list_favorites);
            list.musicCount = 0;
            list.save();
        }

        if (SongSheet.findById(SongSheet.class, 2L) == null) {
            SongSheet list2 = new SongSheet();
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
        List<SongItem> listItems =
                SongItem.find(SongItem.class, "listId = ?",
                        new String[]{"" + getId()}, null, "position", null);
        ArrayList<MusicInfo> list = new ArrayList<>(listItems.size());
        for(SongItem item : listItems) {
            MusicInfo info =
                    MusicInfo.findById(MusicInfo.class, item.musicId);
            if(info != null) {
                list.add(info);
            }
        }

        return list;
    }

    /**
     * 在该歌单中新增歌曲，歌曲添加在歌单末尾
     * @param item 歌曲信息
     */
    public void add(MusicInfo item) {
        // TODO: 2016/8/16
    }

    /**
     * 交换两个歌曲在歌单中的位置
     * @param pos1 歌曲1的位置
     * @param pos2 歌曲2的位置
     */
    public void exchange(int pos1, int pos2) {
        // TODO: 2016/8/16
    }

    /**
     * 重新设置歌单的歌曲内容
     * @param list 歌曲列表，有顺序
     */
    public void setMusicList(List<MusicInfo> list) {
        // TODO: 2016/8/16
    }

    /**
     * 删除歌单，会级联删除SongListItem，但不会删除MusicInfo
     * @return 是否成功
     */
    @Override
    public boolean delete() {
        // TODO: 2016/8/16 删除SongListItem
        return super.delete();
    }

    public Uri getCover() {
        return cover;
    }

    public void setCover(Uri cover) {
        this.cover = cover;
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
