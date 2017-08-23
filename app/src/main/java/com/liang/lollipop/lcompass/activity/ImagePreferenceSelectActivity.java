package com.liang.lollipop.lcompass.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.util.OtherUtil;
import com.liang.lollipop.lcompass.util.Settings;
import com.liang.lollipop.lcrop.LCropUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by LiuJ on 2017/08/22.
 * 选择图片的中继页面
 */
public class ImagePreferenceSelectActivity extends BaseActivity {

    private static final int REQUEST_GET_PHOTO = 789;
    private static final int REQUEST_CROP_PHOTO = 788;

    public static final String ARG_KEY = "ARG_KEY";
    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_SAVE_PATH = "ARG_SAVE_PATH";
    public static final String ARG_CROP_PHOTO = "ARG_CROP_PHOTO";
    public static final String ARG_CIRCLE = "ARG_CIRCLE";
    public static final String ARG_ASPECT_RATIO_WIDTH = "ARG_ASPECT_RATIO_WIDTH";
    public static final String ARG_ASPECT_RATIO_HEIGHT = "ARG_ASPECT_RATIO_HEIGHT";
    public static final String ARG_MAX_WIDTH = "ARG_MAX_WIDTH";
    public static final String ARG_MAX_HEIGHT = "ARG_MAX_HEIGHT";

    private float aspectRatioWidth = 1;
    private float aspectRatioHeight = 1;
    private String key;
    private String savePath;
    private String title;
    private int maxWidth = 100;
    private int maxHeight = 100;
    private boolean cropPhoto = false;
    private boolean isCircle = false;
//    private String imagePath = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        Intent intent = getIntent();
        aspectRatioWidth = intent.getFloatExtra(ARG_ASPECT_RATIO_WIDTH,1);
        aspectRatioHeight = intent.getFloatExtra(ARG_ASPECT_RATIO_HEIGHT,1);
        key = intent.getStringExtra(ARG_KEY);
        savePath = intent.getStringExtra(ARG_SAVE_PATH);
        title = intent.getStringExtra(ARG_TITLE);
        cropPhoto = intent.getBooleanExtra(ARG_CROP_PHOTO,false);
        isCircle = intent.getBooleanExtra(ARG_CIRCLE,false);
        maxWidth = intent.getIntExtra(ARG_MAX_WIDTH,512);
        maxHeight = intent.getIntExtra(ARG_MAX_HEIGHT,512);
        if(maxWidth<100)
            maxWidth = 100;
        if(maxHeight<100)
            maxHeight = 100;
        getPhoto();
    }

    private void getPhoto(){
        LCropUtil.selectPhotos(this,REQUEST_GET_PHOTO,title);
    }

    private void onPhotoResult(int resultCode,Intent data){
        if(resultCode!= LCropUtil.RESULT_OK){
            //如果并不是选中的结果，那么就销毁自身，结束本次任务
            finishSelf();
            return ;
        }
        ArrayList<String> imgList = LCropUtil.getResultPath(data);
        if(imgList==null||imgList.size()<1){
            //如果返回结果是空，那么也直接销毁页面
            finishSelf();
            return ;
        }
        String imagePath = "";
        for(String path : imgList){
            if(!TextUtils.isEmpty(path)){
                imagePath = path;
                break;
            }
        }
        if(cropPhoto&&!TextUtils.isEmpty(imagePath)){
            UCrop.of(Uri.fromFile(new File(imagePath)), Uri.fromFile(OtherUtil.getTempImgFile(this)))
                    .withAspectRatio(aspectRatioWidth,aspectRatioHeight)
                    .withOptions(getUCropOption(title,isCircle))
                    .withMaxResultSize(maxWidth,maxHeight)
                    .start(this,REQUEST_CROP_PHOTO);
        }else{
            saveImage(imagePath);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_GET_PHOTO:
                onPhotoResult(resultCode,data);
                break;
            case REQUEST_CROP_PHOTO:
                onPhotoCropResult(resultCode,data);
                break;
        }
    }

    private void onPhotoCropResult(int resultCode,Intent data){
        if(UCrop.RESULT_ERROR==resultCode){
            final Throwable cropError = UCrop.getError(data);
            Log.d("onPhotoCropResult","图片剪裁出错了："+cropError.toString());
            finishSelf();
            return;
        }
        if(RESULT_OK == resultCode){
            saveImage(OtherUtil.getTempImgPath(this));
        }else{
            finishSelf();
        }
    }

    private UCrop.Options getUCropOption(String title,boolean isCircle){
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        options.setToolbarTitle(title);
        options.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        options.setCircleDimmedLayer(isCircle);
        options.setActiveWidgetColor(ContextCompat.getColor(this,R.color.colorPrimary));
        return options;
    }

    private void finishSelf(){
        finish();
    }

    private void saveImage(String path){
        if(TextUtils.isEmpty(savePath)){
            String name = key+".png";
            //如果没有指定保存地址，那么就以key的名字来保存这个图片
            OtherUtil.saveImage(this,name,path);
            Settings.saveString(this,key,OtherUtil.getImagePath(this,name));
        }else{
            //如果有指定保存地址，那么就保存到指定地址，然后再刷新保存信息
            File file = new File(savePath);
            OtherUtil.saveImage(this,file.getName(),file.getParent());
            Settings.saveString(this,key,savePath);
        }
        finishSelf();
    }

}
