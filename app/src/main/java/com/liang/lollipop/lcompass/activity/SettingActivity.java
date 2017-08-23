package com.liang.lollipop.lcompass.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.dialog.ColorSelectDialog;
import com.liang.lollipop.lcompass.drawable.CircleBgDrawable;
import com.liang.lollipop.lcompass.drawable.DialDrawable;
import com.liang.lollipop.lcompass.drawable.PointerDrawable;
import com.liang.lollipop.lcompass.fragment.SettingsFragment;
import com.liang.lollipop.lcompass.util.OtherUtil;
import com.liang.lollipop.lcompass.util.PermissionsUtil;
import com.liang.lollipop.lcompass.util.Settings;
import com.liang.lollipop.lcompass.util.SharedPreferencesUtils;
import com.liang.lollipop.lcrop.LCropUtil;
import com.liang.lollipop.lcrop.activity.SelectImagesActivity;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Lollipop
 * 设置
 */
public class SettingActivity extends BaseActivity implements SettingsFragment.OnSharedPreferenceChangedListener{

    /*-------------------------------常量部分-开始-------------------------------*/

    private static final int REQUEST_ROOT_BG = 456;
    private static final int REQUEST_DIAL_BG = 457;
    private static final int REQUEST_POINTER_BG = 458;

    private static final int REQUEST_CROP_ROOT_BG = 466;
    private static final int REQUEST_CROP_DIAL_BG = 467;
    private static final int REQUEST_CROP_POINTER_BG = 468;

    //检查读取内存卡权限的ID
    private static final int CHECK_WRITE_SD = 789;

    /*-------------------------------常量部分-结束-------------------------------*/

    private ImageView rootBgView;
    private TextView locationText;

