package com.banlap.llmusic.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Bitmap图片处理工具类
 * */
public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();
    private static final int IMG_BITMAP_LIMIT_SIZE = 900000;      //显示图片的最大限值 （超过该值则需要压缩）

    public static BitmapUtil getInstance() { return new BitmapUtil(); }

    /**
     * 展示图片，并处理图片过大时压缩
     * */
    public Bitmap showBitmap(byte[] inputStream2ByteArr) {
        try {
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
            Log.i(TAG, "bitmap: " + getBitmapSize(bitmap));
            Log.i(TAG, "bitmapNew: " + getBitmapSize(bitmapNew));
            if (getBitmapSize(bitmap) >= IMG_BITMAP_LIMIT_SIZE) {
                Log.i(TAG, "bitmap: use bitmapNew");
                return bitmapNew;
            } else {
                Log.i(TAG, "bitmap: use bitmap");
                return bitmap;
            }
        } catch (Exception e) {
            Log.i("ABMediaPlay", "error " + e.getMessage());
            return null;
        }
    }

    /**
     * bitmap转换字节数组：默认PNG 压缩100
     * */
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        return bitmapToByteArray(bitmap, Bitmap.CompressFormat.PNG, 100);
    }

    /**
     * bitmap转换字节数组（指定格式和压缩质量）
     * */
    public byte[] bitmapToByteArray(Bitmap bitmap, Bitmap.CompressFormat type, int quality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 参数说明：格式（JPEG/PNG/WEBP）、压缩质量（0-100）、输出流
        bitmap.compress(type, quality, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * bitmap转换字节数组（原始像素数据）
     * */
    public byte[] bitmapToRawByteArray(Bitmap bitmap) {
        int size = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(buffer);
        return buffer.array();
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
    public byte[] inputStream2ByteArr(InputStream inputStream) throws IOException {
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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
