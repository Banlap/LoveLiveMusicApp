package com.banlap.llmusic.request;

import android.graphics.Bitmap;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;

import com.banlap.llmusic.model.Message;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.sql.room.RoomCustomPlay;
import com.banlap.llmusic.sql.room.RoomPlayMusic;

import java.io.File;
import java.util.List;

/**
 * @author Banlap on 2021/11/30
 * 后续计划去除EventBus 使用liveData + rxjava/Executor处理ui及耗时操作
 */
public class ThreadEvent<T> {
    public static String ALBUM_LIELLA = "ALBUM_LIELLA";
    public static String ALBUM_SUNNY_PASSION = "ALBUM_SUNNY_PASSION";
    public static String ALBUM_FOUR_YUU = "ALBUM_FOUR_YUU";
    public static String ALBUM_NIJIGASAKI = "ALBUM_NIJIGASAKI";
    public static String ALBUM_AQOURS = "ALBUM_AQOURS";
    public static String ALBUM_US = "ALBUM_US";
    public static String ALBUM_HASUNOSORA = "ALBUM_HASUNOSORA";
    public static String ALBUM_BLUEBIRD = "ALBUM_BLUEBIRD";
    public static String ALBUM_SAINT_SNOW = "ALBUM_SAINT_SNOW";
    public static String ALBUM_A_RISE = "ALBUM_A_RISE";
    public static String ALBUM_OTHER = "ALBUM_OTHER";

    public static final int THREAD_CONNECT_MYSQL = 1000;    //连接数据库
    public static final int VIEW_CONNECT_MYSQL_LOADING = 1001;  //连接数据库 加载中
    public static final int VIEW_CONNECT_MYSQL_SUCCESS = 1002;  //连接数据库 成功
    public static final int VIEW_CONNECT_MYSQL_ERROR = 1003;    //连接数据库 失败
    public static final int VIEW_GET_ALBUM_LIST_SUCCESS = 1010; //获取专辑列表成功
    public static final int VIEW_GET_ALBUM_DATA_SUCCESS = 1011; //获取专辑数据成功
    public static final int VIEW_GET_LOCAL_PLAY_LIST_SUCCESS = 1012; //获取本地列表成功
    public static final int VIEW_GET_ERROR = 1013;
    public static final int VIEW_GET_ALBUM_COUNT_SUCCESS = 1014; //获取某一个专辑音乐数量
    public static final int VIEW_GET_RECOMMEND_SUCCESS = 1015;    //获取每日推荐数据成功
    public static final int VIEW_GET_MESSAGE_SUCCESS = 1016;
    public static final int VIEW_GET_MESSAGE_ERROR = 1017;
    public static final int THREAD_GET_APP_VERSION_SUCCESS = 1018; //获取App版本更新

    public static final int THREAD_GET_TOTAL_LIELLA = 1020;
    public static final int VIEW_GET_TOTAL_LIELLA_SUCCESS = 1021;
    public static final int THREAD_GET_TOTAL_LIYUU = 1022;
    public static final int VIEW_GET_TOTAL_LIYUU_SUCCESS = 1023;
    public static final int THREAD_GET_TOTAL_SUNNY_PASSION = 1024;
    public static final int VIEW_GET_TOTAL_SUNNY_PASSION_SUCCESS = 1025;
    public static final int THREAD_GET_TOTAL_NIJIGASAKI = 1026;
    public static final int VIEW_GET_TOTAL_NIJIGASAKI_SUCCESS = 1027;
    public static final int THREAD_GET_TOTAL_AQOURS = 1028;
    public static final int VIEW_GET_TOTAL_AQOURS_SUCCESS = 1029;
    public static final int THREAD_GET_TOTAL_US = 1030;
    public static final int VIEW_GET_TOTAL_US_SUCCESS = 1031;
    public static final int THREAD_GET_TOTAL_HASUNOSORA = 1032;
    public static final int VIEW_GET_TOTAL_HASUNOSORA_SUCCESS = 1033;
    public static final int THREAD_GET_TOTAL_SAINT_SNOW = 1034;
    public static final int VIEW_GET_TOTAL_SAINT_SNOW_SUCCESS = 1035;
    public static final int THREAD_GET_TOTAL_A_RISE = 1036;
    public static final int VIEW_GET_TOTAL_A_RISE_SUCCESS = 1037;
    public static final int THREAD_GET_TOTAL_OTHER = 1038;
    public static final int VIEW_GET_TOTAL_OTHER_SUCCESS = 1039;

