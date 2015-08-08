package com.jingcai.apps.aizhuan.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.jingcai.apps.aizhuan.persistence.UserSubject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 从相册或拍照获取一张图片，然后截取
 *
 * Created by Json Ding on 2015/8/7.
 */
public class CaptureCropUtil {

    public static final String CROP_IMAGE_PATH = "/dalegexue/image/crop/";
    public static final String CAPTURE_IMAGE_PATH = "/dalegexue/image/capture/";

    public static final int REQUEST_CODE_CAMERA = 0xa00;
    public static final int REQUEST_CODE_IMAGE = 0xa01;
    public static final int REQUEST_CODE_RESIZE = 0xa02;

    private Activity mActivity;

    public CaptureCropUtil(Activity activity){
        mActivity = activity;
    }

    public void openCamera(){

            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageUri());
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            mActivity.startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);

    }

    public void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        mActivity.startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void resizeImage(Uri uri,int aspectX,int aspectY,int outputX,int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCropImageUri());
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); //关闭人脸检测
        mActivity.startActivityForResult(intent, REQUEST_CODE_RESIZE);
    }

    public Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            final InputStream is = mActivity.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 使用前提：用户已登陆
     * 获取照片存放的uri
     *
     * @return
     */
    public static Uri getCaptureImageUri() {
        final File path = new File(Environment.getExternalStorageDirectory()+CAPTURE_IMAGE_PATH);
        if(!path.exists()){
            path.mkdirs();
        }
        return Uri.fromFile(new File(path.getAbsolutePath(),"CAPTURE_"+ UserSubject.getStudentid()).getAbsoluteFile());
    }

    public static Uri getCropImageUri() {
        final File path = new File(Environment.getExternalStorageDirectory()+CROP_IMAGE_PATH);
        if(!path.exists()){
            path.mkdirs();
        }
        return Uri.fromFile(new File(path.getAbsolutePath(),"CROP_"+UserSubject.getStudentid()).getAbsoluteFile());
    }

}
