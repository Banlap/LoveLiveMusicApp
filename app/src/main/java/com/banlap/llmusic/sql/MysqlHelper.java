package com.banlap.llmusic.sql;

import android.util.Log;

import com.banlap.llmusic.model.Message;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.Version;
import com.banlap.llmusic.request.ThreadEvent;

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

    private static final String URL= "";
    private static final String USERNAME= "";
    private static final String PASSWORD= "";

    public static MysqlHelper getInstance() { return new MysqlHelper(); }

    public static Connection connectDB() {
        //EventBus.getDefault().post(new ThreadEvent(ThreadEvent.CONNECT_MYSQL_LOADING));
        Connection cn=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn= DriverManager.getConnection(URL, USERNAME, PASSWORD);
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.CONNECT_MYSQL_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.CONNECT_MYSQL_ERROR));
        }
        return cn;
    }

    public int findMusicCount() {
        int size=0;
        try {
            Connection cn= connectDB();
            String sql = "select count(*) from music where music_name not in ('')";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs!=null) {
                if(rs.next()) {
                    size = rs.getInt(1);
                }
                rs.close();
            }
            cn.close();
            Log.e("MYSQL", "musicListSize: " + size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /** 查询所有数据 */
    public List<Music> findMusicSql() {
        List<Music> musicList = new ArrayList<>();
        try {
            Connection cn= connectDB();
            String sql = "select * from music";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs != null) {
                //Log.e("MYSQL", "rs: " + rs.getMetaData().getColumnCount());
                while (rs.next()) {
                    Music music = new Music();
                    music.setMusicId(rs.getInt("music_id"));
                    music.setMusicType(rs.getString("music_type"));
                    music.setMusicName(rs.getString("music_name"));
                    music.setMusicSinger(rs.getString("music_singer"));
                    music.setMusicImg(rs.getString("music_img"));
                    music.setMusicURL(rs.getString("music_url"));
                    music.setMusicLyric(rs.getString("music_lyric"));
                    musicList.add(music);
                }
                rs.close();
            }
            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("MYSQL", "musicList: " + musicList.size() + " musicList1: " + musicList.get(0).getMusicName());
        return musicList;
    }

    /** 根据musicType查询总数 */
    public int findMusicByMusicTypeCount(String musicType) {
        int size=0;
        try {
            Connection cn= connectDB();
            String sql = "select count(*) from music where music_name not in ('') and music_type = '" + musicType + "'";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs!=null) {
                if(rs.next()) {
                    size = rs.getInt(1);
                }
                rs.close();
            }
            cn.close();
            Log.e("MYSQL", "musicListSize: " + size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /** 根据musicType查询数据 */
    public List<Music> findMusicByMusicTypeSql(String musicType) {
        List<Music> musicList = new ArrayList<>();
        try {
            Connection cn= connectDB();
            String sql = "select * from music where music_type = '" + musicType + "'";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs != null) {
                //Log.e("MYSQL", "rs: " + rs.getMetaData().getColumnCount());
                while (rs.next()) {
                    Music music = new Music();
                    music.setMusicId(rs.getInt("music_id"));
                    music.setMusicType(rs.getString("music_type"));
                    music.setMusicName(rs.getString("music_name"));
                    music.setMusicSinger(rs.getString("music_singer"));
                    music.setMusicImg(rs.getString("music_img"));
                    music.setMusicURL(rs.getString("music_url"));
                    music.setMusicLyric(rs.getString("music_lyric"));
                    musicList.add(music);
                }
                rs.close();
            }
            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.e("MYSQL", "musicList: " + musicList.size() + " musicList1: " + musicList.get(0).getMusicName());
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
                //Log.e("MYSQL", "rs: " + rs.getMetaData().getColumnCount());
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
        Log.e("MYSQL", "messageList: " + messageList.size());
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
                //Log.e("MYSQL", "rs: " + rs.getMetaData().getColumnCount());
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
        Log.e("MYSQL", "versionList: " + versionList.size());
        return versionList;
    }

    public void updateSql() {
        try {
            Connection cn= connectDB();
            String sql = "";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            Log.e("MYSQL", "rs: " + rs.getMetaData().getColumnCount());
            cn.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