    public static final int THREAD_GET_DATA_LIST = 1100;           //获取所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_LIELLA = 1101; //获取Liella所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_SUNNY_PASSION = 1102; //获取SUNNY PASSION所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_FOUR_YUU = 1103; //获取Liyuu所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_NIJIGASAKI = 1104;  //获取虹团所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_AQOURS = 1105;  //获取Aqours所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_US = 1106;  //获取μ's所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_HASUNOSORA = 1107; //获取莲团所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_SAINT_SNOW = 1108; //获取saint snow所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_A_RISE = 1109; //获取arise所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_OTHER = 1110; //获取其他歌曲
    public static final int THREAD_GET_DATA_LIST_BY_BLUEBIRD = 1111; //获取bluebird所有歌曲
    public static final int THREAD_GET_DATA_LIST_BY_LOCAL_PLAY = 1112; //获取本地歌曲

    public static final int GET_DATA_LIST_COUNT = 1150;
    public static final int THREAD_GET_DATA_RECOMMEND = 1151; //获取每日推荐信息
    public static final int GET_DATA_LIST_MESSAGE = 1152;
    public static final int THREAD_GET_DATA_APP_VERSION = 1153; //获取当前app版本信息

    public static final int SCAN_LOCAL_FILE = 1154;
    public static final int VIEW_SCAN_LOCAL_FILE_SUCCESS = 1155; //扫描本地文件成功
    public static final int VIEW_SCAN_LOCAL_FILE_ERROR = 1156; //扫描本地文件失败
    public static final int VIEW_SCAN_LOCAL_FILE_BY_CHECK_PERMISSION = 1157; //扫描本地文件时检查权限
    public static final int VIEW_SELECT_LOCAL_FILE_SUCCESS = 1158; //选择本地文件成功
    public static final int SELECT_LOCAL_FILE_ERROR = 1159;
    public static final int VIEW_SELECT_IMG_FILE_SUCCESS = 1160; //选择图片文件成功
    public static final int THREAD_SELECT_VIDEO_FILE = 1161; //开始选择图片文件

    public static final int THREAD_DOWNLOAD_APP_BY_MAIN = 1162; //在主页面进行app下载
    public static final int VIEW_DOWNLOAD_APP_BY_MAIN_START = 1163; //在主页面上弹窗显示app下载
    public static final int VIEW_DOWNLOAD_APP_BY_MAIN_LOADING = 1164; //在主页面上更新弹窗显示app下载进度
    public static final int VIEW_DOWNLOAD_APP_BY_MAIN_SUCCESS = 1165; //在主页面上更新app成功
    public static final int VIEW_DOWNLOAD_APP_BY_MAIN_ERROR = 1166; //在主页面上更新app失败
    public static final int THREAD_DOWNLOAD_APP_BY_SETTINGS = 1167; //在设置页面或pad模式下进行app下载
    public static final int VIEW_DOWNLOAD_APP_BY_SETTINGS_START = 1168;  //在设置页面或pad模式下弹窗显示app下载
    public static final int VIEW_DOWNLOAD_APP_BY_SETTINGS_LOADING = 1169;  //在设置页面或pad模式下更新弹窗显示app下载进度
    public static final int VIEW_DOWNLOAD_APP_BY_SETTINGS_SUCCESS = 1170; //在设置页面或pad模式下更新app成功
    public static final int VIEW_DOWNLOAD_APP_BY_SETTINGS_ERROR = 1171; //在设置页面或pad模式下更新app失败
    public static final int VIEW_DOWNLOAD_MUSIC = 1172;  //刷新下载音乐进度
    public static final int VIEW_DOWNLOAD_MUSIC_SHOW = 1173; //展示下载音乐
    public static final int VIEW_DOWNLOAD_MUSIC_UPDATE= 1174;  //更新展示下载音乐的列表情况
    public static final int VIEW_DOWNLOAD_MUSIC_FINISH = 1175; //下载音乐完成
    public static final int VIEW_DOWNLOAD_MUSIC_CANCEL = 1176; //下载音乐取消

    public static final int GET_CURRENT_TIME = 1177;
    public static final int SHOW_FRAGMENT = 1178;
    public static final int VIEW_PLAY_FINISH_SUCCESS = 1179; //当前音乐播放完成后的处理
    public static final int VIEW_PLAY_ERROR = 1180; //当前音乐播放失败
    public static final int THREAD_GET_MUSIC_METADATA = 1181; //获取当前音乐meta数据源
    public static final int VIEW_GET_MUSIC_METADATA = 1182;  //获取当前音乐meta数据源后处理
    public static final int VIEW_REFRESH_MUSIC_MEMORY_VALUE = 1183; //刷新音乐文件大小值

