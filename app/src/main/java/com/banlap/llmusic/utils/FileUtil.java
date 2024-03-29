package com.banlap.llmusic.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件工具类
 * */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static FileUtil getInstance() { return new FileUtil();}

    public static String ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.i(TAG, "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.i(TAG, "The File doesn't not exist.");
            } catch (IOException e) {
                Log.i(TAG, "e: " +e.getMessage());
            }
        }
        return content;
    }


    /**
     * 获取文件路径
     * */
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                if(id.contains(":")) {
                    final String[] split = id.split(":");
                    String path = split[1];
                    //部分机型获取格式为msf:XXX(数字) 则需要额外判断并使用另外的获取文件路径方式
                    if(isNumeric(path)) {
                        Uri contentUri = MediaStore.Files.getContentUri("external");
                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{split[1]};
                        return getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                    return path;
                } else {
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }

            } else if (isMediaDocument(uri)) { // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) { // File
            return uri.getPath();
        }
        return null;
    }

    /**
     * 删除文件
     * */
    public boolean deleteFile(Context context, String url) {
        boolean b = false, isExists = false;
        if(!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            File file = new File(getRealPathByUri(context, uri));
            isExists = file.exists();
            if(isExists) {
                b = file.delete();
            }
        }
        Log.i(TAG, "file is exists: " + isExists + " file is delete: " + b + " url: " + url);
        return b;
    }

    /**
     * 获取文件真实路径
     * */
    @SuppressLint("Range")
    public String getRealPathByUri(Context context, Uri uri){
        if(null == uri)
            return "";
        String [] ps = {MediaStore.Images.Media.DATA};
        Cursor pathCursor = new CursorLoader(context, uri, ps,null,null,null).loadInBackground();
        if(null == pathCursor )
            return uri.getPath();
        pathCursor.moveToFirst();
        return pathCursor.getString(pathCursor.getColumnIndex(MediaStore.Images.Media.DATA));
    }

    /**
     * 文件复制 TODO未完成
     * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void copyFile(Context context, File fromFile, File toFile) {
        try {

            if (fromFile.isDirectory()) {
                if (!toFile.exists()) {
                    toFile.mkdirs();
                }

                File[] files = fromFile.listFiles();
                if (files != null) {
                    for (File file : files) {
                        copyFile(context, file, toFile);
                    }
                }
            } else {
                if(fromFile.exists()) {
                    Files.copy(fromFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存异常错误日志到本地中
     * */
    public void saveCrashLogToFile(Throwable e) {
        StringBuilder sb = new StringBuilder();

        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        // 写入异常发生时间
        sb.append("Crash Time: ").append(currentTime).append("\n\n");

        // 写入异常信息
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        sb.append(stackTrace);

        // 将日志写入到本地文件
        try {
            String path="";
            //Android 10以上使用 getExternalFilesDir(null)获取路径
            path = LLActivityManager.getInstance().getTopActivity().getExternalFilesDir(null).getAbsolutePath() + "/LLMusicLog/crash.log";

            File logFile = new File(path);
            if(!logFile.exists()) {
                logFile.getParentFile().mkdirs();
            }

            FileWriter writer = new FileWriter(logFile);
            writer.write(sb.toString());
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    public File createImageFile(Context context) {
        try {
            // 创建一个临时文件，用于保存拍照的照片
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            return image;
        } catch (Exception e) {
            Log.i(TAG, "e: " + e.getMessage());
            return null;
        }
    }

    /**
     * 当前字符串是否为数字
     * */
    public static boolean isNumeric(String str){
        for(int i=str.length();--i>=0;){
            int chr=str.charAt(i);
            if(chr<48 || chr>57) {
                return false;
            }
        }
        return true;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
