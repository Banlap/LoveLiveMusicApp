package com.banlap.llmusic.sql;

import android.util.Log;

import com.banlap.llmusic.BuildConfig;
import com.banlap.llmusic.model.Message;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.Version;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.sql.room.RoomPlayMusic;

import org.greenrobot.eventbus.EventBus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Banlap on 2021/11/3
 */
public class MysqlHelper {
    private static final String TAG = MysqlHelper.class.getSimpleName();
    private static final String URL= BuildConfig.MYSQL_URL;
    private static final String USERNAME= BuildConfig.MYSQL_ACCOUNT;
    private static final String PASSWORD= BuildConfig.MYSQL_PASSWORD;

    //专辑类型
    public static final String MUSIC_TYPE_LIELLA = "Liella";
    public static final String MUSIC_TYPE_LIYUU = "Fo(u)rYuU";
    public static final String MUSIC_TYPE_SUNNYPASSION = "SunnyPassion";
    public static final String MUSIC_TYPE_NIJIGASAKI = "Nijigasaki";
    public static final String MUSIC_TYPE_AQOURS = "Aqours";
    public static final String MUSIC_TYPE_US = "us";
    public static final String MUSIC_TYPE_HASUNOSORA = "Hasunosora";
    public static final String MUSIC_TYPE_BLUEBIRD = "Bluebird";
    public static final String MUSIC_TYPE_SAINT_SNOW = "Saint Snow";
    public static final String MUSIC_TYPE_A_RISE = "A-RISE";
    public static final String MUSIC_TYPE_OTHER = "Other";


    public static MysqlHelper getInstance() { return new MysqlHelper(); }

