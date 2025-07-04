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
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.banlap.llmusic.R;
import com.banlap.llmusic.service.MusicPlayService;
import com.banlap.llmusic.ui.activity.LockFullScreenActivity;
import com.banlap.llmusic.ui.activity.MainActivity;

import java.io.ByteArrayOutputStream;

/**
 * 通知栏帮助类 展示LLMusic通知
 * */
public class NotificationHelper {
    private static final String TAG = NotificationHelper.class.getSimpleName();
    public static final int LL_MUSIC_PLAYER = 0x01; //播放通知
    public static final int LL_MUSIC_CHARACTER= 0x02;  //角色通知
    public static final int LL_MUSIC_FULL_SCREEN= 0x03;  //锁屏通知
    public static final int LL_MUSIC_FLOATING_LYRIC= 0x04;  //浮动歌词通知
    public Notification notification;
    public static NotificationCompat.Builder builder;
    public static NotificationManager manager;

    public Bitmap roundedBitmap;
    public static NotificationHelper getInstance() { return new NotificationHelper(); }


    /**
     * 判断是否需要打开设置界面
     */
    public boolean isOpenNotificationSetting(Context context) {
        if (!isNotificationEnabled(context)) {
            return false;
        } else {
            return true; //有通知权限
        }
    }

    /**
     * 判断该app是否打开了通知
     * 注：可以通过NotificationManagerCompat 中的 areNotificationsEnabled()来判断是否开启通知权限。
     * NotificationManagerCompat 在 android.support.v4.app包中，是API 22.1.0 中加入的。
     * 而 areNotificationsEnabled()则是在 API 24.1.0之后加入的。
     * areNotificationsEnabled 只对 API 19 及以上版本有效，低于API 19 会一直返回true
     * */
    public boolean isNotificationEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 打开通知权限
     *
     * @param context
     */
    public void openNotificationSettingsForApp(Context context) {
        // Links to this app's notification settings.
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", context.getPackageName());
        intent.putExtra("app_uid", context.getApplicationInfo().uid);
        // for Android 8 and above
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        context.startActivity(intent);
    }

    /**
     * 创建音乐通知，使用MediaStyle方式 （低版本通知）
     * */
    @SuppressLint("RemoteViewLayout")
    public Notification createRemoteViews(Context context, String musicName, String musicSinger, Bitmap bitmap, boolean isPause) {

        //播放与暂停
        Intent intentServiceIsPause = new Intent(context, MusicPlayService.class);
        intentServiceIsPause.setAction(MusicPlayService.INTENT_ACTION_PLAY);
        intentServiceIsPause.putExtra("IsPauseMusic", true);
        intentServiceIsPause.putExtra("MusicName", musicName);
        intentServiceIsPause.putExtra("MusicSinger", musicSinger);
        if(bitmap !=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  //压缩图片50% 防止显示不到图片
            byte[] bitmapByte = baos.toByteArray();
            intentServiceIsPause.putExtra("MusicBitmap", bitmapByte);
        } else {
            intentServiceIsPause.putExtra("MusicBitmap", (byte[]) null);
        }
        @SuppressLint("UnspecifiedImmutableFlag")
        //使用PendingIntent时 版本大于31时需要添加 FLAG_IMMUTABLE 或者 FLAG_MUTABLE
        PendingIntent pIntentIsPause = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceIsPause,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //下一首
        Intent intentServiceNext = new Intent(context, MusicPlayService.class);
        intentServiceNext.setAction(MusicPlayService.INTENT_ACTION_PLAY_NEXT);
        intentServiceNext.putExtra("NextMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentNext = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceNext,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //上一首
        Intent intentServiceLast = new Intent(context, MusicPlayService.class);
        intentServiceLast.setAction(MusicPlayService.INTENT_ACTION_PLAY_LAST);
        intentServiceLast.putExtra("LastMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentLast = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceLast,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //进入主程序
        Intent intentMain = new Intent(context, MainActivity.class);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        //FLAG_ACTIVITY_RESET_TASK_IF_NEEDED 按需启动的关键,如果任务队列中已经存在,则重建程序
        intentMain.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentMain = PendingIntent.getActivity(context, LL_MUSIC_PLAYER, intentMain,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);

        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(context.getPackageName(), "LLMusic_channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setShowBadge(false);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, context.getPackageName());
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setChannelId(context.getPackageName());
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setCategory(Notification.CATEGORY_TRANSPORT);
        builder.setDefaults(0); // 禁用所有默认的提示行为
        builder.setSilent(true); // 设置静默通知
        builder.setShowWhen(false);
        builder.setSmallIcon(R.mipmap.ic_llmp_small_1);
        builder.setContentTitle(musicName);
        builder.setContentText(musicSinger);
        builder.setOngoing(false); //传统通知时，控制播放时是否能划走通知

        if(bitmap !=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  //压缩图片50% 防止显示不到图片
            builder.setLargeIcon(bitmap);
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_llmp_new_2));
        }

        MediaSessionCompat mediaSession = MusicPlayService.mMediaSession;
        if(mediaSession == null) {
            Log.e(TAG, "mediaSession is null, so create new one");
            mediaSession = new MediaSessionCompat(context, context.getPackageName());
        }
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2) //对应显示通知栏的按钮
        );

