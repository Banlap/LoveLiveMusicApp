package com.banlap.llmusic.uivm;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.banlap.llmusic.R;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.sql.MysqlHelper;
import com.banlap.llmusic.utils.CharacterHelper;
import com.banlap.llmusic.utils.OkhttpUtil;
import com.banlap.llmusic.utils.SPUtil;
import com.banlap.llmusic.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @author Banlap on 2021/11/30
 */
public class MainVM extends AndroidViewModel {

    public MainCallBack callBack;
    public static final int CHARACTER_NAME_KEKE_INT = 100;     //角色：唐可可
    public static final int CHARACTER_NAME_KANON_INT = 101;    //角色：涩谷香音

    private static final int NORMAL_STATUS_CHARACTER = 1001;    //角色正常状态
    private static final int MOVE_STATUS_CHARACTER = 1002;      //角色动态状态
    private static final int LISTEN_STATUS_CHARACTER_LEFT = 1003;   //角色听歌状态 左
    private static final int LISTEN_STATUS_CHARACTER_RIGHT = 1004;  //角色听歌状态 右
    private static final int IMG_BITMAP_LIMIT_SIZE = 900000;      //显示图片的最大限值 （超过该值则需要压缩）
    private boolean isDownloadStop = false;  //是否取消下载app


    public MainVM(@NonNull Application application) { super(application); }

    public void setCallBack(MainCallBack callBack) { this.callBack = callBack; }

