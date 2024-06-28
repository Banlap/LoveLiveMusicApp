package com.banlap.llmusic.pad.uivm.vm;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.utils.BitmapUtil;
import com.banlap.llmusic.utils.CharacterHelper;
import com.banlap.llmusic.utils.OkhttpUtil;
import com.banlap.llmusic.utils.SPUtil;
import com.banlap.llmusic.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;

public class PadMainVM extends AndroidViewModel {
    private static final String TAG = PadMainVM.class.getSimpleName();

    public static final int CHARACTER_NAME_KEKE_INT = 100;     //角色：唐可可
    public static final int CHARACTER_NAME_KANON_INT = 101;    //角色：涩谷香音

    private static final int NORMAL_STATUS_CHARACTER = 1001;    //角色正常状态
    private static final int MOVE_STATUS_CHARACTER = 1002;      //角色动态状态
    private static final int LISTEN_STATUS_CHARACTER_LEFT = 1003;   //角色听歌状态 左
    private static final int LISTEN_STATUS_CHARACTER_RIGHT = 1004;  //角色听歌状态 右

    private boolean isDownloadStop = false;  //是否取消下载app

    private PadMainCallBack callBack;

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
                    //Log.i(TAG, "NORMAL");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_MOVE_STATUS_CHARACTER, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = MOVE_STATUS_CHARACTER;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                } else if(msg.arg1 == MOVE_STATUS_CHARACTER) {
                    //Log.i(TAG, "MOVE");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_NORMAL_STATUS_CHARACTER, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = NORMAL_STATUS_CHARACTER;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                } else if(msg.arg1 == LISTEN_STATUS_CHARACTER_LEFT) {
                    //Log.i(TAG, "MOVE");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_LISTEN_STATUS_CHARACTER_LEFT, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = LISTEN_STATUS_CHARACTER_RIGHT;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                } else if (msg.arg1 == LISTEN_STATUS_CHARACTER_RIGHT) {
                    //Log.i(TAG, "MOVE");
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

    public PadMainVM(@NonNull Application application) {
        super(application);
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
            } else {
                //获取最新的每日推荐数据
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_RECOMMEND));
                SPUtil.setStrValue(context, SPUtil.RecommendDate, TimeUtil.getCurrentDateStr());
            }
            return;
        }
        //获取最新的每日推荐数据
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_DATA_RECOMMEND));
        SPUtil.setStrValue(context, SPUtil.RecommendDate, TimeUtil.getCurrentDateStr());
    }

    /** 获取歌词 */
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
                    Bitmap bitmap = BitmapUtil.getInstance().showBitmap(response.body().byteStream());
                    if(bitmap != null) {
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, bitmap));
                    } else {
                        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, dataSource, (Bitmap) null, false));
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

    /** 默认存储Music值 */
    public static Music setMusicMsg(Music musicMsg, boolean isPlaying) {
        musicMsg.isPlaying = isPlaying;
        return musicMsg;
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


    /** 下载新版本App */
    public void downloadUrl(String url) {
        isDownloadStop = false;
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_START2));

        OkhttpUtil.getInstance().request(url, new OkhttpUtil.OkHttpCallBack() {
            @Override
            public void onSuccess(Response response) {
                downloadApp(response);
            }

            @Override
            public void onError(String e) {
                Log.i("ABMediaPlay", "error " + e);
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_ERROR2));
            }
        });
    }

    /** 下载新版本App */
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
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_LOADING2, progress));
                if(isDownloadStop) {
                    file.delete(); //取消下载则删除文件
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_SUCCESS2, false));
                    os.close();
                    is.close();
                    return;
                }
                os.write(bs,0,len);
            }
            //完毕关闭所有连接
            os.close();
            is.close();
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_SUCCESS2, true, file));
        } catch (Exception e) {
            Log.i("ABMediaPlay", "error " + e.getMessage());
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_ERROR2));
        }
    }

    public void changeDownloadApp(boolean status){
        isDownloadStop = status;
    }

    public void setCallBack(PadMainCallBack callBack) { this.callBack = callBack; }

    public void viewBack() { callBack.viewBack(); }

    public interface PadMainCallBack {
        void viewBack();
    }


}
