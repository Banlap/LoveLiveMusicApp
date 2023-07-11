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
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.banlap.llmusic.R;
import com.banlap.llmusic.service.MusicIsPauseService;
import com.banlap.llmusic.service.MusicLastService;
import com.banlap.llmusic.service.MusicNextService;
import com.banlap.llmusic.ui.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

/**
 * 通知栏帮助类 展示LLMusic通知
 * */
public class NotificationHelper {
    public static final int LL_MUSIC_PLAYER = 0x01;
    public static final int LL_MUSIC_CHARACTER= 0x02;
    public Notification notification;
    public NotificationCompat.Builder builder;
    public NotificationManager manager;
    public static NotificationHelper getInstance() { return new NotificationHelper();}


    @SuppressLint("RemoteViewLayout")
    public void createRemoteViews(Context context, String musicName, String musicSinger, Bitmap bitmap, boolean isDefault) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_remote_notification_bar);
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
        PendingIntent pIntentIsPause = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceIsPause, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_play, pIntentIsPause);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_play, pIntentIsPause);

        Intent intentServiceNext = new Intent(context, MusicNextService.class);
        intentServiceNext.putExtra("NextMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentNext = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceNext, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_next, pIntentNext);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_next, pIntentNext);

        Intent intentServiceLast = new Intent(context, MusicLastService.class);
        intentServiceLast.putExtra("LastMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentLast = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceLast, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_last, pIntentLast);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_last, pIntentLast);

        Intent intentMain = new Intent(context, MainActivity.class);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        //FLAG_ACTIVITY_RESET_TASK_IF_NEEDED 按需启动的关键,如果任务队列中已经存在,则重建程序
        intentMain.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentMain = PendingIntent.getActivity(context, LL_MUSIC_PLAYER, intentMain, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_music_all, pIntentMain);
        remoteBigViews.setOnClickPendingIntent(R.id.ll_music_all, pIntentMain);

        NotificationHelper.getInstance().createNotification(context, null, remoteViews, remoteBigViews);

    }

    /** 创建通知栏Notification */
    public void createNotification(Context context, PendingIntent pIntent, RemoteViews view, RemoteViews bigView) {

        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_1", "LLMusic_channel", NotificationManager.IMPORTANCE_LOW);
            //channel.enableLights(true);
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(context, "channel_1");
            builder.setCustomBigContentView(bigView);
            builder.setCustomContentView(view);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            //builder.setContentIntent(pIntent);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.ic_llmp_small_1);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_llmp));
            notification = builder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            manager.notify(LL_MUSIC_PLAYER, notification);
        } else {
            builder = new NotificationCompat.Builder(context);
            builder.setCustomBigContentView(bigView);
            builder.setCustomContentView(view);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            //builder.setContentIntent(pIntent);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.ic_llmp_small_1);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_llmp));
            notification = builder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            manager.notify(LL_MUSIC_PLAYER, notification);
        }
    }

    @SuppressLint("RemoteViewLayout")
    public Notification createNotificationReturn(Context context, String musicName, String musicSinger, Bitmap bitmap, boolean isDefault) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_remote_notification_bar);
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bitmapByte = baos.toByteArray();
            intentServiceIsPause.putExtra("MusicBitmap", bitmapByte);
        } else {
            remoteViews.setImageViewResource(R.id.iv_music_img, context.getResources().getIdentifier("ic_llmp_2", "mipmap", context.getPackageName()));
            remoteBigViews.setImageViewResource(R.id.iv_music_img, context.getResources().getIdentifier("ic_llmp_2", "mipmap", context.getPackageName()));
            intentServiceIsPause.putExtra("MusicBitmap", (byte[]) null);
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentIsPause = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceIsPause, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_play, pIntentIsPause);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_play, pIntentIsPause);

        Intent intentServiceNext = new Intent(context, MusicNextService.class);
        intentServiceNext.putExtra("NextMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentNext = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceNext, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_next, pIntentNext);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_next, pIntentNext);

        Intent intentServiceLast = new Intent(context, MusicLastService.class);
        intentServiceLast.putExtra("LastMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentLast = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceLast, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_last, pIntentLast);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_last, pIntentLast);

        Intent intentMain = new Intent(context, MainActivity.class);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        //FLAG_ACTIVITY_RESET_TASK_IF_NEEDED 按需启动的关键,如果任务队列中已经存在,则重建程序
        intentMain.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentMain = PendingIntent.getActivity(context, LL_MUSIC_PLAYER, intentMain, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_music_all, pIntentMain);
        remoteBigViews.setOnClickPendingIntent(R.id.ll_music_all, pIntentMain);

        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_1", "LLMusic_channel", NotificationManager.IMPORTANCE_LOW);
            //channel.enableLights(true);
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(context, "channel_1");
            builder.setCustomBigContentView(remoteBigViews);
            builder.setCustomContentView(remoteViews);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            //builder.setContentIntent(pIntent);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.ic_llmp_small_1);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_llmp));
            notification = builder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            manager.notify(LL_MUSIC_PLAYER, notification);
        } else {
            builder = new NotificationCompat.Builder(context);
            builder.setCustomBigContentView(remoteBigViews);
            builder.setCustomContentView(remoteViews);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            //builder.setContentIntent(pIntent);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.ic_llmp_small_1);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_llmp));
            notification = builder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            manager.notify(LL_MUSIC_PLAYER, notification);
        }
        return notification;
    }

}