    public static Connection connectDB() {
        //EventBus.getDefault().post(new ThreadEvent(ThreadEvent.CONNECT_MYSQL_LOADING));
        Connection cn=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn= DriverManager.getConnection(URL, USERNAME, PASSWORD);
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_CONNECT_MYSQL_SUCCESS));
        } catch (Exception e) {
            Log.e(TAG, "connectDB error: " + e.getMessage());
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_CONNECT_MYSQL_ERROR));
        }
        return cn;
    }

    /** 查询所有数据总数 */
    public int findMusicCount() {
        return findMusicCountBySql("select count(*) from music where music_name not in ('')");
    }

    /** 根据musicType查询总数 */
    public int findMusicByMusicTypeCount(String musicType) {
        return findMusicCountBySql("select count(*) from music where music_name not in ('') and music_type = '" + musicType + "'");
    }

    /** 根据musicType和musicSinger查询总数 */
    public int findMusicByMusicTypeAndMusicSingerCount(String musicType, String musicSinger) {
        return findMusicCountBySql("select count(*) from music where music_name not in ('') and music_type = '" + musicType + "' and music_singer = '" + musicSinger + "'");
    }

    /** 查询所有数据 */
    public List<RoomPlayMusic> findMusicAll() {
        return findMusicBySql("select * from music");
    }

    /** 根据musicType查询数据 */
    public List<RoomPlayMusic> findMusicByMusicTypeSql(String musicType) {
        return findMusicBySql("select * from music where music_type = '" + musicType + "'");
    }

    /** 根据musicType和musicSinger查询数据 */
    public List<RoomPlayMusic> findMusicByMusicTypeAndMusicSingerSql(String musicType, String musicSinger) {
        return findMusicBySql("select * from music where music_type = '" + musicType + "' and music_singer = '" + musicSinger + "'");
    }

    /** 根据MusicId查询数据 */
    public List<RoomPlayMusic> findMusicByMusicIdSql(String... musicId) {
        List<RoomPlayMusic> musicList = new ArrayList<>();

        if(musicId.length >0) {
            StringBuilder musicIdListStr = new StringBuilder();
            musicIdListStr.append("(");
            for (int i = 0; i < musicId.length; i++) {
                if (i == musicId.length - 1) {
                    musicIdListStr.append("'").append(musicId[i]).append("'");
                } else {
                    musicIdListStr.append("'").append(musicId[i]).append("',");
                }
            }
            musicIdListStr.append(")");
            String sql = "select * from music where music_id in " + musicIdListStr;
            return findMusicBySql(sql);
        }
        return musicList;
    }

    /**
     * 随机查询数据
     * @param count 指定查询多少条
     * */
    public List<RoomPlayMusic> findMusicByRandomSql(int count) {
        return findMusicBySql("select * from music where music_name != '' order by rand() limit " + count);
    }

    /** 通过sql查询music表数据总数 */
    public int findMusicCountBySql(String sql) {
        int size=0;
        try {
            Connection cn= connectDB();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs!=null) {
                if(rs.next()) {
                    size = rs.getInt(1);
                }
                rs.close();
            }
            cn.close();
            Log.i("MYSQL", "musicListSize: " + size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /** 通过sql查询music表数据 */
    public List<RoomPlayMusic> findMusicBySql(String sql) {
        List<RoomPlayMusic> musicList = new ArrayList<>();
        try {
            Connection cn= connectDB();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs != null) {
                //Log.i("MYSQL", "rs: " + rs.getMetaData().getColumnCount());
                while (rs.next()) {
                    RoomPlayMusic music = new RoomPlayMusic();
                    music.musicId = rs.getInt("music_id");
                    music.musicType = rs.getString("music_type");
                    music.musicName = rs.getString("music_name");
                    music.musicSinger = rs.getString("music_singer");
                    music.musicImg = rs.getString("music_img");
                    music.musicURL = rs.getString("music_url");
                    music.musicLyric = rs.getString("music_lyric");
                    musicList.add(music);
                }
                rs.close();
            }
            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.i("MYSQL", "musicList: " + musicList.size() + " musicList1: " + musicList.get(0).getMusicName());
        return musicList;
    }

    /** 查询所有消息数据 */
    public List<Message> findMessageSql() {
        List<Message> messageList = new ArrayList<>();
        try {
            Connection cn= connectDB();
            String sql = "select * from message";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs != null) {
                //Log.i("MYSQL", "rs: " + rs.getMetaData().getColumnCount());
                while (rs.next()) {
                    Message message = new Message();
                    message.setMessageId(rs.getInt("message_id"));
                    message.setMessageType(rs.getString("message_type"));
                    message.setMessageImg(rs.getString("message_img"));
                    message.setMessageTitle(rs.getString("message_title"));
                    message.setMessageContent(rs.getString("message_content"));
                    message.setMessageDate(rs.getDate("message_date"));
                    messageList.add(message);
                }
                rs.close();
            }
            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("MYSQL", "messageList: " + messageList.size());
        return messageList;
    }

    /** 查询当前是否需要更新App */
    public List<Version> findVersionSql() {
        List<Version> versionList = new ArrayList<>();
        try {
            Connection cn= connectDB();
            String sql = "select * from version";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs != null) {
                //Log.i("MYSQL", "rs: " + rs.getMetaData().getColumnCount());
                while (rs.next()) {
                    Version version = new Version();
                    version.setVersionId(rs.getInt("version_id"));
                    version.setVersionName(rs.getString("version_name"));
                    version.setVersionCode(rs.getString("version_code"));
                    version.setVersionType(rs.getString("version_type"));
                    version.setVersionTitle(rs.getString("version_title"));
                    version.setVersionContent(rs.getString("version_content"));
                    version.setVersionUrl(rs.getString("version_url"));
                    version.setVersionDate(rs.getDate("version_date"));
                    versionList.add(version);
                }
                rs.close();
            }
            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("MYSQL", "versionList: " + versionList.size());
        return versionList;
    }

    public void updateSql() {
        try {
            Connection cn= connectDB();
            String sql = "";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            Log.i("MYSQL", "rs: " + rs.getMetaData().getColumnCount());
            cn.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