    public static final int VIEW_PLAY_LIST_FIRST = 1184; //播放当前第一首音乐
    public static final int VIEW_PLAY_MUSIC_BY_CHARACTER = 1185;  //角色控制播放
    public static final int VIEW_PLAY_LOCAL_MUSIC = 1186; //播放本地音乐
    public static final int VIEW_ADD_LOCAL_MUSIC = 1187; //添加本地音乐到播放列表
    public static final int VIEW_PLAY_FAVORITE_MUSIC = 1188; //播放收藏音乐
    public static final int VIEW_ADD_FAVORITE_MUSIC = 1189; //在收藏列表中点击添加到播放列表里
    public static final int THREAD_ADD_MUSIC_TO_CUSTOM_PLAY_LIST = 1190; //添加歌曲到自建歌曲列表里面
    public static final int THREAD_DELETE_MUSIC_IN_CUSTOM_PLAY_LIST = 1191; //删除自建歌曲列表的某一首歌
    public static final int VIEW_PLAY_RECOMMEND_MUSIC = 1192; //点击每日推荐歌曲
    public static final int THREAD_SAVE_MUSIC_DATA = 1193; //保存本地文件到列表
    public static final int THREAD_GET_MUSIC_LYRIC = 1194;   //获取音乐歌词

    public static final int THREAD_SHOW_IMAGE_URL = 1195;    //展示音乐图片
    public static final int VIEW_MUSIC_IS_NEXT = 1196;  //播放下一首歌曲
    public static final int VIEW_MUSIC_IS_LAST = 1197;  //播放上一首歌曲
    public static final int VIEW_MUSIC_IS_PAUSE = 1198; //歌曲播放或暂停

    public static final int VIEW_SHOW_CHARACTER_TALK = 1200;
    public static final int VIEW_UPDATE_CHARACTER_STATUS = 1201;
    public static final int VIEW_HIDE_CHARACTER_TALK = 1202; //隐藏角色对话
    public static final int VIEW_NORMAL_STATUS_CHARACTER = 1203; //展示角色：正常状态
    public static final int VIEW_MOVE_STATUS_CHARACTER = 1204; //展示角色：动态
    public static final int VIEW_LISTEN_STATUS_CHARACTER_LEFT = 1205; //展示角色：听歌状态向左偏移
    public static final int VIEW_LISTEN_STATUS_CHARACTER_RIGHT = 1206; //展示角色：听歌状态向右偏移

    public static final int VIEW_SEEK_BAR_POS = 1207; //刷新当前音乐进度
    public static final int THREAD_SAVE_MUSIC_DATA_LIST = 1208; //保存整个列表到本地数据库

    public static final int VIEW_SEEK_BAR_RESUME = 1209; //重置进度条参数
    public static final int VIEW_PAUSE = 1210;   //处理音乐播放或暂停
    public static final int VIEW_SHOW_VISUALIZER =1211;
    public static final int VIEW_SHOW_STOP_VISUALIZER = 1212;
    public static final int VIEW_MUSIC_MSG = 1213;  //展示音乐信息
    public static final int VIEW_MUSIC_MSG_UPDATE = 1214; //更新音乐信息

