package com.liang.lollipop.lcrop;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;

import com.liang.lollipop.lcrop.activity.SelectImagesActivity;

/**
 * Created by Lollipop on 2017/08/02.
 * 图片处理工具集合的对外接口封装
 */
public class LCropUtil {

    public static void selectPhotos(Activity activity,int requestCode,String title,boolean showCamera,int maxSize,int minSize,int colSize){
        Intent intent = new Intent(activity, SelectImagesActivity.class);
        intent.putExtra(SelectImagesActivity.ARG_TITLE,title);
        intent.putExtra(SelectImagesActivity.ARG_SHOW_CAMERA,showCamera);
        intent.putExtra(SelectImagesActivity.ARG_MAX_SIZE,maxSize);
        intent.putExtra(SelectImagesActivity.ARG_MIN_SIZE,minSize);
        intent.putExtra(SelectImagesActivity.ARG_COL_SIZE,colSize);
        activity.startActivityForResult(intent,requestCode);
    }

    public static void selectPhotos(Activity activity,int requestCode,String title,int maxSize,int minSize,int colSize){
        selectPhotos(activity,requestCode,title,true,maxSize,minSize,colSize);
    }

    public static void selectPhotos(Activity activity,int requestCode,String title,int maxSize,int colSize){
        selectPhotos(activity,requestCode,title,maxSize,1,colSize);
    }

    public static void selectPhotos(Activity activity,int requestCode,String title,int maxSize){
        selectPhotos(activity,requestCode,title,maxSize,3);
    }

    public static void selectPhotos(Activity activity,int requestCode,String title){
        selectPhotos(activity,requestCode,title,1);
    }

    public static void selectPhotos(Activity activity,int requestCode){
        selectPhotos(activity,requestCode,null,1);
    }

    public static void selectPhotos(Fragment fragment, int requestCode, String title, boolean showCamera, int maxSize, int minSize, int colSize){
        Intent intent = new Intent(fragment.getActivity(), SelectImagesActivity.class);
        intent.putExtra(SelectImagesActivity.ARG_TITLE,title);
        intent.putExtra(SelectImagesActivity.ARG_SHOW_CAMERA,showCamera);
        intent.putExtra(SelectImagesActivity.ARG_MAX_SIZE,maxSize);
        intent.putExtra(SelectImagesActivity.ARG_MIN_SIZE,minSize);
        intent.putExtra(SelectImagesActivity.ARG_COL_SIZE,colSize);
        fragment.startActivityForResult(intent,requestCode);
    }

    public static void selectPhotos(Fragment fragment,int requestCode,String title,int maxSize,int minSize,int colSize){
        selectPhotos(fragment,requestCode,title,true,maxSize,minSize,colSize);
    }

    public static void selectPhotos(Fragment fragment,int requestCode,String title,int maxSize,int colSize){
        selectPhotos(fragment,requestCode,title,maxSize,1,colSize);
    }

    public static void selectPhotos(Fragment fragment,int requestCode,String title,int maxSize){
        selectPhotos(fragment,requestCode,title,maxSize,3);
    }

    public static void selectPhotos(Fragment fragment,int requestCode,String title){
        selectPhotos(fragment,requestCode,title,1);
    }

    public static void selectPhotos(Fragment fragment,int requestCode){
        selectPhotos(fragment,requestCode,null,1);
    }

    public static void selectPhotos(android.support.v4.app.Fragment fragment, int requestCode, String title, boolean showCamera, int maxSize, int minSize, int colSize){
        Intent intent = new Intent(fragment.getActivity(), SelectImagesActivity.class);
        intent.putExtra(SelectImagesActivity.ARG_TITLE,title);
        intent.putExtra(SelectImagesActivity.ARG_SHOW_CAMERA,showCamera);
        intent.putExtra(SelectImagesActivity.ARG_MAX_SIZE,maxSize);
        intent.putExtra(SelectImagesActivity.ARG_MIN_SIZE,minSize);
        intent.putExtra(SelectImagesActivity.ARG_COL_SIZE,colSize);
        fragment.startActivityForResult(intent,requestCode);
    }

    public static void selectPhotos(android.support.v4.app.Fragment fragment,int requestCode,String title,int maxSize,int minSize,int colSize){
        selectPhotos(fragment,requestCode,title,true,maxSize,minSize,colSize);
    }

    public static void selectPhotos(android.support.v4.app.Fragment fragment,int requestCode,String title,int maxSize,int colSize){
        selectPhotos(fragment,requestCode,title,maxSize,1,colSize);
    }

    public static void selectPhotos(android.support.v4.app.Fragment fragment,int requestCode,String title,int maxSize){
        selectPhotos(fragment,requestCode,title,maxSize,3);
    }

    public static void selectPhotos(android.support.v4.app.Fragment fragment,int requestCode,String title){
        selectPhotos(fragment,requestCode,title,1);
    }

    public static void selectPhotos(android.support.v4.app.Fragment fragment,int requestCode){
        selectPhotos(fragment,requestCode,null,1);
    }

    public static void cropPhoto(Activity activity,int requestCode,Uri uri,int aspectX,int aspectY,int outputX,int outputY,boolean returnData){
        activity.startActivityForResult(createCropIntent(uri,aspectX,aspectY,outputX,outputY,returnData), requestCode);
    }

    public static void cropPhoto(Activity activity,int requestCode,Uri uri,int aspectX,int aspectY,boolean returnData){
        activity.startActivityForResult(createCropIntent(uri,aspectX,aspectY,returnData), requestCode);
    }

    public static void cropPhoto(Fragment fragment,int requestCode,Uri uri,int aspectX,int aspectY,int outputX,int outputY,boolean returnData){
        fragment.startActivityForResult(createCropIntent(uri,aspectX,aspectY,outputX,outputY,returnData), requestCode);
    }

    public static void cropPhoto(Fragment fragment,int requestCode,Uri uri,int aspectX,int aspectY,boolean returnData){
        fragment.startActivityForResult(createCropIntent(uri,aspectX,aspectY,returnData), requestCode);
    }

    public static void cropPhoto(android.support.v4.app.Fragment fragment, int requestCode, Uri uri, int aspectX, int aspectY, int outputX, int outputY, boolean returnData){
        fragment.startActivityForResult(createCropIntent(uri,aspectX,aspectY,outputX,outputY,returnData), requestCode);
    }

    public static void cropPhoto(android.support.v4.app.Fragment fragment,int requestCode,Uri uri,int aspectX,int aspectY,boolean returnData){
        fragment.startActivityForResult(createCropIntent(uri,aspectX,aspectY,returnData), requestCode);
    }

    private static Intent createCropIntent(Uri uri,int aspectX,int aspectY,int outputX,int outputY,boolean returnData){
        Intent intent = createCropIntent(uri,aspectX,aspectY,returnData);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        return intent;
    }

    private static Intent createCropIntent(Uri uri,int aspectX,int aspectY,boolean returnData){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("return-data", returnData);
        return intent;
    }

}
