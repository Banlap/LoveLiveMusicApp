package com.banlap.llmusic.request;

import android.graphics.Bitmap;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;

import com.banlap.llmusic.model.Message;
import com.banlap.llmusic.model.Music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Banlap on 2021/11/30
 */
public class ThreadEvent<T> {
    public static String ALBUM_LIELLA = "ALBUM_LIELLA";
    public static String ALBUM_SUNNY_PASSION = "ALBUM_SUNNY_PASSION";
    public static String ALBUM_FOUR_YUU = "ALBUM_FOUR_YUU";
    public static String ALBUM_NIJIGASAKI = "ALBUM_NIJIGASAKI";
    public static String ALBUM_AQOURS = "ALBUM_AQOURS";
    public static String ALBUM_US = "ALBUM_US";
    public static String ALBUM_HASUNOSORA = "ALBUM_HASUNOSORA";
    public static String ALBUM_SAINT_SNOW = "ALBUM_SAINT_SNOW";
    public static String ALBUM_A_RISE = "ALBUM_A_RISE";
    public static final int CONNECT_MYSQL = 0x10;    //连接数据库
    public static final int CONNECT_MYSQL_LOADING = 0x101;  //连接数据库 加载中
    public static final int CONNECT_MYSQL_SUCCESS = 0x11;  //连接数据库 成功
    public static final int CONNECT_MYSQL_ERROR = 0x12;   //连接数据库 失败
    public static final int GET_SUCCESS = 0x13;            //获取专辑列表成功
    public static final int GET_ALBUM_SUCCESS = 0x131;     //获取专辑数据成功
    public static final int GET_LOCAL_PLAY_LIST_SUCCESS = 0x132;  //获取本地列表成功
    public static final int GET_ERROR = 0x14;
    public static final int GET_COUNT_SUCCESS = 0x15;
    public static final int GET_RECOMMEND_SUCCESS = 0x151;    //获取每日推荐数据成功
    public static final int GET_MESSAGE_SUCCESS = 0x16;
    public static final int GET_MESSAGE_ERROR = 0x17;
    public static final int GET_APP_VERSION_SUCCESS = 0x18;

    public static final int GET_DATA_LIST = 0x20;           //获取所有歌曲
    public static final int GET_DATA_LIST_BY_LIELLA = 0x201;    //获取Liella所有歌曲
    public static final int GET_DATA_LIST_BY_SUNNY_PASSION = 0x202; //获取SUNNY PASSION所有歌曲
    public static final int GET_DATA_LIST_BY_FOUR_YUU = 0x203;    //获取Liyuu所有歌曲
    public static final int GET_DATA_LIST_BY_NIJIGASAKI = 0x204;  //获取虹团所有歌曲
    public static final int GET_DATA_LIST_BY_AQOURS = 0x205;  //获取Aqours所有歌曲
    public static final int GET_DATA_LIST_BY_US = 0x206;  //
    public static final int GET_DATA_LIST_BY_HASUNOSORA = 0x207; //
    public static final int GET_DATA_LIST_BY_SAINT_SNOW = 0x208;
    public static final int GET_DATA_LIST_BY_A_RISE = 0x209;
    public static final int GET_DATA_LIST_BY_LOCAL_PLAY = 0x255;

    public static final int GET_DATA_LIST_COUNT = 0x21;
    public static final int GET_DATA_RECOMMEND = 0x211;
    public static final int GET_DATA_LIST_MESSAGE = 0x22;
    public static final int GET_DATA_APP_VERSION = 0x23;

    public static final int DOWNLOAD_APP = 0x24;
    public static final int SCAN_LOCAL_FILE = 0x2401;
    public static final int SCAN_LOCAL_FILE_SUCCESS = 0x2402;
    public static final int SCAN_LOCAL_FILE_ERROR = 0x2403;
    public static final int SCAN_LOCAL_FILE_BY_CHECK_PERMISSION = 0x2404;
    public static final int SELECT_LOCAL_FILE_SUCCESS = 0x2405;
    public static final int SELECT_LOCAL_FILE_ERROR = 0x2406;
    public static final int SELECT_IMG_FILE_SUCCESS = 0x2407;

    public static final int SELECT_VIDEO_FILE = 0x2408;