        builder.addAction(new NotificationCompat.Action(
                R.mipmap.ic_last_black, "Previous", pIntentLast));
        builder.addAction(new NotificationCompat.Action(
                isPause? R.mipmap.ic_play_black : R.mipmap.ic_pause_black, "IsPause", pIntentIsPause));
        builder.addAction(new NotificationCompat.Action(
                R.mipmap.ic_next_black, "Next", pIntentNext));

        builder.setContentIntent(pIntentMain);

        notification = builder.build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        manager.notify(LL_MUSIC_PLAYER, notification);
        NotificationManagerCompat.from(context).notify(LL_MUSIC_PLAYER, notification);

        return notification;
    }


    /**
     * 旧版本通知栏
     * */
    @Deprecated
    @SuppressLint("RemoteViewLayout")
    public Notification createRemoteViewsOld(Context context, String musicName, String musicSinger, Bitmap bitmap, boolean isDefault) {
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

        Intent intentServiceIsPause = new Intent(context, MusicPlayService.class);
        intentServiceIsPause.setAction(MusicPlayService.INTENT_ACTION_PLAY);
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
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_play, pIntentIsPause);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_play, pIntentIsPause);

        Intent intentServiceNext = new Intent(context, MusicPlayService.class);
        intentServiceNext.setAction(MusicPlayService.INTENT_ACTION_PLAY_NEXT);
        intentServiceNext.putExtra("NextMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentNext = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceNext,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_next, pIntentNext);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_next, pIntentNext);

        Intent intentServiceLast = new Intent(context, MusicPlayService.class);
        intentServiceLast.setAction(MusicPlayService.INTENT_ACTION_PLAY_LAST);
        intentServiceLast.putExtra("LastMusic", true);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentLast = PendingIntent.getService(context, LL_MUSIC_PLAYER, intentServiceLast,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_last, pIntentLast);
        remoteBigViews.setOnClickPendingIntent(R.id.bt_last, pIntentLast);

        Intent intentMain = new Intent(context, MainActivity.class);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        //FLAG_ACTIVITY_RESET_TASK_IF_NEEDED 按需启动的关键,如果任务队列中已经存在,则重建程序
        intentMain.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pIntentMain = PendingIntent.getActivity(context, LL_MUSIC_PLAYER, intentMain,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                        PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_music_all, pIntentMain);
        remoteBigViews.setOnClickPendingIntent(R.id.ll_music_all, pIntentMain);

        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(context.getPackageName(), "LLMusic_channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, context.getPackageName());
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setCategory(Notification.CATEGORY_CALL);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_llmp_small_1);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_llmp_new));
        builder.setCustomBigContentView(remoteBigViews);
        builder.setCustomContentView(remoteViews);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle());
        notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        manager.notify(LL_MUSIC_PLAYER, notification);

        return notification;
    }

    /**
     * 创建全屏通知
     * */
    public Notification createFullScreen(Context context) {
        Intent fullScreenIntent = new Intent(context, LockFullScreenActivity.class);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 111, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_3", "LLMusic_FullScreen_channel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, "channel_3");
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setSmallIcon(R.mipmap.ic_llmp_small_1);
        builder.setSound(null);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setOngoing(true);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setAutoCancel(true);

        builder.setFullScreenIntent(pendingIntent, true);
        Notification notification = builder.build();
        manager.notify(LL_MUSIC_FULL_SCREEN, notification);
        return notification;
    }

    /**
     * 清除指定通知
     * @param id 通知对应的id
     * */
    public void cancelNotification(Context context, int id) {
        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(id);
    }

}