    /** 角色对话handler */
    private static final Handler talkHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            //正常操作
            if(msg.what == 0) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_HIDE_CHARACTER_TALK));
            }
            talkHandler.sendEmptyMessageDelayed(msg.what -1, 1000);
        }
    };

    /** 角色Handler */
    private static final Handler animatedHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            //正常操作
            if(msg.what == 0) {
                if(msg.arg1 == NORMAL_STATUS_CHARACTER) {
                    //Log.i("LogByAB", "NORMAL");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_MOVE_STATUS_CHARACTER, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = MOVE_STATUS_CHARACTER;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                } else if(msg.arg1 == MOVE_STATUS_CHARACTER) {
                    //Log.i("LogByAB", "MOVE");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_NORMAL_STATUS_CHARACTER, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = NORMAL_STATUS_CHARACTER;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                } else if(msg.arg1 == LISTEN_STATUS_CHARACTER_LEFT) {
                    //Log.i("LogByAB", "MOVE");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_LISTEN_STATUS_CHARACTER_LEFT, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = LISTEN_STATUS_CHARACTER_RIGHT;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                } else if (msg.arg1 == LISTEN_STATUS_CHARACTER_RIGHT) {
                    //Log.i("LogByAB", "MOVE");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_LISTEN_STATUS_CHARACTER_RIGHT, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = LISTEN_STATUS_CHARACTER_LEFT;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                }
            } else {
                android.os.Message message = new Message();
                message.what = msg.what - 1;
                message.arg1 = msg.arg1;
                message.arg2 = msg.arg2;
                animatedHandler.sendMessageDelayed(message, 500);
            }

        }
    };

    /** 获取歌词文本 */
    public void showLyric(Music dataSource, final boolean isLoop) {
        List<MusicLyric> musicLyricList = new ArrayList<>();
        String lyricUrl = dataSource.musicLyric != null ? dataSource.musicLyric : "";
        if(!lyricUrl.equals("")) {
            OkhttpUtil.getInstance().request(lyricUrl, new OkhttpUtil.OkHttpCallBack() {
                @Override
                public void onSuccess(Response response) {
                    try {
                        String ly="";   //歌词文本
                        String lyWithoutTime="";   //歌词文本
                        InputStream is = response.body().byteStream();
                        if (is != null) {
                            InputStreamReader inputreader = new InputStreamReader(is);
                            BufferedReader buffreader = new BufferedReader(inputreader);
                            String line;
                            int id=0;
                            //分行读取
                            while (( line = buffreader.readLine()) != null) {
                                ly += line + "\n";
                                lyWithoutTime += line.substring(line.indexOf("]")+1) + "\n";

                                if(!line.equals("")) {
                                    String time = "", lyric="";
                                    if("[".equals(line.substring(0,1))) {
                                        time = line.substring(line.indexOf("[")+1, line.indexOf("]"));
                                        lyric = line.substring(line.indexOf("]")+1);
                                    } else {
                                        lyric = line;
                                    }
                                    if(!lyric.equals("") && 0 != lyric.trim().length()) {
                                        MusicLyric musicLyric = new MusicLyric();
                                        boolean isSetLyric = false;
                                        if(!time.equals("")) {
                                            if(musicLyricList.size() >0) {
                                                for(int i=0; i< musicLyricList.size(); i++) {
                                                    if(!"00:00".equals(time)) {
                                                        if(time.equals(musicLyricList.get(i).lyricTime)) {
                                                            musicLyricList.get(i).setLyricContext2(lyric);
                                                            isSetLyric = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if(!isSetLyric) {
                                            musicLyric.setLyricId(id++);
                                            musicLyric.setLyricTime(time);
                                            musicLyric.setLyricContext(lyric);
                                            musicLyric.setLyricContext2("");
                                            musicLyricList.add(musicLyric);
                                            if(musicLyricList.size() >1) {
                                                musicLyricList.get(musicLyricList.size() -2).setLyricEndTime(time);
                                            }
                                        }

                                    }
                                }
                            }
                            is.close();
                            EventBus.getDefault().post(new ThreadEvent<MusicLyric>(ThreadEvent.VIEW_LYRIC, dataSource, isLoop, ly, lyWithoutTime, musicLyricList));
                        }
                    } catch (Exception e) {
                        Log.i("ABMediaPlay", "http error " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String e) {
                    Log.i("ABMediaPlay", "error: " + e);
                    EventBus.getDefault().post(new ThreadEvent<MusicLyric>(ThreadEvent.VIEW_LYRIC, dataSource, isLoop, "", "", musicLyricList));
                }
            });
        } else {
            EventBus.getDefault().post(new ThreadEvent<MusicLyric>(ThreadEvent.VIEW_LYRIC, dataSource, isLoop, "", "", musicLyricList));
        }
    }

    /** 获取网络图片 */
    public void showImageURL(String musicName, String musicSinger, String dataSource) {

        OkhttpUtil.getInstance().request(dataSource, new OkhttpUtil.OkHttpCallBack() {
            @Override
            public void onSuccess(Response response) {
                try {
                    InputStream inputStream = response.body().byteStream();
                    //inputStream调用一次后会被清空
                    byte[] inputStream2ByteArr = inputStream2ByteArr(inputStream);
                    //使用工厂把网络的输入流生产Bitmap
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //options.inJustDecodeBounds = true;
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 1; // 1 不压缩, 4 为宽和高变为原来的1/4，即图片压缩为原来的1/16
                    Bitmap bitmap = BitmapFactory.decodeByteArray(inputStream2ByteArr, 0, inputStream2ByteArr.length, options);
                    //Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                    //重新压缩图片
                    BitmapFactory.Options optionsNew = new BitmapFactory.Options();
                    optionsNew.inJustDecodeBounds = false;
                    optionsNew.inSampleSize = 4;//宽和高变为原来的1/4，即图片压缩为原来的1/16
                    Bitmap bitmapNew = BitmapFactory.decodeByteArray(inputStream2ByteArr, 0, inputStream2ByteArr.length, optionsNew);
                    //Bitmap bitmapNew = BitmapFactory.decodeStream(inputStream, null, optionsNew);

                    //计算当前bitmap大小
                    Log.i("LogByAB", "bitmap: " + getBitmapSize(bitmap));
                    Log.i("LogByAB", "bitmapNew: " + getBitmapSize(bitmapNew));
                    if (getBitmapSize(bitmap) >= IMG_BITMAP_LIMIT_SIZE) {
                        Log.i("LogByAB", "bitmap: resize");
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, bitmapNew));
                    } else {
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, bitmap));
                    }
                } catch (Exception e) {
                    Log.i("ABMediaPlay", "error " + e.getMessage());
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, dataSource, (Bitmap) null, false));
                }
            }

            @Override
            public void onError(String e) {
                Log.i("ABMediaPlay", "error: " + e);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, dataSource, (Bitmap) null, false));
            }
        });
    }

    /** 展示本地文件图片 */
    public void showImageBitmap(String musicName, String musicSinger, Bitmap bitmap) {
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, bitmap));
    }

    /** 下载新版本App */
    public void downloadUrl(String dataSource) {
        isDownloadStop = false;
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_START));

        OkhttpUtil.getInstance().request(dataSource, new OkhttpUtil.OkHttpCallBack() {
            @Override
            public void onSuccess(Response response) {
                downloadApp(response);
            }

            @Override
            public void onError(String e) {
                Log.i("ABMediaPlay", "error " + e);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_ERROR));
            }
        });
    }

    /** 开始下载新版本App */
    private void downloadApp(Response response) {
        try {
            //数据缓冲
            byte[] bs = new byte[1024];
            int len;
            long total = 0;

            long contentLength = response.body().contentLength();
            InputStream is = response.body().byteStream();

            String path="";
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                path = getApplication().getExternalFilesDir(null).getAbsolutePath() + "/LLMusic.apk";
            } else {
                path = Environment.getExternalStorageDirectory().getPath() + "/LLMusic.apk";
            }
            File file = new File(path);
            if(file.exists()){    //如果目标文件已经存在
                file.delete();    //则删除旧文件
            }
            OutputStream os = new FileOutputStream(file);
            //开始读取
            while((len = is.read(bs)) != -1){
                total += len;
                int progress = (int) (100 * (total / (double) contentLength));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_LOADING, progress));
                //Log.i("LogByAB","Download progress: " + (100 * (total / (double) contentLength)));
                if(isDownloadStop) {
                    file.delete(); //取消下载则删除文件
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_SUCCESS, false));
                    os.close();
                    is.close();
                    return;
                }
                os.write(bs,0,len);
            }
            //完毕关闭所有连接
            os.close();
            is.close();
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_SUCCESS, true, file));
        } catch (Exception e) {
            Log.i("ABMediaPlay", "error " + e.getMessage());
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_ERROR));
        }
    }

    /** 更改App下载状态：取消下载 */
    public void changeDownloadApp(boolean status){
        isDownloadStop = status;
    }

    /** 角色默认状态 */
    public static void initAnimatedCharacter(String characterName) {
        android.os.Message message = new Message();
        message.what = 2;
        message.arg1 = NORMAL_STATUS_CHARACTER;
        if(characterName.equals(CharacterHelper.CHARACTER_NAME_KANON)) {
            message.arg2 = CHARACTER_NAME_KANON_INT;
        } else if(characterName.equals(CharacterHelper.CHARACTER_NAME_KEKE)) {
            message.arg2 = CHARACTER_NAME_KEKE_INT;
        }
        animatedHandler.sendMessage(message);
    }

    /** 角色听歌状态 */
    public static void animatedListenCharacter(String characterName) {
        android.os.Message message = new Message();
        message.what = 2;
        message.arg1 = LISTEN_STATUS_CHARACTER_LEFT;
        if(characterName.equals(CharacterHelper.CHARACTER_NAME_KANON)) {
            message.arg2 = CHARACTER_NAME_KANON_INT;
        } else if(characterName.equals(CharacterHelper.CHARACTER_NAME_KEKE)) {
            message.arg2 = CHARACTER_NAME_KEKE_INT;
        }
        animatedHandler.sendMessage(message);
    }

    /** 角色默认状态 */
    public static void stopHandler() {
        animatedHandler.removeCallbacksAndMessages(null);
    }

    /** 角色默认状态 */
    public static void stopTalkHandler() {
        talkHandler.removeCallbacksAndMessages(null);
    }

    /** 是否已经开启弹窗权限*/
    public static boolean isCanDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 展示每日推荐
     * */
    public static void showRecommendData(Context context) {
        String recommendDate = SPUtil.getStrValue(context, SPUtil.RecommendDate);
        if(!TextUtils.isEmpty(recommendDate) && !TimeUtil.isCheckTime(recommendDate, 24)) {
            //本地缓存列表
            List<Music> spList = SPUtil.getListValue(context, SPUtil.RecommendListData, Music.class);
            if(spList.size() >0){
                EventBus.getDefault().post(new ThreadEvent<>(ThreadEvent.GET_RECOMMEND_SUCCESS, spList));
            }
            return;
        }
        //获取最新的每日推荐数据
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_RECOMMEND));
        SPUtil.setStrValue(context, SPUtil.RecommendDate, TimeUtil.getCurrentDateStr());
    }

    /**
     * 判断服务是否开启
     *
     * @param mContext 上下文
     * @param className 服务class名
     * @return true:开启 false:未开启
     */
    public static boolean isWorked(Context mContext, String className) {
        ActivityManager myManager = (ActivityManager) mContext
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }


    /** 默认存储Music值 */
    public static Music setMusicMsg(Music musicMsg, boolean isPlaying) {
        Music music = new Music();
        music.setMusicId(musicMsg.getMusicId());
        music.setMusicName(musicMsg.getMusicName());
        music.setMusicSinger(musicMsg.getMusicSinger());
        music.setMusicType(musicMsg.getMusicType());
        music.setMusicImg(musicMsg.getMusicImg());
        music.setMusicURL(musicMsg.getMusicURL());
        music.setMusicFavorite(musicMsg.getMusicFavorite());
        music.setMusicLyric(musicMsg.getMusicLyric());
        music.setMusicImgByte(musicMsg.getMusicImgByte());
        music.setLocal(musicMsg.isLocal);
        music.isPlaying = isPlaying;
        return music;
    }

    /** 转换成时间格式*/
    public String rebuildTime(long position) {
        long minLong = position /1000/60;
        long secLong = position /1000%60;
        String minStr = minLong <10 ? "0"+minLong : ""+minLong;
        String secStr = secLong <10 ? "0"+secLong : ""+secLong;
        return minStr + ":" + secStr;
    }

    public interface MainCallBack {

    }


    /**
     * 获取bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /** 将输入流转为为字节数组 */
    private byte[] inputStream2ByteArr(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buff)) != -1) {
            outputStream.write(buff, 0, len);
        }
        inputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 原始图片的宽、高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        /**
         * 压缩方式一
         */
        // 计算压缩的比例：分为宽高比例
        final int heightRatio = Math.round((float) height
                / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        return inSampleSize;
    }

}
