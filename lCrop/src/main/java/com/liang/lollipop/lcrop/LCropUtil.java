package com.liang.lollipop.lcrop;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.liang.lollipop.lcrop.activity.SelectImagesActivity;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Lollipop on 2017/08/02.
 * 图片处理工具集合的对外接口封装
 */
public class LCropUtil {

    public static final String RESULT_DATA = SelectImagesActivity.RESULT_DATA;
    public static final int RESULT_OK = SelectImagesActivity.RESULT_OK;

    public static ArrayList<String> getResultPath(Intent data){
        if(data==null)
            return null;
       return data.getStringArrayListExtra(SelectImagesActivity.RESULT_DATA);
    }

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

//    public static void cropPhoto(Activity activity,int requestCode,String uri,int aspectX,int aspectY,int outputX,int outputY,String outputUri){
//        activity.startActivityForResult(createCropIntent(activity,uri,aspectX,aspectY,outputX,outputY,outputUri), requestCode);
//    }
//
//    public static void cropPhoto(Activity activity,int requestCode,String uri,int aspectX,int aspectY,String outputUri){
//        activity.startActivityForResult(createCropIntent(activity,uri,aspectX,aspectY,outputUri), requestCode);
////        Intent intent = new Intent(activity, CropActivity.class);
////        intent.putExtra(CropActivity.ARG_IMAGE_URL,uri);
////        activity.startActivity(intent);
//    }
//
//    public static void cropPhoto(Fragment fragment,int requestCode,String uri,int aspectX,int aspectY,int outputX,int outputY,String outputUri){
//        fragment.startActivityForResult(createCropIntent(fragment.getActivity(),uri,aspectX,aspectY,outputX,outputY,outputUri), requestCode);
//    }
//
//    public static void cropPhoto(Fragment fragment,int requestCode,String uri,int aspectX,int aspectY,String outputUri){
//        fragment.startActivityForResult(createCropIntent(fragment.getActivity(),uri,aspectX,aspectY,outputUri), requestCode);
//    }
//
//    public static void cropPhoto(android.support.v4.app.Fragment fragment, int requestCode, String uri, int aspectX, int aspectY, int outputX, int outputY, String outputUri){
//        fragment.startActivityForResult(createCropIntent(fragment.getContext(),uri,aspectX,aspectY,outputX,outputY,outputUri), requestCode);
//    }
//
//    public static void cropPhoto(android.support.v4.app.Fragment fragment,int requestCode,String uri,int aspectX,int aspectY,String outputUri){
//        fragment.startActivityForResult(createCropIntent(fragment.getContext(),uri,aspectX,aspectY,outputUri), requestCode);
//    }

    public static UCrop uCrop(String srcPath, String outputPath){
        return uCrop(new File(srcPath),new File(outputPath));
    }

    public static UCrop uCrop(File src, File output){
        return UCrop.of(fromFile(src),fromFile(output));
    }

//    private static Intent createCropIntent(Context context,String uri,int aspectX,int aspectY,int outputX,int outputY,String outputUri){
//        Intent intent = createCropIntent(context,uri,aspectX,aspectY,outputUri);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);
//        return intent;
//    }
//
//    private static Intent createCropIntent(Context context,String path, int aspectX, int aspectY, String outputUri){
//        Intent intent = new Intent("com.android.camera.action.CROP");
//
//        intent.setDataAndType(getUri(context,path), "image/*");
//        // 设置裁剪
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectY);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("return-data", TextUtils.isEmpty(outputUri));
//
//        if(!TextUtils.isEmpty(outputUri)){
//            intent.putExtra(MediaStore.EXTRA_OUTPUT,getUri(context,outputUri));
//        }
//        return intent;
//    }

    public static Uri fromPath(String path){
        return fromFile(new File(path));
    }

    public static Uri fromFile(File path){
        return Uri.fromFile(path);
    }

//    public static Uri getUri(Context context,String path){
//        if(Build.VERSION.SDK_INT>=24){
//            //安卓N以后，文件管理高度私有化，
//            // 如果跨应用传递地址，需要使用ContentProvider或FileProvider
//            ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA, path);
//            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//        }else{
//            return Uri.parse(path);
//        }
//    }

}