    public static final int DOWNLOAD_APP_START = 0x241;
    public static final int DOWNLOAD_APP_LOADING = 0x242;
    public static final int DOWNLOAD_APP_SUCCESS = 0x243;
    public static final int DOWNLOAD_APP_ERROR = 0x244;
    public static final int DOWNLOAD_APP2 = 0x245;
    public static final int DOWNLOAD_APP_START2 = 0x246;
    public static final int DOWNLOAD_APP_LOADING2 = 0x247;
    public static final int DOWNLOAD_APP_SUCCESS2 = 0x248;
    public static final int DOWNLOAD_APP_ERROR2 = 0x249;
    public static final int VIEW_DOWNLOAD_MUSIC = 0x25;
    public static final int VIEW_DOWNLOAD_MUSIC_SHOW = 0x251;
    public static final int VIEW_DOWNLOAD_MUSIC_UPDATE= 0x252;
    public static final int VIEW_DOWNLOAD_MUSIC_FINISH = 0x253;
    public static final int VIEW_DOWNLOAD_MUSIC_CANCEL = 0x254;

    public static final int GET_CURRENT_TIME = 0x30;
    public static final int SHOW_FRAGMENT = 0x301;
    public static final int PLAY_FINISH_SUCCESS = 0x31;
    public static final int PLAY_ERROR = 0x311;
    public static final int GET_MUSIC_METADATA = 0x312;
    public static final int VIEW_GET_MUSIC_METADATA = 0x313;

    public static final int PLAY_LIST_FIRST = 0x32;
    public static final int PLAY_MUSIC_BY_CHARACTER = 0x33;  //角色控制播放
    public static final int PLAY_LOCAL_MUSIC = 0x331;
    public static final int ADD_LOCAL_MUSIC = 0x332;
    public static final int PLAY_FAVORITE_MUSIC = 0x333;
    public static final int ADD_FAVORITE_MUSIC = 0x334;
    public static final int ADD_MUSIC_TO_LOCAL_PLAY_LIST = 0x335;
    public static final int DELETE_MUSIC_IN_LOCAL_PLAY_LIST = 0x336;
    public static final int PLAY_RECOMMEND_MUSIC = 0x337;
    public static final int SAVE_LOCAL_MUSIC_LIST = 0x338;
    public static final int GET_MUSIC_LYRIC = 0x34;   //获取音乐歌词

    public static final int SHOW_IMAGE_URL = 0x36;    //展示音乐图片
    public static final int MUSIC_IS_NEXT = 0x37;
    public static final int MUSIC_IS_LAST = 0x38;
    public static final int MUSIC_IS_PAUSE = 0x39;

    public static final int VIEW_SHOW_CHARACTER_TALK = 0x41;
    public static final int VIEW_SHOW_CHARACTER_TALK_CONTENT = 0x411;
    public static final int VIEW_HIDE_CHARACTER_TALK = 0x42;
    public static final int VIEW_NORMAL_STATUS_CHARACTER = 0x43;
    public static final int VIEW_MOVE_STATUS_CHARACTER = 0x431;
    public static final int VIEW_LISTEN_STATUS_CHARACTER_LEFT = 0x432;
    public static final int VIEW_LISTEN_STATUS_CHARACTER_RIGHT = 0x433;

    public static final int VIEW_SEEK_BAR_POS = 0x50;
    public static final int UPDATE_NOTIFICATION_SEEK_BAR_POS = 0x501;

