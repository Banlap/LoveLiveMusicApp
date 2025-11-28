package com.banlap.llmusic.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.banlap.llmusic.R;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.phone.ui.activity.MainActivity;
import com.banlap.llmusic.utils.BitmapUtil;
import com.banlap.llmusic.utils.NotificationHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 音乐播放器小组件 4x2
 * */
public class LLMusicWidgetProvider extends AppWidgetProvider {

    private static final String TAG = LLMusicWidgetProvider.class.getSimpleName();
    public static final String WIDGET_PROVIDER_REFRESH_MUSIC_MSG = "WIDGET_PROVIDER_REFRESH_MUSIC_MSG";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        String musicName, musicSinger;
        musicName = (MusicPlayService.currentRoomPlayMusic != null)? MusicPlayService.currentRoomPlayMusic.musicName : "";
        musicSinger = (MusicPlayService.currentRoomPlayMusic != null)? MusicPlayService.currentRoomPlayMusic.musicSinger : "";
        Log.i(TAG, "update success: musicName:" + musicName + " musicSinger: " + musicSinger );
        setRemoteViews(context, appWidgetManager, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        //Log.i(TAG, "send success: " + intent.getIntExtra("MusicProgress", 0));

        if (action.equals(WIDGET_PROVIDER_REFRESH_MUSIC_MSG)) {
            setRemoteViews(context, appWidgetManager, intent);
        }
        super.onReceive(context, intent);
    }


    /** 展示小组件视图 */
    private void setRemoteViews(Context context, AppWidgetManager appWidgetManager, Intent intent) {
        final ComponentName mComponentName = new ComponentName(context, LLMusicWidgetProvider.class);

        String musicName = "LLMusic", musicSinger = "LLSinger", startTime = "00:00", endTime = "00:00";
        boolean isStop = true, isLoading = false;
        int musicProgress = 0;

        if(intent != null) {
            isLoading = intent.getBooleanExtra("IsLoading", false);
            if(!TextUtils.isEmpty(intent.getStringExtra("StartTime"))) {
                startTime = intent.getStringExtra("StartTime");
            }
            if(!TextUtils.isEmpty(intent.getStringExtra("AllTime"))) {
                endTime = intent.getStringExtra("AllTime");
            }
            musicProgress = intent.getIntExtra("MusicProgress", 0);
        }

        //当小组件重新加入时 获取上次音乐信息
        String musicNameTemp = MusicPlayService.currentRoomPlayMusic != null? MusicPlayService.currentRoomPlayMusic.musicName : "";
        String musicSingerTemp = MusicPlayService.currentRoomPlayMusic != null? MusicPlayService.currentRoomPlayMusic.musicSinger : "";
        Bitmap bitmap = null;

        if(!TextUtils.isEmpty(musicNameTemp) && !TextUtils.isEmpty(musicSingerTemp)) {
            musicName = musicNameTemp;
            musicSinger = musicSingerTemp;

            if(MusicPlayService.currentRoomPlayMusic.musicImgByte != null) {
                boolean isExistsLastByteArray = false;
                if(MusicPlayService.lastWidgetByteArray != null) {
                    if (Arrays.equals(MusicPlayService.lastWidgetByteArray, MusicPlayService.currentRoomPlayMusic.musicImgByte)) {
                        isExistsLastByteArray = true;
                        //Log.i(TAG, "byte[] 未变化，跳过解码");
                    } else {
                        //Log.i(TAG, "byte[] 有变化，需要解码");
                    }
                } else {
                    // Log.i(TAG, "lastByteArray 为null，需要解码");
                }

                if(!isExistsLastByteArray) {
                    MusicPlayService.lastWidgetByteArray = MusicPlayService.currentRoomPlayMusic.musicImgByte;
                    MusicPlayService.lastWidgetBitmapRef = new WeakReference<>(BitmapUtil.getInstance().showBitmap(MusicPlayService.currentRoomPlayMusic.musicImgByte));
                }

                if(MusicPlayService.lastWidgetBitmapRef != null && MusicPlayService.lastWidgetBitmapRef.get() != null && !MusicPlayService.lastWidgetBitmapRef.get().isRecycled()) {
                    bitmap = MusicPlayService.lastWidgetBitmapRef.get();
                }
            }

            if(!MusicPlayService.isStop) {
                isStop = false;
            }
        }


        @SuppressLint("RemoteViewLayout")
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget_llmusic);

