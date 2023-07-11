package com.banlap.llmusic.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.banlap.llmusic.R;
import com.banlap.llmusic.utils.FileUtil;
import com.banlap.llmusic.utils.PermissionUtil;
import com.banlap.llmusic.utils.SelectImgHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 裁剪图片页面
 * */
public class SelectImgActivity extends AppCompatActivity {

    private final String TAG = SelectImgActivity.class.getSimpleName();
    private static SelectImgHelper.SelectImgListener mListener;

    private String mUriStr="";
    private ActivityResultLauncher<Intent> intentTakePhotoLauncher;   //选择图片
    private ActivityResultLauncher<Intent> intentCroppingPhotoLauncher;   //裁切图片
    private Uri destinationUri;

    public static void start(Context context, SelectImgHelper.SelectImgListener listener) {
        mListener = listener;
        Intent intent = new Intent(context, SelectImgActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));
        initCheck();
    }

    /**
     * 检查所需权限
     * */
    private void initCheck() {
        String[] permission = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if(PermissionUtil.getInstance().checkPermission(this, permission)) {
            mListener.onNeedPermission(this, permission, mListener);
            finish();
            return;
        }
        initActivityResultListener();
        initSelectImg();
    }

    /**
     * 初始化系统回调
     * */
    private void initActivityResultListener() {
        intentTakePhotoLauncher = registerForActivityResult(  //选择图片回调
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Intent intent = result.getData();
                            Uri uri = intent.getData();
                            if(uri != null) {
                                //startCroppingImg(uri);
                                startCroppingImgNew(uri);
                                return;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "e: " + e.getMessage());
                        }
                        finish();
                    }
                });

        intentCroppingPhotoLauncher = registerForActivityResult(  //裁剪图片回调
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(mUriStr)));
                            if(bitmap != null) {
                                boolean b = FileUtil.getInstance().deleteFile(getApplication(), SelectImgHelper.DEFAULT_URL);
                                mListener.onSuccess(bitmap);
                            } else {
                                Log.e(TAG, "e: " + "uri = null");
                                mListener.onError();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "e: " + "uri = null");
                            mListener.onError();
                        }
                        finish();
                    }
                });
    }

    /**
     * 选择图片
     * */
    private void initSelectImg() {
        Intent intentPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mUriStr =  SelectImgHelper.DEFAULT_URL;
        intentTakePhotoLauncher.launch(intentPhoto);
    }

    /**
     * 系统图片裁剪 （最新版本android系统调用出现问题）
     * */
    @Deprecated
    private void startCroppingImg(Uri uri) {
        // 调用系统中自带的图片剪裁
        Intent intentCropping = new Intent("com.android.camera.action.CROP");

        intentCropping.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intentCropping.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intentCropping.putExtra("aspectX", 1);
        intentCropping.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intentCropping.putExtra("outputX", 150);
        intentCropping.putExtra("outputY", 150);
        intentCropping.putExtra("scale", true);
        intentCropping.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentCropping.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
        intentCropping.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intentCropping.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intentCropping.putExtra("return-data", false);
        Uri imageUri = Uri.parse(mUriStr);
        intentCropping.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intentCropping.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        intentCroppingPhotoLauncher.launch(intentCropping);


    }

    /**
     * 使用第三方图片裁剪（适用于安卓最新版本）
     * */
    private void startCroppingImgNew(Uri uri) {
        UCrop.Options options = new UCrop.Options();
        // 修改标题栏颜色
        options.setToolbarColor(getResources().getColor(R.color.light_ea));
        // 修改状态栏颜色
        options.setStatusBarColor(getResources().getColor(R.color.light_ff));
        // 隐藏底部工具
        options.setHideBottomControls(true);
        // 图片格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        // 设置图片压缩质量
        options.setCompressionQuality(100);
        // 是否让用户调整范围(默认false)，如果开启，可能会造成剪切的图片的长宽比不是设定的
        // 如果不开启，用户不能拖动选框，只能缩放图片
        options.setFreeStyleCropEnabled(false);
        // 设置图片压缩质量
        options.setCompressionQuality(100);
        // 圆
        options.setCircleDimmedLayer(false);
        // 不显示网格线
        options.setShowCropGrid(false);

        //目标图片Uri
        File outputFile = new File(getCacheDir(), "tmp.jpg");
        destinationUri = Uri.fromFile(outputFile);
        Uri imageUri = Uri.parse(mUriStr);

        // 设置源uri及目标uri
        UCrop.of(uri, destinationUri)
                // 长宽比
                .withAspectRatio(1, 1)
                // 图片大小
                .withMaxResultSize(400, 400)
                // 配置参数
                .withOptions(options)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UCrop.RESULT_ERROR) {
            if(destinationUri != null) {
                FileUtil.getInstance().deleteFile(getApplication(), destinationUri.getPath());
                destinationUri = null;
            }
            finish();
            return;
        }

        if (resultCode == RESULT_OK) { //裁剪后处理
            if(requestCode == UCrop.REQUEST_CROP) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(destinationUri));
                    if(bitmap != null) {
                        if(destinationUri != null) {
                            FileUtil.getInstance().deleteFile(getApplication(), destinationUri.getPath());
                            destinationUri = null;
                        }
                        mListener.onSuccess(bitmap);
                    } else {
                        Log.e(TAG, "e: " + "uri = null");
                        mListener.onError();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "e: " + "uri = null");
                    mListener.onError();
                }
                finish();
            }
        } else if (resultCode == RESULT_CANCELED) { //裁剪时取消
            if(requestCode == UCrop.REQUEST_CROP) {
                if(destinationUri != null) {
                    FileUtil.getInstance().deleteFile(getApplication(), destinationUri.getPath());
                    destinationUri = null;
                }
                //FileUtil.getInstance().deleteFile(getApplication(), SelectImgHelper.DEFAULT_URL);
                finish();
            }
        }
    }
}