    public static final int RUNNABLE_DELAY = 0x511;
    public static final int VIEW_SEEK_BAR_RESUME = 0x51;
    public static final int VIEW_PAUSE = 0x52;   //处理播放或暂停
    public static final int VIEW_SHOW_VISUALIZER =0x521;
    public static final int VIEW_SHOW_STOP_VISUALIZER = 0x522;
    public static final int VIEW_MUSIC_MSG = 0x53;  //展示音乐信息
    public static final int VIEW_ADD_MUSIC = 0x54;
    public static final int VIEW_ADD_FAVORITE_MUSIC = 0x541;
    public static final int VIEW_ADD_MUSIC_TO_LOCAL_PLAY_LIST = 0x542;
    public static final int VIEW_ADD_MUSIC_TO_LOCAL_PLAY_LIST_SUCCESS = 0x543;
    public static final int VIEW_CANCEL_FAVORITE_MUSIC = 0x544;
    public static final int VIEW_FRESH_FAVORITE_MUSIC = 0x545;
    public static final int VIEW_DELETE_MUSIC = 0x55;
    public static final int VIEW_DELETE_LOCAL_MUSIC = 0x551;
    public static final int VIEW_DELETE_FAVORITE_MUSIC = 0x552;
    public static final int VIEW_DELETE_MUSIC_TO_LOCAL_PLAY_LIST = 0x553;
    public static final int VIEW_DELETE_MUSIC_TO_LOCAL_PLAY_LIST_SUCCESS = 0x554;
    public static final int VIEW_LYRIC = 0x56;
    public static final int VIEW_IMAGE_URL = 0x57;
    public static final int VIEW_SHOW_OR_HIDE_MASKING_BACKGROUND = 0x58;
    public static final int VIEW_CHANGE_THEME = 0x59;
    public static final int VIEW_SETTING_LAUNCH_VIDEO_SUCCESS = 0x60;
    public static final int VIEW_SETTING_LAUNCH_VIDEO_ERROR = 0x601;
    public static final int VIEW_CONTROLLER_MODE = 0x61;
    public static final int VIEW_NEW_CONTROLLER_MODE = 0x611;
    public static final int VIEW_BG_MODE = 0x612;
    public static final int VIEW_INTO_SET_BG = 0x613;

    public static final int VIEW_CLICK_LOCAL_OR_FAVORITE = 0x62;
    public static final int VIEW_HIDE_LOCAL_OR_FAVORITE = 0x621;
    public static final int VIEW_COUNT_DOWN_REFRESH = 0x631;
    public static final int VIEW_COUNT_DOWN_FINISH = 0x632;

    public static final int VIEW_SCREEN_LOCK= 0x641;
    public static final int VIEW_SCREEN_UNLOCK= 0x642;


    /** 蓝牙部分控制 */
    public static final int BLUETOOTH_DISCONNECT = 0x70;
    public static final int ACTION_MEDIA_BUTTON = 0x71; //已使用onKeyDown方法实现


    /**
     * Pad版本
     * */
    public static final int PAD_CONNECT_MYSQL= 0x9101;
    public static final int PAD_GET_DATA_LIST = 0x9102;
    public static final int VIEW_PAD_GET_SUCCESS = 0x9103;
    public static final int VIEW_PAD_CHANGE_FRAGMENT= 0x9110;
    public static final int VIEW_PAD_CHANGE_LAST_FRAGMENT= 0x9111;

    public static final int PAD_GET_DATA_LIST_BY_LIELLA = 0x9201;    //获取Liella所有歌曲
    public static final int PAD_GET_DATA_LIST_BY_SUNNY_PASSION = 0x9202; //获取SUNNY PASSION所有歌曲
    public static final int PAD_GET_DATA_LIST_BY_FOUR_YUU = 0x9203;    //获取Liyuu所有歌曲
    public static final int PAD_GET_DATA_LIST_BY_NIJIGASAKI = 0x9204;  //获取虹团所有歌曲
    public static final int PAD_GET_DATA_LIST_BY_AQOURS = 0x9205;  //获取Aqours所有歌曲
    public static final int PAD_GET_DATA_LIST_BY_US = 0x9206;  //
    public static final int PAD_GET_DATA_LIST_BY_HASUNOSORA = 0x9207; //
    public static final int PAD_GET_DATA_LIST_BY_SAINT_SNOW = 0x9208;
    public static final int PAD_GET_DATA_LIST_BY_A_RISE = 0x9209;
    public static final int PAD_GET_DATA_LIST_BY_LOCAL_PLAY = 0x9255;
    public static final int PAD_VIEW_PLAY_MUSIC = 0x9301;
    public static final int PAD_VIEW_ADD_MUSIC = 0x9302;
    public static final int PAD_PLAY_ALL_MUSIC = 0x9303;
    public static final int PAD_VIEW_GET_MUSIC_METADATA = 0x9313;


    public int msgCode;
    public List<Music> musicList;
    public List<Message> messageList;

    public List<T> tList;
    public T t;

