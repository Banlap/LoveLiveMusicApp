package com.banlap.llmusic.utils;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.banlap.llmusic.R;
import com.banlap.llmusic.service.MusicIsPauseService;
import com.banlap.llmusic.service.MusicLastService;
import com.banlap.llmusic.service.MusicNextService;
import com.banlap.llmusic.ui.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 通知栏帮助类 展示LLMusic通知
 * */
public class NotificationHelper {
    private static final String TAG = NotificationHelper.class.getSimpleName();
    public static final int LL_MUSIC_PLAYER = 0x01;
    public static final int LL_MUSIC_CHARACTER= 0x02;
    public Notification notification;
    public NotificationCompat.Builder builder;
    public NotificationManager manager;
    public Bitmap roundedBitmap;
    public static NotificationHelper getInstance() { return new NotificationHelper(); }


    @SuppressLint("RemoteViewLayout")
    public Notification createRemoteViews(Context context, String musicName, String musicSinger, Bitmap bitmap, boolean isDefault) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                //Build.VERSION.SDK_INT >= Build.VERSION_CODES.S? R.layout.view_remote_notification_small_bar:
                        R.layout.view_remote_notification_bar);
        RemoteViews remoteBigViews = new RemoteViews(context.getPackageName(), R.layout.view_remote_notification_big_bar);

        remoteViews.setTextViewText(R.id.tv_music_name, musicName);
        remoteViews.setTextViewText(R.id.tv_singer_name, musicSinger);
        remoteBigViews.setTextViewText(R.id.tv_music_name, musicName);
        remoteBigViews.setTextViewText(R.id.tv_singer_name, musicSinger);

        if(isDefault) {
            remoteViews.setImageViewResource(R.id.bt_play, context.getResources().getIdentifier("selector_play_purple_notify_selected", "drawable", context.getPackageName()));
            remoteBigViews.setImageViewResource(R.id.bt_play, context.getResources().getIdentifier("selector_play_purple_notify_selected", "drawable", context.getPackageName()));
        } else {
            remoteViews.setImageViewResource(R.id.bt_play, context.getResources().getIdentifier("selector_pause_purple_notify_selected", "drawable", context.getPackageName()));
            remoteBigViews.setImageViewResource(R.id.bt_play, context.getResources().getIdentifier("selector_pause_purple_notify_selected", "drawable", context.getPackageName()));
        }

        Intent intentServiceIsPause = new Intent(context, MusicIsPauseService.class);
        intentServiceIsPause.putExtra("IsPauseMusic", true);
        intentServiceIsPause.putExtra("MusicName", musicName);
        intentServiceIsPause.putExtra("MusicSinger", musicSinger);

        if(bitmap !=null) {
            remoteViews.setImageViewBitmap(R.id.iv_music_img, bitmap);
            remoteBigViews.setImageViewBitmap(R.id.iv_music_img, bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  //压缩图片50% 防止显示不到图片
            byte[] bitmapByte = baos.toByteArray();
            intentServiceIsPause.putExtra("MusicBitmap", bitmapByte);
        } else {
            remoteViews.setImageViewResource(R.id.iv_music_img, context.getResources().getIdentifier("ic_llmp_2", "mipmap", context.getPackageName()));
            remoteBigViews.setImageViewResource(R.id.iv_music_img, context.getResources().getIdentifier("ic_llmp_2", "mipmap", context.getPackageName()));
            intentServiceIsPause.putExtra("MusicBitmap", (byte[]) null);
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        //使用PendingIntent时 版本大于31时需要添加 FLAG_IMMUTABLE 或者 FLAG_MUTABLE
        PendingIntent pIntentIsPause = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceIsPause,
                //Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_play, pIntentIsPause);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_play, pIntentIsPause);

        Intent intentServiceNext = new Intent(context, MusicNextService.class);
        intentServiceNext.putExtra("NextMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentNext = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceNext,
                //Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_next, pIntentNext);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_next, pIntentNext);

        Intent intentServiceLast = new Intent(context, MusicLastService.class);
        intentServiceLast.putExtra("LastMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentLast = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceLast,
                //Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_last, pIntentLast);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_last, pIntentLast);

        Intent intentMain = new Intent(context, MainActivity.class);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        //FLAG_ACTIVITY_RESET_TASK_IF_NEEDED 按需启动的关键,如果任务队列中已经存在,则重建程序
        intentMain.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentMain = PendingIntent.getActivity(context, LL_MUSIC_PLAYER, intentMain,
                //Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_music_all, pIntentMain);
        remoteBigViews.setOnClickPendingIntent(R.id.ll_music_all, pIntentMain);

        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_1", "LLMusic_channel", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, "channel_1");
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_llmp_small_1);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_llmp));
        builder.setCustomBigContentView(remoteBigViews);
        builder.setCustomContentView(remoteViews);

        notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        manager.notify(LL_MUSIC_PLAYER, notification);

       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.i(TAG, "bitmap != null: " + (bitmap !=null));
            if(bitmap != null) {
                try {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                    requestOptions.format(DecodeFormat.PREFER_RGB_565);

                    Glide.with(context)
                            .setDefaultRequestOptions(requestOptions)
                            .asBitmap()
                            .load(bitmap)
                            .transform(new RoundedCornersTransformation(15, 0, RoundedCornersTransformation.CornerType.ALL))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    remoteViews.setImageViewBitmap(R.id.iv_music_img, resource);
                                    remoteBigViews.setImageViewBitmap(R.id.iv_music_img, resource);
                                    manager.notify(LL_MUSIC_PLAYER, notification);
                                    roundedBitmap = resource;

                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    super.onLoadCleared(placeholder);
                                    if(roundedBitmap != null) {
                                        remoteViews.setImageViewBitmap(R.id.iv_music_img, roundedBitmap);
                                        remoteBigViews.setImageViewBitmap(R.id.iv_music_img, roundedBitmap);
                                        manager.notify(LL_MUSIC_PLAYER, notification);
                                    }

                                }
                            });
                } catch (Exception e) {
                    Log.e(TAG, "Exception1: " + e.getMessage());
                    remoteViews.setImageViewBitmap(R.id.iv_music_img, bitmap);
                    remoteBigViews.setImageViewBitmap(R.id.iv_music_img, bitmap);
                    manager.notify(LL_MUSIC_PLAYER, notification);
                }
            } else {
                remoteViews.setImageViewResource(R.id.iv_music_img, context.getResources().getIdentifier("ic_llmp_2", "mipmap", context.getPackageName()));
                remoteBigViews.setImageViewResource(R.id.iv_music_img, context.getResources().getIdentifier("ic_llmp_2", "mipmap", context.getPackageName()));
                manager.notify(LL_MUSIC_PLAYER, notification);
            }
        }*/

        return notification;
    }


}