    public static final int VIEW_ADD_MUSIC = 1215; //添加音乐到播放列表
    public static final int VIEW_SAVE_FAVORITE_MUSIC = 1216; //将该歌曲添加到收藏音乐列表中
    public static final int VIEW_ADD_MUSIC_TO_LOCAL_PLAY_LIST = 1217; //添加歌曲到自建歌曲列表里面
    public static final int VIEW_ADD_MUSIC_TO_LOCAL_PLAY_LIST_SUCCESS = 1218; //添加歌曲到自建歌曲列表里面显示成功
    public static final int VIEW_CANCEL_FAVORITE_MUSIC = 1219; //当前歌曲取消收藏
    public static final int VIEW_FRESH_FAVORITE_MUSIC = 1220; //刷新当前是否收藏音乐
    public static final int VIEW_DELETE_MUSIC = 1221; //删除播放列表下的音乐
    public static final int VIEW_DELETE_LOCAL_MUSIC = 1222; //删除本地歌曲列表下的音乐
    public static final int VIEW_DELETE_FAVORITE_MUSIC = 1223; //删除收藏音乐列表下的音乐
    public static final int VIEW_DELETE_MUSIC_TO_LOCAL_PLAY_LIST = 1224; //删除自建歌单里面到某一首歌曲
    public static final int VIEW_DELETE_MUSIC_TO_LOCAL_PLAY_LIST_SUCCESS = 1225; //删除自建歌单里面到某一首歌曲成功
    public static final int VIEW_LYRIC = 1226; //展示歌词
    public static final int VIEW_IMAGE_URL = 1227; //展示音乐图片链接
    public static final int VIEW_SHOW_OR_HIDE_MASKING_BACKGROUND = 1228; //是否显示遮罩层
    public static final int VIEW_CHANGE_THEME = 1229; //变更主题
    public static final int VIEW_SETTING_LAUNCH_VIDEO_SUCCESS = 1230; //设置启动页面的视频成功
    public static final int VIEW_SETTING_LAUNCH_VIDEO_ERROR = 1231; //设置启动页面的视频失败
    public static final int VIEW_CONTROLLER_MODE = 1232; //切换简约播放控制器模式
    public static final int VIEW_NEW_CONTROLLER_MODE = 1233; //切换新版播放控制器模式
    public static final int VIEW_BG_MODE = 1234; //切换背景模式
    public static final int VIEW_INTO_SET_BG = 1235; //设置背景 - 进入系统图片
    public static final int VIEW_CONTROLLER_MODE_FLOATING = 1236; //点击切换悬浮模式

    public static final int VIEW_CLICK_LOCAL_OR_FAVORITE = 1237; //点击本地列表或收藏夹列表
    public static final int VIEW_HIDE_LOCAL_OR_FAVORITE = 1238; //隐藏本地列表或收藏夹列表
    public static final int VIEW_COUNT_DOWN_REFRESH = 1239; //歌曲定时任务刷新
    public static final int VIEW_COUNT_DOWN_FINISH = 1240; //歌曲定时任务完成

    public static final int VIEW_SCREEN_LOCK = 1241;
    public static final int VIEW_SCREEN_UNLOCK = 1242;
    public static final int VIEW_CLOSE_FLOATING_LYRIC = 1243;

    /** 蓝牙部分控制 */
    public static final int VIEW_BLUETOOTH_DISCONNECT = 1244; //蓝牙断开时处理逻辑
    public static final int VIEW_ACTION_MEDIA_BUTTON = 1245; //已使用onKeyDown方法实现

    /** Room相关 */
    public static final int VIEW_ROOM_GET_THEME_ID = 1300; //本地存储：获取主题ID值
    public static final int VIEW_ROOM_GET_CONTROLLER_SCENE = 1301; //本地存储：获取控制器模式

    /**
     * Pad版本
     * */
    public static final int THREAD_PAD_CONNECT_MYSQL = 2000; //pad模式连接数据库
    public static final int VIEW_PAD_GET_DATA_LIST = 2001;
    public static final int VIEW_PAD_GET_SUCCESS = 2002;
    public static final int VIEW_PAD_CHANGE_FRAGMENT= 2003; //pad模式下切换页面
    public static final int VIEW_PAD_CHANGE_LAST_FRAGMENT= 2004; //pad模式下切换上一个页面

    public static final int THREAD_PAD_GET_DATA_LIST_BY_LIELLA = 2010;    //pad模式下获取Liella所有歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_SUNNY_PASSION = 2011; //pad模式下获取SUNNY PASSION所有歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_FOUR_YUU = 2012;    //pad模式下获取Liyuu所有歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_NIJIGASAKI = 2013;  //pad模式下获取虹团所有歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_AQOURS = 2014;  //pad模式下获取Aqours所有歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_US = 2015;  //pad模式下获取μ's所有歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_HASUNOSORA = 2016; //pad模式下获取莲团所有歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_SAINT_SNOW = 2017; //pad模式下获取saint snow所有歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_A_RISE = 2018; //pad模式下获取arise所有歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_OTHER = 2019; //pad模式下获取其他歌曲
    public static final int THREAD_PAD_GET_DATA_LIST_BY_BLUEBIRD = 2020; //pad模式下获取bluebird所有歌曲

    public static final int PAD_GET_DATA_LIST_BY_LOCAL_PLAY = 2050;
    public static final int VIEW_PAD_PLAY_MUSIC = 2051; //pad模式下播放歌曲
    public static final int VIEW_PAD_ADD_MUSIC = 2052; //pad模式下添加歌曲到播放列表
    public static final int VIEW_PAD_PLAY_ALL_MUSIC = 2053; //pad模式下播放所有歌曲
    public static final int VIEW_PAD_GET_MUSIC_METADATA = 2054; //pad模式下获取音乐meta数据源


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
    public long l;
    public Music music;
    public RoomPlayMusic roomPlayMusic;
    public RoomCustomPlay roomCustomPlay;
    public Bitmap bitmap;
    public SpannableStringBuilder ssb;
    public File file;
    public Double d;
    public KeyEvent kt;