        remoteViews.setTextViewText(R.id.tv_music_name, musicName);
        remoteViews.setTextViewText(R.id.tv_music_singer, musicSinger);
        remoteViews.setTextViewText(R.id.tv_start_time, startTime);
        remoteViews.setTextViewText(R.id.tv_end_time, endTime);
        remoteViews.setProgressBar(R.id.pb_music_bar, 100, musicProgress, false);

        remoteViews.setImageViewResource(R.id.bt_play, context.getResources().getIdentifier(isStop ? "selector_play_black_selected" : "selector_pause_black_selected", "drawable", context.getPackageName()));
        remoteViews.setViewVisibility(R.id.pb_loading_music, isLoading? View.VISIBLE : View.INVISIBLE);

        Intent intentServiceIsPause = new Intent(context, MusicPlayService.class);
        intentServiceIsPause.setAction(MusicPlayService.INTENT_ACTION_PLAY);
        intentServiceIsPause.putExtra("IsPauseMusic", true);
        intentServiceIsPause.putExtra("MusicName", musicName);
        intentServiceIsPause.putExtra("MusicSinger", musicSinger);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.format(DecodeFormat.PREFER_RGB_565);

        if(bitmap !=null) {
            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .asBitmap()
                    .load(bitmap)
                    .transform(new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            remoteViews.setImageViewBitmap(R.id.iv_music_img, resource);
                        }
                    });
            intentServiceIsPause.putExtra("MusicBitmap", MusicPlayService.lastWidgetByteArray);
        } else {
            remoteViews.setImageViewResource(R.id.iv_music_img, context.getResources().getIdentifier("ic_llmp_new_2", "mipmap", context.getPackageName()));
            intentServiceIsPause.putExtra("MusicBitmap", (byte[]) null);
        }

        @SuppressLint("UnspecifiedImmutableFlag") //使用PendingIntent时 版本大于31时需要添加 FLAG_IMMUTABLE 或者 FLAG_MUTABLE
        PendingIntent pIntentIsPause = PendingIntent.getService(context, NotificationHelper.LL_MUSIC_PLAYER, intentServiceIsPause,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_play, pIntentIsPause);


        Intent intentServiceLast = new Intent(context, MusicPlayService.class);
        intentServiceLast.setAction(MusicPlayService.INTENT_ACTION_PLAY_LAST);
        intentServiceLast.putExtra("LastMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentLast = PendingIntent.getService(context, NotificationHelper.LL_MUSIC_PLAYER, intentServiceLast,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_last, pIntentLast);

        Intent intentServiceNext = new Intent(context, MusicPlayService.class);
        intentServiceNext.setAction(MusicPlayService.INTENT_ACTION_PLAY_NEXT);
        intentServiceNext.putExtra("NextMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentNext = PendingIntent.getService(context, NotificationHelper.LL_MUSIC_PLAYER, intentServiceNext,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_next, pIntentNext);

        Intent intentMain = new Intent(context, MainActivity.class);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        //FLAG_ACTIVITY_RESET_TASK_IF_NEEDED 按需启动的关键,如果任务队列中已经存在,则重建程序
        intentMain.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentMain = PendingIntent.getActivity(context, NotificationHelper.LL_MUSIC_PLAYER, intentMain,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_widget_music, pIntentMain);

        appWidgetManager.updateAppWidget(mComponentName, remoteViews);
    }


}