    private PointerDrawable pointerDrawable;
    private DialDrawable dialDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        tryGetTypeface();
    }

    private void tryGetTypeface(){
        PermissionsUtil.checkPermissions(this, CHECK_WRITE_SD, new PermissionsUtil.OnPermissionsPass() {
            @Override
            public void onPermissionsPass() {
                getTypeface();
            }
        },PermissionsUtil.WRITE_EXTERNAL_STORAGE);
    }

    private void getTypeface(){
        try {
            File dir = new File(OtherUtil.getSDFontPath());
            if(!dir.exists()){
                dir.mkdirs();
                return;
            }
            File ttfFile = new File(dir,"fonts.ttf");
            if(ttfFile.exists()){
                Typeface typeFace = Typeface.createFromFile(ttfFile);
                if(typeFace!=null)
                    dialDrawable.setTypeface(typeFace);
                return;
            }

            File otfFile = new File(dir,"fonts.otf");
            if(otfFile.exists()){
                Typeface typeFace = Typeface.createFromFile(otfFile);
                if(typeFace!=null)
                    dialDrawable.setTypeface(typeFace);
            }
        }catch (Exception e){
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CHECK_WRITE_SD:
                if(PermissionsUtil.checkPermission(this,PermissionsUtil.WRITE_EXTERNAL_STORAGE)){
                    getTypeface();
                }else{
                    PermissionsUtil.popPermissionsDialog(this,
                            getString(R.string.no_write_sd_with_fonts));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_setting_toolbar);
        setSupportActionBar(toolbar);

        rootBgView = (ImageView) findViewById(R.id.setting_background_body);
        locationText = (TextView) findViewById(R.id.settting_location_text);

        ImageView backgroundView = (ImageView) findViewById(R.id.settting_background_img);
        ImageView foregroundView = (ImageView) findViewById(R.id.settting_foreground_img);
        backgroundView.setImageDrawable(dialDrawable = new DialDrawable());
        foregroundView.setImageDrawable(pointerDrawable = new PointerDrawable());

    }

    private void initData(){

        onDialBgColorChange(Settings.dialBgColor(this));
        onDialScaleColorChange(Settings.dialScaleColor(this));
        onDialTextColorChange(Settings.dialTextColor(this));
        onPointerBgColorChange(Settings.pointerBgColor(this));
        onPointerColorChange(Settings.pointerColor(this));
        onRootBgColorChange(Settings.rootBgColor(this));
        onLocationTextColorChange(Settings.locationTextColor(this));

        onRootBackgroundImgChange();
        onDialBackgroundImgChange();
        onPointerBackgroundImgChange();
        onLanguageModeChange();
    }

    private void onDialBgColorChange(int color){
        dialDrawable.setBgColor(color);
    }

    private void onDialScaleColorChange(int color){
        dialDrawable.setScaleColor(color);
    }

    private void onDialTextColorChange(int color){
        dialDrawable.setTextColor(color);
    }


    private void onPointerBgColorChange(int color){
        pointerDrawable.setBgColor(color);
    }


    private void onPointerColorChange(int color){
        pointerDrawable.setColor(color);
    }

    private void onRootBgColorChange(int color){
        rootBgView.setBackgroundColor(color);
    }

    private void onLanguageModeChange(){
        onLanguageModeChange(Settings.isChinaseScale(this));
    }

    private void onLanguageModeChange(boolean type){
        dialDrawable.setChinase(type);
    }

    private void onLocationTextColorChange(int color){
        locationText.setTextColor(color);
    }

    private void onRootBackgroundImgChange(){
        if(Settings.isShowRootBgImg(this)){
            glide.load(Settings.getRootBgImg(this)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(rootBgView);
        }else{
            rootBgView.setImageDrawable(null);
        }
    }

    private void onDialBackgroundImgChange(){
        dialDrawable.setShowBitmap(Settings.isShowDialBgImg(this));
        glide.load(Settings.getDialBgImg(this)).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                dialDrawable.setBitmap(resource);
            }
        });
    }

    private void onPointerBackgroundImgChange(){
        pointerDrawable.setShowBitmap(Settings.isShowPointerBgImg(this));
        glide.load(Settings.getPointerBgImg(this)).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                pointerDrawable.setBitmap(resource);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            case "DIAL_BG_COLOR":
                onDialBgColorChange(sharedPreferences.getInt(key,Color.WHITE));
                break;
            case "DIAL_SCALE_COLOR":
                onDialScaleColorChange(sharedPreferences.getInt(key,Color.GRAY));
                break;
            case "DIAL_TEXT_COLOR":
                onDialTextColorChange(sharedPreferences.getInt(key,Color.GRAY));
                break;
            case "POINTER_COLOR":
                onPointerColorChange(sharedPreferences.getInt(key,Color.GRAY));
                break;
            case "POINTER_BG_COLOR":
                onPointerBgColorChange(sharedPreferences.getInt(key,Color.TRANSPARENT));
                break;
            case "ROOT_BG_COLOR":
                onRootBgColorChange(sharedPreferences.getInt(key,ContextCompat.getColor(this, R.color.background)));
                break;
            case "LOCATION_TEXT_COLOR":
                onLocationTextColorChange(sharedPreferences.getInt(key,Color.GRAY));
                break;
            case "ROOT_BG_IMAGE_STATE":
            case "ROOT_BG_IMAGE":
                onRootBackgroundImgChange();
                break;
            case "DIAL_BG_IMAGE_STATE":
            case "DIAL_BG_IMAGE":
                onDialBackgroundImgChange();
                break;
            case "POINTER_BG_IMAGE_STATE":
            case "POINTER_BG_IMAGE":
                onPointerBackgroundImgChange();
                break;
            case "SHOW_SETTING_BTN":
                break;
            case "STABLE_MODE":
                break;
            case "ROTATING_FOREGROUND":
                break;
            case "OLD_API":
                break;
            case "3D_MODE":
                break;
            case "CAMERA_MODE":
                break;
            case "CHINESE_MODE":
                onLanguageModeChange(sharedPreferences.getBoolean(key,true));
                break;
        }
    }
}