    public ThreadEvent(int msgCode) { this.msgCode = msgCode; }
    //public ThreadEvent(int msgCode, List<Music> musicList) { this.msgCode = msgCode; this.musicList = musicList; }
    public ThreadEvent(int msgCode, List<T> tList) { this.msgCode = msgCode; this.tList = tList; }
    public ThreadEvent(int msgCode, List<T> tList, int i) { this.msgCode = msgCode; this.tList = tList; this.i = i; }
    public ThreadEvent(int msgCode, List<T> tList, String str) { this.msgCode = msgCode; this.tList = tList; this.str = str;}
    public ThreadEvent(int msgCode, String str) { this.msgCode = msgCode; this.str = str; }
    public ThreadEvent(int msgCode, String str, String str2) { this.msgCode = msgCode; this.str = str; this.str2 = str2; }
    public ThreadEvent(int msgCode, String str, String str2, String str3) { this.msgCode = msgCode; this.str = str; this.str2 = str2; this.str3 = str3;}
    public ThreadEvent(int msgCode, String str, String str2, String str3,  Bitmap bitmap, boolean b) { this.msgCode = msgCode; this.str = str; this.str2 = str2; this.str3 = str3; this.bitmap = bitmap; this.b = b; }
    public ThreadEvent(int msgCode, String str, String str2, String str3, boolean b) { this.msgCode = msgCode; this.str = str; this.str2 = str2; this.str3 = str3; this.b = b; }
    public ThreadEvent(int msgCode, String str, int i) { this.msgCode = msgCode; this.str = str; this.i = i;}
    public ThreadEvent(int msgCode, String str, int i, int i2, byte[] byteArray) { this.msgCode = msgCode; this.str = str; this.i = i; this.i2 = i2; this.byteArray = byteArray; }
    public ThreadEvent(int msgCode, String str, int i, long l, byte[] byteArray) { this.msgCode = msgCode; this.str = str; this.i = i; this.l = l; this.byteArray = byteArray; }
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
    public ThreadEvent(int msgCode, KeyEvent kt) { this.msgCode = msgCode; this.kt = kt; }

    public ThreadEvent(int msgCode, RoomPlayMusic roomPlayMusic) { this.msgCode = msgCode; this.roomPlayMusic = roomPlayMusic; }
    public ThreadEvent(int msgCode, RoomPlayMusic roomPlayMusic, int i) { this.msgCode = msgCode; this.roomPlayMusic = roomPlayMusic; this.i = i; }
    public ThreadEvent(int msgCode, RoomPlayMusic roomPlayMusic, long l) { this.msgCode = msgCode; this.roomPlayMusic = roomPlayMusic; this.l = l; }
    public ThreadEvent(int msgCode, RoomPlayMusic roomPlayMusic, int i, int i2) { this.msgCode = msgCode; this.roomPlayMusic = roomPlayMusic; this.i = i; this.i2 = i2; }
    public ThreadEvent(int msgCode, RoomPlayMusic roomPlayMusic, int i, long l) { this.msgCode = msgCode; this.roomPlayMusic = roomPlayMusic; this.i = i; this.l = l;}
    public ThreadEvent(int msgCode, RoomPlayMusic roomPlayMusic, boolean b) { this.msgCode = msgCode; this.roomPlayMusic = roomPlayMusic; this.b = b; }
    public ThreadEvent(int msgCode, RoomPlayMusic roomPlayMusic, boolean b, long l) { this.msgCode = msgCode; this.roomPlayMusic = roomPlayMusic; this.b = b; this.l = l;}
    public ThreadEvent(int msgCode, RoomPlayMusic roomPlayMusic, boolean b, String str, String str2) { this.msgCode = msgCode; this.roomPlayMusic = roomPlayMusic; this.b = b; this.str = str; this.str2 = str2; }
    public ThreadEvent(int msgCode, RoomPlayMusic roomPlayMusic, boolean b, String str, String str2, List<T> tList) { this.msgCode = msgCode; this.roomPlayMusic = roomPlayMusic; this.b = b; this.str = str; this.str2 = str2; this.tList = tList; }
}
