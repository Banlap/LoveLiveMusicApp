package com.banlap.llmusic.fixed;

import com.banlap.llmusic.R;
import com.banlap.llmusic.model.TeamMusic;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.sql.room.RoomPlayMusic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Banlap on 2021/11/30
 */
public class AppMusic {
    public static AppMusic getInstance() { return new AppMusic(); }
    public List<RoomPlayMusic> list;
    public List<TeamMusic> teamMusicList;

    public List<RoomPlayMusic> getNullMusicData() {
        list = new ArrayList<>();

        RoomPlayMusic nullMusic1 = new RoomPlayMusic();
        nullMusic1.musicId = 9999991;
        nullMusic1.musicType = " ";
        nullMusic1.musicName = " ";

        RoomPlayMusic nullMusic2 = new RoomPlayMusic();
        nullMusic2.musicId = 9999992;
        nullMusic2.musicType = " ";
        nullMusic2.musicName = " ";

        list.add(nullMusic1);
        list.add(nullMusic2);
        return list;
    }

    public List<TeamMusic> getTeamMusicList() {
        teamMusicList = new ArrayList<>();

        TeamMusic teamMusic11 = new TeamMusic();
        teamMusic11.setAlbum(ThreadEvent.ALBUM_BLUEBIRD);
        teamMusic11.setTitleName("イキヅライブ!");
        teamMusic11.setRemark1("イキヅライブ!");
        teamMusic11.setRemark2("LoveLive! BLUEBIRD");
        teamMusic11.setResId(R.mipmap.ic_album_bluebird_2);
        teamMusic11.setResId2(R.mipmap.ic_album_bluebird);
        teamMusicList.add(teamMusic11);

        TeamMusic teamMusic10 = new TeamMusic();
        teamMusic10.setAlbum(ThreadEvent.ALBUM_HASUNOSORA);
        teamMusic10.setTitleName("蓮ノ空女学院スクールアイドルクラブ");
        teamMusic10.setRemark1("蓮ノ空女学院スクールアイドルクラブ");
        teamMusic10.setRemark2("Hasunosora Jogakuin School Idol Club");
        teamMusic10.setResId(R.mipmap.ic_album_hasu_2);
        teamMusic10.setResId2(R.mipmap.ic_album_hasu);
        teamMusicList.add(teamMusic10);

        TeamMusic teamMusic9 = new TeamMusic();
        teamMusic9.setAlbum(ThreadEvent.ALBUM_LIELLA);
        teamMusic9.setTitleName("Liella!");
        teamMusic9.setRemark1("Liella!");
        teamMusic9.setRemark2("LoveLive!Superstar!!");
        teamMusic9.setResId(R.mipmap.ic_album_liella_3);
        teamMusic9.setResId2(R.mipmap.ic_album_liella);
        teamMusicList.add(teamMusic9);

        TeamMusic teamMusic8 = new TeamMusic();
        teamMusic8.setAlbum(ThreadEvent.ALBUM_NIJIGASAKI);
        teamMusic8.setTitleName("虹ヶ咲学園スクールアイドル同好会");
        teamMusic8.setRemark1("虹ヶ咲学園スクールアイドル同好会");
        teamMusic8.setRemark2("Nijigasaki HighSchool IdolClub");
        teamMusic8.setResId(R.mipmap.ic_album_nijigasaki_3);
        teamMusic8.setResId2(R.mipmap.ic_album_nijigasaki);
        teamMusicList.add(teamMusic8);

        TeamMusic teamMusic7 = new TeamMusic();
        teamMusic7.setAlbum(ThreadEvent.ALBUM_AQOURS);
        teamMusic7.setTitleName("Aqours");
        teamMusic7.setRemark1("Aqours");
        teamMusic7.setRemark2("LoveLive!Sunshine!!");
        teamMusic7.setResId(R.mipmap.ic_album_aqours_3);
        teamMusic7.setResId2(R.mipmap.ic_album_aqours_4);
        teamMusicList.add(teamMusic7);

        TeamMusic teamMusic6 = new TeamMusic();
        teamMusic6.setAlbum(ThreadEvent.ALBUM_US);
        teamMusic6.setTitleName("μ's");
        teamMusic6.setRemark1("μ's");
        teamMusic6.setRemark2("LoveLive!");
        teamMusic6.setResId(R.mipmap.ic_album_us_3);
        teamMusic6.setResId2(R.mipmap.ic_album_us_2);
        teamMusicList.add(teamMusic6);

        TeamMusic teamMusic5 = new TeamMusic();
        teamMusic5.setAlbum(ThreadEvent.ALBUM_SUNNY_PASSION);
        teamMusic5.setTitleName("サニーパッション");
        teamMusic5.setRemark1("サニーパッション");
        teamMusic5.setRemark2("SunnyPassion");
        teamMusic5.setResId(R.mipmap.ic_album_sunny_passion);
        teamMusic5.setResId2(R.mipmap.ic_album_sunny_passion_3);
        teamMusicList.add(teamMusic5);

        TeamMusic teamMusic4 = new TeamMusic();
        teamMusic4.setAlbum(ThreadEvent.ALBUM_SAINT_SNOW);
        teamMusic4.setTitleName("セイントスノー");
        teamMusic4.setRemark1("セイントスノー");
        teamMusic4.setRemark2("Saint Snow");
        teamMusic4.setResId(R.mipmap.ic_album_saint_snow_2);
        teamMusic4.setResId2(R.mipmap.ic_album_saint_snow);
        teamMusicList.add(teamMusic4);

        TeamMusic teamMusic3 = new TeamMusic();
        teamMusic3.setAlbum(ThreadEvent.ALBUM_A_RISE);
        teamMusic3.setTitleName("アライズ");
        teamMusic3.setRemark1("アライズ");
        teamMusic3.setRemark2("A-RISE");
        teamMusic3.setResId(R.mipmap.ic_album_a_rise_2);
        teamMusic3.setResId2(R.mipmap.ic_album_a_rise);
        teamMusicList.add(teamMusic3);

        TeamMusic teamMusic2 = new TeamMusic();
        teamMusic2.setAlbum(ThreadEvent.ALBUM_FOUR_YUU);
        teamMusic2.setTitleName("Liyuu");
        teamMusic2.setRemark1("Liyuu");
        teamMusic2.setRemark2("Liyuu");
        teamMusic2.setResId(R.mipmap.ic_album_liyuu);
        teamMusic2.setResId2(R.mipmap.ic_album_liyuu_2);
        teamMusicList.add(teamMusic2);

        TeamMusic teamMusic1 = new TeamMusic();
        teamMusic1.setAlbum(ThreadEvent.ALBUM_OTHER);
        teamMusic1.setTitleName("Other");
        teamMusic1.setRemark1("Other");
        teamMusic1.setRemark2("其他");
        teamMusic1.setResId(R.mipmap.ic_album_lovelive_2);
        teamMusic1.setResId2(R.mipmap.ic_album_lovelive);
        teamMusicList.add(teamMusic1);

        return teamMusicList;
    }
}