    public String str;
    public String str2;
    public String str3;
    public int[] intArray;
    public String[] strArray;
    public byte[] byteArray;
    public int i;
    public int i2;
    public boolean b;
    public Music music;
    public Bitmap bitmap;
    public SpannableStringBuilder ssb;
    public File file;
    public Double d;
    public KeyEvent kt;

    public ThreadEvent(int msgCode) { this.msgCode = msgCode; }
    public ThreadEvent(int msgCode, List<Music> musicList) { this.msgCode = msgCode; this.musicList = musicList; }
    public ThreadEvent(int msgCode, List<T> tList, int i) { this.msgCode = msgCode; this.tList = tList; this.i = i; }
    public ThreadEvent(int msgCode, List<T> tList, String str) { this.msgCode = msgCode; this.tList = tList; this.str = str;}
    public ThreadEvent(int msgCode, String str) { this.msgCode = msgCode; this.str = str; }
    public ThreadEvent(int msgCode, String str, String str2) { this.msgCode = msgCode; this.str = str; this.str2 = str2; }
    public ThreadEvent(int msgCode, String str, String str2, String str3) { this.msgCode = msgCode; this.str = str; this.str2 = str2; this.str3 = str3;}
    public ThreadEvent(int msgCode, String str, String str2, String str3,  Bitmap bitmap, boolean b) { this.msgCode = msgCode; this.str = str; this.str2 = str2; this.str3 = str3; this.bitmap = bitmap; this.b = b; }
    public ThreadEvent(int msgCode, String str, String str2, String str3, boolean b) { this.msgCode = msgCode; this.str = str; this.str2 = str2; this.str3 = str3; this.b = b; }
    public ThreadEvent(int msgCode, String str, int i) { this.msgCode = msgCode; this.str = str; this.i = i;}
    public ThreadEvent(int msgCode, String str, int i, int i2, byte[] byteArray) { this.msgCode = msgCode; this.str = str; this.i = i; this.i2 = i2; this.byteArray = byteArray; }
    public ThreadEvent(int msgCode, Double d) { this.msgCode = msgCode; this.d = d; }
    public ThreadEvent(int msgCode, int i) { this.msgCode = msgCode; this.i = i; }
    public ThreadEvent(int msgCode, int i, int i2) { this.msgCode = msgCode; this.i = i; this.i2 = i2;}
    public ThreadEvent(int msgCode, int i, int i2, String str) { this.msgCode = msgCode; this.i = i; this.i2 = i2; this.str = str;}
    public ThreadEvent(int msgCode, boolean b) { this.msgCode = msgCode; this.b = b; }
    public ThreadEvent(int msgCode, boolean b, File file) { this.msgCode = msgCode; this.b = b; this.file = file;}
    public ThreadEvent(int msgCode, T t) { this.msgCode = msgCode; this.t = t; }
    public ThreadEvent(int msgCode, Music music) { this.msgCode = msgCode; this.music = music; }
    public ThreadEvent(int msgCode, Music music, int i) { this.msgCode = msgCode; this.music = music; this.i = i; }
    public ThreadEvent(int msgCode, Music music, boolean b) { this.msgCode = msgCode; this.music = music; this.b = b; }
    public ThreadEvent(int msgCode, Music music, boolean b, String str, String str2) { this.msgCode = msgCode; this.music = music; this.b = b; this.str = str; this.str2 = str2; }
    public ThreadEvent(int msgCode, Music music, boolean b, String str, String str2, List<T> tList) { this.msgCode = msgCode; this.music = music; this.b = b; this.str = str; this.str2 = str2; this.tList = tList; }
    public ThreadEvent(int msgCode, String str, String str2, Bitmap bitmap) { this.msgCode = msgCode; this.str = str; this.str2 = str2; this.bitmap = bitmap; }
    public ThreadEvent(int msgCode, SpannableStringBuilder ssb) { this.msgCode = msgCode; this.ssb = ssb; }
    public ThreadEvent(int msgCode, String[] strArray) { this.msgCode = msgCode; this.strArray = strArray; }
    public ThreadEvent(int msgCode, int[] intArray, String[] strArray) { this.msgCode = msgCode; this.intArray = intArray; this.strArray = strArray; }
    public ThreadEvent(int msgCode, KeyEvent kt) { this.msgCode = msgCode; this.kt = kt; };
}
