package com.banlap.llmusic.uivm;

import android.Manifest;
import android.app.Application;
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
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.banlap.llmusic.R;
import com.banlap.llmusic.model.Music;
import com.banlap.llmusic.model.MusicLyric;
import com.banlap.llmusic.request.ThreadEvent;
import com.banlap.llmusic.utils.CharacterHelper;

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

/**
 * @author Banlap on 2021/11/30
 */
public class MainVM extends AndroidViewModel {

    private final Object lock = new Object();
    private boolean isStop = false;
    public MainCallBack callBack;
    private MediaPlayer mediaPlayer;
    public static final int CHARACTER_NAME_KEKE_INT = 100;
    public static final int CHARACTER_NAME_KANON_INT = 101;

    private static final int NORMAL_STATUS_CHARACTER = 1001;
    private static final int MOVE_STATUS_CHARACTER = 1002;
    private static final int LISTEN_STATUS_CHARACTER_LEFT = 1003;
    private static final int LISTEN_STATUS_CHARACTER_RIGHT = 1004;
    private boolean isDownloadStop = false;  //??????????????????app


    public MainVM(@NonNull Application application) { super(application); }

    public void setCallBack(MainCallBack callBack) { this.callBack = callBack; }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                while (true) {
                    while (isStop) {
                        synchronized (lock) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.GET_CURRENT_TIME,  rebuildTime(currentPosition), currentPosition));
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_SEEK_BAR_POS, currentPosition));
                    //callBack.viewSeekBarPos(mediaPlayer.getCurrentPosition());
                }
            }
        }
    };


    private static final Handler talkHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            //????????????
            if(msg.what == 0) {
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_HIDE_CHARACTER_TALK));
            }
            talkHandler.sendEmptyMessageDelayed(msg.what -1, 1000);
        }
    };

    private static final Handler animatedHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            //????????????
            if(msg.what == 0) {
                if(msg.arg1 == NORMAL_STATUS_CHARACTER) {
                    //Log.e("LogByAB", "NORMAL");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_MOVE_STATUS_CHARACTER, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = MOVE_STATUS_CHARACTER;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                } else if(msg.arg1 == MOVE_STATUS_CHARACTER) {
                    //Log.e("LogByAB", "MOVE");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_NORMAL_STATUS_CHARACTER, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = NORMAL_STATUS_CHARACTER;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                } else if(msg.arg1 == LISTEN_STATUS_CHARACTER_LEFT) {
                    //Log.e("LogByAB", "MOVE");
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_LISTEN_STATUS_CHARACTER_LEFT, msg.arg2));
                    android.os.Message message = new Message();
                    message.what = 2;
                    message.arg1 = LISTEN_STATUS_CHARACTER_RIGHT;
                    message.arg2 = msg.arg2;
                    animatedHandler.sendMessage(message);
                } else if (msg.arg1 == LISTEN_STATUS_CHARACTER_RIGHT) {
                    //Log.e("LogByAB", "MOVE");
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

    public void showLyric(Music dataSource, final boolean isLoop) {
        HttpURLConnection connection=null;
        try {
            String ly="";   //????????????
            String lyWithoutTime="";   //????????????
            String lyLineTime="";   //????????????
            List<MusicLyric> musicLyricList = new ArrayList<>();
            String lyricUrl = dataSource.musicLyric !=null ? dataSource.musicLyric : "";
            if(!lyricUrl.equals("")) {
                //Log.e("ABMediaPlay", "url: " + lyricUrl);
                URL url = new URL(lyricUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET"); //????????????
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                if(connection.getResponseCode() == 200) {
                    InputStream is = connection.getInputStream();
                    if (is != null) {
                        InputStreamReader inputreader = new InputStreamReader(is);
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line;
                        int id=0;
                        //????????????
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
                } else {
                    Log.e("ABMediaPlay", "http code: error " + connection.getResponseCode());
                }
            } else {
                EventBus.getDefault().post(new ThreadEvent<MusicLyric>(ThreadEvent.VIEW_LYRIC, dataSource, isLoop, ly, lyWithoutTime, musicLyricList));
            }
        } catch (Exception e) {
            Log.e("ABMediaPlay", "http error " + e.getMessage());
            e.printStackTrace();
        } finally {
            if(connection!=null) {
                connection.disconnect();
            }
        }
    }

    /** ?????????????????? */
    public void showImageURL(String musicName, String musicSinger, String dataSource) {
        HttpURLConnection connection=null;
        try {
            URL url = new URL(dataSource);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); //????????????
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            if(connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                //inputStream???????????????????????????
                byte[] inputStream2ByteArr = inputStream2ByteArr(inputStream);
                //???????????????????????????????????????Bitmap
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inJustDecodeBounds = true;
                options.inJustDecodeBounds = false;
                options.inSampleSize = 1; // 1 ?????????, 4 ???????????????????????????1/4??????????????????????????????1/16
                Bitmap bitmap = BitmapFactory.decodeByteArray(inputStream2ByteArr,0,inputStream2ByteArr.length, options);
                //Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                //??????????????????
                BitmapFactory.Options optionsNew = new BitmapFactory.Options();
                optionsNew.inJustDecodeBounds = false;
                optionsNew.inSampleSize = 4;//????????????????????????1/4??????????????????????????????1/16
                Bitmap bitmapNew = BitmapFactory.decodeByteArray(inputStream2ByteArr,0,inputStream2ByteArr.length, optionsNew);
                //Bitmap bitmapNew = BitmapFactory.decodeStream(inputStream, null, optionsNew);

                //????????????bitmap??????
                Log.e("LogByAB", "bitmap: " + getBitmapSize(bitmap));
                Log.e("LogByAB", "bitmapNew: " + getBitmapSize(bitmapNew));
                if(getBitmapSize(bitmap)>=900000) {
                    Log.e("LogByAB", "bitmap: resize" );
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, bitmapNew));
                } else {
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, bitmap));
                }
            }
        } catch (Exception e) {
            Log.e("ABMediaPlay", "error " + e.getMessage());
        } finally {
            if(connection!=null) {
                connection.disconnect();
            }
        }
    }

    /** ???????????????????????? */
    public void showImageBitmap(String musicName, String musicSinger, Bitmap bitmap) {
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.VIEW_IMAGE_URL, musicName, musicSinger, bitmap));
    }

    /** ???????????????App */
    public void downloadUrl(String dataSource) {
        isDownloadStop = false;
        EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_START));
        HttpURLConnection connection=null;
        //????????????
        byte[] bs = new byte[1024];
        int len;
        long total = 0;
        try {
            URL url = new URL(dataSource);
            connection = (HttpURLConnection) url.openConnection();
            int contentLength = connection.getContentLength();
            InputStream is = connection.getInputStream();
            if (is == null) {
                throw new RuntimeException("stream is null");
            }
            String path="";
            if (Build.VERSION.SDK_INT > 29) {
                path = getApplication().getExternalFilesDir(null).getAbsolutePath() + "/LLMusic.apk";
            } else {
                path = Environment.getExternalStorageDirectory().getPath() + "/LLMusic.apk";
            }
            File file = new File(path);
            if(file.exists()){    //??????????????????????????????
                file.delete();    //??????????????????
            }
            OutputStream os = new FileOutputStream(file);
            //????????????
            while((len = is.read(bs)) != -1){
                total += len;
                int progress = (int) (100 * (total / (double) contentLength));
                EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_LOADING, progress));
                //Log.e("LogByAB","Download progress: " + (100 * (total / (double) contentLength)));
                if(isDownloadStop) {
                    file.delete(); //???????????????????????????
                    EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_SUCCESS, false));
                    os.close();
                    is.close();
                    return;
                }
                os.write(bs,0,len);
            }
            //????????????????????????
            os.close();
            is.close();
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_SUCCESS, true, file));
        }catch (Exception e) {
            Log.e("ABMediaPlay", "error " + e.getMessage());
            EventBus.getDefault().post(new ThreadEvent(ThreadEvent.DOWNLOAD_APP_ERROR));
        } finally {
            if(connection!=null) {
                connection.disconnect();
            }
        }

    }

    public void changeDownloadApp(boolean status){
        isDownloadStop = status;
    }


    /** ?????? */
    public void stop() {
        isStop = true;
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /** ???????????? */
    public void showCharacterContent() {
        android.os.Message message = new Message();
        message.what = 3;
        talkHandler.sendMessage(message);
    }

    public static void showContent() {
        android.os.Message message = new Message();
        message.what = 3;
        talkHandler.sendMessage(message);
    }

    /** ?????????????????? */
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

    /** ?????????????????? */
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

    /** ?????????????????? */
    public static void stopHandler() {
        animatedHandler.removeCallbacksAndMessages(null);
    }

    /** ?????????????????? */
    public static void stopTalkHandler() {
        talkHandler.removeCallbacksAndMessages(null);
    }

    /** ?????????????????????*/
    public String rebuildTime(long position) {
        long minLong = position /1000/60;
        long secLong = position /1000%60;
        String minStr = minLong <10 ? "0"+minLong : ""+minLong;
        String secStr = secLong <10 ? "0"+secLong : ""+secLong;
        return minStr + ":" + secStr;
    }

    public interface MainCallBack {
        void viewSeekBarResume();
        void viewPause(boolean isPause);
        void viewMusicMsg(Music musicSource, int allPos);
        void viewSeekBarPos(int pos);
    }


    /**
     * ??????bitmap?????????
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // ?????????????????????????????????x??????
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /** ????????????????????????????????? */
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
        // ????????????????????????
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

//		if (height > reqHeight || width > reqWidth) {
//			//?????????????????????????????????????????????
//			/**
//			 * ???????????????
//			 */
//			// final int halfHeight = height / 2;
//			// final int halfWidth = width / 2;
//			// while ((halfHeight / inSampleSize) > reqHeight
//			// && (halfWidth / inSampleSize) > reqWidth) {
//			// inSampleSize *= 2;
//			// }
//
        /**
         * ???????????????
         */
        // ??????????????????????????????????????????
        final int heightRatio = Math.round((float) height
                / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//		}

        return inSampleSize;
    }

}
