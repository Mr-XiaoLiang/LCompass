package com.liang.lollipop.lcompass.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
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
import com.liang.lollipop.lcompass.drawable.CircleBgDrawable;
import com.liang.lollipop.lcompass.drawable.DialDrawable;
import com.liang.lollipop.lcompass.drawable.PointerDrawable;
import com.liang.lollipop.lcompass.util.OtherUtil;
import com.liang.lollipop.lcompass.util.Settings;
import com.liang.lollipop.lcompass.util.SharedPreferencesUtils;
import com.liang.lollipop.lcrop.LCropUtil;
import com.liang.lollipop.lcrop.activity.SelectImagesActivity;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Lollipop
 * 设置
 */
public class SettingActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener,SwitchCompat.OnCheckedChangeListener {

    /*-------------------------------常量部分-开始-------------------------------*/

    private static final int REQUEST_ROOT_BG = 456;
    private static final int REQUEST_DIAL_BG = 457;
    private static final int REQUEST_POINTER_BG = 458;

    private static final int REQUEST_CROP_ROOT_BG = 466;
    private static final int REQUEST_CROP_DIAL_BG = 467;
    private static final int REQUEST_CROP_POINTER_BG = 468;

    /*-------------------------------常量部分-结束-------------------------------*/

    private View settingSheet;
    private ImageView rootBgView;
    private TextView locationText;

    /*-------------------------------颜色调整部分-开始-------------------------------*/
    private int dialBgColor = 0;
    private int dialScaleColor = 0;
    private int dialTextColor = 0;
    private int pointerBgColor = 0;
    private int pointerColor = 0;
    private int rootBgColor = 0;
    private int locationTextColor = 0;

    private CircleBgDrawable dialBgColorDrawable;
    private CircleBgDrawable dialScaleColorDrawable;
    private CircleBgDrawable dialTextColorDrawable;
    private CircleBgDrawable pointerBgColorDrawable;
    private CircleBgDrawable pointerColorDrawable;
    private CircleBgDrawable rootBgColorDrawable;
    private CircleBgDrawable locationTextColorDrawable;

    private TextView dialBgColorText;
    private TextView dialScaleColorText;
    private TextView dialTextColorText;
    private TextView pointerBgColorText;
    private TextView pointerColorText;
    private TextView rootBgColorText;
    private TextView locationTextColorText;

    private SeekBar dialBgColorAlphaBar;
    private SeekBar dialBgColorRedBar;
    private SeekBar dialBgColorGreenBar;
    private SeekBar dialBgColorBlueBar;

    private SeekBar dialScaleColorAlphaBar;
    private SeekBar dialScaleColorRedBar;
    private SeekBar dialScaleColorGreenBar;
    private SeekBar dialScaleColorBlueBar;

    private SeekBar dialTextColorAlphaBar;
    private SeekBar dialTextColorRedBar;
    private SeekBar dialTextColorGreenBar;
    private SeekBar dialTextColorBlueBar;

    private SeekBar pointerBgColorAlphaBar;
    private SeekBar pointerBgColorRedBar;
    private SeekBar pointerBgColorGreenBar;
    private SeekBar pointerBgColorBlueBar;

    private SeekBar pointerColorAlphaBar;
    private SeekBar pointerColorRedBar;
    private SeekBar pointerColorGreenBar;
    private SeekBar pointerColorBlueBar;

    private SeekBar rootBgColorAlphaBar;
    private SeekBar rootBgColorRedBar;
    private SeekBar rootBgColorGreenBar;
    private SeekBar rootBgColorBlueBar;

    private SeekBar locationTextColorAlphaBar;
    private SeekBar locationTextColorRedBar;
    private SeekBar locationTextColorGreenBar;
    private SeekBar locationTextColorBlueBar;
    /*-------------------------------颜色调整部分-结束-------------------------------*/

    private PointerDrawable pointerDrawable;
    private DialDrawable dialDrawable;

    /*-------------------------------图片调整部分-开始-------------------------------*/
    private SwitchCompat rootBgSwitch;
    private SwitchCompat dialBgSwitch;
    private SwitchCompat pointerBgSwitch;

    private ImageView rootBgShowView;
    private ImageView dialBgShowView;
    private ImageView pointerBgShowView;
    /*-------------------------------图片调整部分-结束-------------------------------*/

    private SwitchCompat showSettingBtnSwitch;
    private SwitchCompat stableModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData(){
        dialBgColor = Settings.dialBgColor(this);
        dialScaleColor = Settings.dialScaleColor(this);
        dialTextColor = Settings.dialTextColor(this);
        pointerBgColor = Settings.pointerBgColor(this);
        pointerColor = Settings.pointerColor(this);
        rootBgColor = Settings.rootBgColor(this);
        locationTextColor = Settings.locationTextColor(this);

        dialBgColorAlphaBar.setProgress(Color.alpha(dialBgColor));
        dialBgColorRedBar.setProgress(Color.red(dialBgColor));
        dialBgColorGreenBar.setProgress(Color.green(dialBgColor));
        dialBgColorBlueBar.setProgress(Color.blue(dialBgColor));

        dialScaleColorAlphaBar.setProgress(Color.alpha(dialScaleColor));
        dialScaleColorRedBar.setProgress(Color.red(dialScaleColor));
        dialScaleColorGreenBar.setProgress(Color.green(dialScaleColor));
        dialScaleColorBlueBar.setProgress(Color.blue(dialScaleColor));

        dialTextColorAlphaBar.setProgress(Color.alpha(dialTextColor));
        dialTextColorRedBar.setProgress(Color.red(dialTextColor));
        dialTextColorGreenBar.setProgress(Color.green(dialTextColor));
        dialTextColorBlueBar.setProgress(Color.blue(dialTextColor));

        pointerBgColorAlphaBar.setProgress(Color.alpha(pointerBgColor));
        pointerBgColorRedBar.setProgress(Color.red(pointerBgColor));
        pointerBgColorGreenBar.setProgress(Color.green(pointerBgColor));
        pointerBgColorBlueBar.setProgress(Color.blue(pointerBgColor));

        pointerColorAlphaBar.setProgress(Color.alpha(pointerColor));
        pointerColorRedBar.setProgress(Color.red(pointerColor));
        pointerColorGreenBar.setProgress(Color.green(pointerColor));
        pointerColorBlueBar.setProgress(Color.blue(pointerColor));

        rootBgColorAlphaBar.setProgress(Color.alpha(rootBgColor));
        rootBgColorRedBar.setProgress(Color.red(rootBgColor));
        rootBgColorGreenBar.setProgress(Color.green(rootBgColor));
        rootBgColorBlueBar.setProgress(Color.blue(rootBgColor));

        locationTextColorAlphaBar.setProgress(Color.alpha(locationTextColor));
        locationTextColorRedBar.setProgress(Color.red(locationTextColor));
        locationTextColorGreenBar.setProgress(Color.green(locationTextColor));
        locationTextColorBlueBar.setProgress(Color.blue(locationTextColor));

        onDialBgColorChange(dialBgColor);
        onDialScaleColorChange(dialScaleColor);
        onDialTextColorChange(dialTextColor);
        onPointerBgColorChange(pointerBgColor);
        onPointerColorChange(pointerColor);
        onRootBgColorChange(rootBgColor);

        rootBgSwitch.setChecked(Settings.isShowRootBgImg(this));
        dialBgSwitch.setChecked(Settings.isShowDialBgImg(this));
        pointerBgSwitch.setChecked(Settings.isShowPointerBgImg(this));
        showSettingBtnSwitch.setChecked(Settings.isShowSettingBtn(this));
        stableModeSwitch.setChecked(Settings.isStableMode(this));
        onRootBackgroundImgChange();
        onDialBackgroundImgChange();
        onPointerBackgroundImgChange();
        onLocationTextColorChange();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_setting_toolbar);
        setSupportActionBar(toolbar);

        rootBgView = (ImageView) findViewById(R.id.setting_background_body);
        locationText = (TextView) findViewById(R.id.settting_location_text);

        settingSheet = findViewById(R.id.sheet_setting_root);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(settingSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback(){

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                /*
                 STATE_COLLAPSED： 默认的折叠状态， bottom sheets只在底部显示一部分布局。显示高度可以通过 app:behavior_peekHeight 设置（默认是0）
                 STATE_DRAGGING ： 过渡状态，此时用户正在向上或者向下拖动bottom sheet
                 STATE_SETTLING: 视图从脱离手指自由滑动到最终停下的这一小段时间
                 STATE_EXPANDED： bottom sheet 处于完全展开的状态：当bottom sheet的高度低于CoordinatorLayout容器时，整个bottom sheet都可见；或者CoordinatorLayout容器已经被bottom sheet填满。
                 STATE_HIDDEN ： 默认无此状态（可通过app:behavior_hideable 启用此状态），启用后用户将能通过向下滑动完全隐藏 bottom sheet
                 */
//                switch (newState){
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                    case BottomSheetBehavior.STATE_COLLAPSED:
//                    case BottomSheetBehavior.STATE_DRAGGING:
//                    case BottomSheetBehavior.STATE_EXPANDED:
//                    case BottomSheetBehavior.STATE_SETTLING:
//                        return;
//                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                settingSheet.setBackgroundColor(Color.argb((int)(slideOffset*128),0,0,0));
            }
        });

        ImageView backgroundView = (ImageView) findViewById(R.id.settting_background_img);
        ImageView foregroundView = (ImageView) findViewById(R.id.settting_foreground_img);
        backgroundView.setImageDrawable(dialDrawable = new DialDrawable());
        foregroundView.setImageDrawable(pointerDrawable = new PointerDrawable());

        //颜色设置部分View的初始化开始------------------------
        dialBgColorText = (TextView) findViewById(R.id.dial_bg_text);
        dialScaleColorText = (TextView) findViewById(R.id.dial_scale_text);
        dialTextColorText = (TextView) findViewById(R.id.dial_text_text);
        pointerBgColorText = (TextView) findViewById(R.id.dial_pointer_bg_text);
        pointerColorText = (TextView) findViewById(R.id.dial_pointer_text);
        rootBgColorText = (TextView) findViewById(R.id.root_bg_text);
        locationTextColorText = (TextView) findViewById(R.id.location_text_color_text);

        dialBgColorAlphaBar = (SeekBar) findViewById(R.id.dial_bg_a);
        dialBgColorRedBar = (SeekBar) findViewById(R.id.dial_bg_r);
        dialBgColorGreenBar = (SeekBar) findViewById(R.id.dial_bg_g);
        dialBgColorBlueBar = (SeekBar) findViewById(R.id.dial_bg_b);

        dialScaleColorAlphaBar = (SeekBar) findViewById(R.id.dial_scale_a);
        dialScaleColorRedBar = (SeekBar) findViewById(R.id.dial_scale_r);
        dialScaleColorGreenBar = (SeekBar) findViewById(R.id.dial_scale_g);
        dialScaleColorBlueBar = (SeekBar) findViewById(R.id.dial_scale_b);

        dialTextColorAlphaBar = (SeekBar) findViewById(R.id.dial_text_a);
        dialTextColorRedBar = (SeekBar) findViewById(R.id.dial_text_r);
        dialTextColorGreenBar = (SeekBar) findViewById(R.id.dial_text_g);
        dialTextColorBlueBar = (SeekBar) findViewById(R.id.dial_text_b);

        pointerBgColorAlphaBar = (SeekBar) findViewById(R.id.dial_pointer_bg_a);
        pointerBgColorRedBar = (SeekBar) findViewById(R.id.dial_pointer_bg_r);
        pointerBgColorGreenBar = (SeekBar) findViewById(R.id.dial_pointer_bg_g);
        pointerBgColorBlueBar = (SeekBar) findViewById(R.id.dial_pointer_bg_b);

        pointerColorAlphaBar = (SeekBar) findViewById(R.id.dial_pointer_a);
        pointerColorRedBar = (SeekBar) findViewById(R.id.dial_pointer_r);
        pointerColorGreenBar = (SeekBar) findViewById(R.id.dial_pointer_g);
        pointerColorBlueBar = (SeekBar) findViewById(R.id.dial_pointer_b);

        rootBgColorAlphaBar = (SeekBar) findViewById(R.id.root_bg_a);
        rootBgColorRedBar = (SeekBar) findViewById(R.id.root_bg_r);
        rootBgColorGreenBar = (SeekBar) findViewById(R.id.root_bg_g);
        rootBgColorBlueBar = (SeekBar) findViewById(R.id.root_bg_b);

        locationTextColorAlphaBar = (SeekBar) findViewById(R.id.location_text_color_a);
        locationTextColorRedBar = (SeekBar) findViewById(R.id.location_text_color_r);
        locationTextColorGreenBar = (SeekBar) findViewById(R.id.location_text_color_g);
        locationTextColorBlueBar = (SeekBar) findViewById(R.id.location_text_color_b);

        dialBgColorAlphaBar.setOnSeekBarChangeListener(this);
        dialBgColorRedBar.setOnSeekBarChangeListener(this);
        dialBgColorGreenBar.setOnSeekBarChangeListener(this);
        dialBgColorBlueBar.setOnSeekBarChangeListener(this);

        dialScaleColorAlphaBar.setOnSeekBarChangeListener(this);
        dialScaleColorRedBar.setOnSeekBarChangeListener(this);
        dialScaleColorGreenBar.setOnSeekBarChangeListener(this);
        dialScaleColorBlueBar.setOnSeekBarChangeListener(this);

        dialTextColorAlphaBar.setOnSeekBarChangeListener(this);
        dialTextColorRedBar.setOnSeekBarChangeListener(this);
        dialTextColorGreenBar.setOnSeekBarChangeListener(this);
        dialTextColorBlueBar.setOnSeekBarChangeListener(this);

        pointerBgColorAlphaBar.setOnSeekBarChangeListener(this);
        pointerBgColorRedBar.setOnSeekBarChangeListener(this);
        pointerBgColorGreenBar.setOnSeekBarChangeListener(this);
        pointerBgColorBlueBar.setOnSeekBarChangeListener(this);

        pointerColorAlphaBar.setOnSeekBarChangeListener(this);
        pointerColorRedBar.setOnSeekBarChangeListener(this);
        pointerColorGreenBar.setOnSeekBarChangeListener(this);
        pointerColorBlueBar.setOnSeekBarChangeListener(this);

        rootBgColorAlphaBar.setOnSeekBarChangeListener(this);
        rootBgColorRedBar.setOnSeekBarChangeListener(this);
        rootBgColorGreenBar.setOnSeekBarChangeListener(this);
        rootBgColorBlueBar.setOnSeekBarChangeListener(this);

        locationTextColorAlphaBar.setOnSeekBarChangeListener(this);
        locationTextColorRedBar.setOnSeekBarChangeListener(this);
        locationTextColorGreenBar.setOnSeekBarChangeListener(this);
        locationTextColorBlueBar.setOnSeekBarChangeListener(this);

        ImageView dialBgImg = (ImageView) findViewById(R.id.dial_bg_img);
        dialBgImg.setImageDrawable(dialBgColorDrawable = new CircleBgDrawable());

        ImageView dialScaleImg = (ImageView) findViewById(R.id.dial_scale_img);
        dialScaleImg.setImageDrawable(dialScaleColorDrawable = new CircleBgDrawable());

        ImageView dialTextImg = (ImageView) findViewById(R.id.dial_text_img);
        dialTextImg.setImageDrawable(dialTextColorDrawable = new CircleBgDrawable());

        ImageView pointerBgImg = (ImageView) findViewById(R.id.dial_pointer_bg_img);
        pointerBgImg.setImageDrawable(pointerBgColorDrawable = new CircleBgDrawable());

        ImageView pointerImg = (ImageView) findViewById(R.id.dial_pointer_img);
        pointerImg.setImageDrawable(pointerColorDrawable = new CircleBgDrawable());

        ImageView rootBgImg = (ImageView) findViewById(R.id.root_bg_img);
        rootBgImg.setImageDrawable(rootBgColorDrawable = new CircleBgDrawable());

        ImageView locImg = (ImageView) findViewById(R.id.location_text_color_img);
        locImg.setImageDrawable(locationTextColorDrawable = new CircleBgDrawable());

        //颜色设置部分View的初始化结束------------------------

        //图片设置部分View的初始化开始------------------------

        rootBgSwitch = (SwitchCompat) findViewById(R.id.image_background_root_switch);
        dialBgSwitch = (SwitchCompat) findViewById(R.id.image_background_dial_switch);
        pointerBgSwitch = (SwitchCompat) findViewById(R.id.image_background_pointer_switch);

        rootBgSwitch.setOnCheckedChangeListener(this);
        dialBgSwitch.setOnCheckedChangeListener(this);
        pointerBgSwitch.setOnCheckedChangeListener(this);

        rootBgShowView = (ImageView) findViewById(R.id.image_background_root_show);
        dialBgShowView = (ImageView) findViewById(R.id.image_background_dial_show);
        pointerBgShowView = (ImageView) findViewById(R.id.image_background_pointer_show);

        findViewById(R.id.image_background_root).setOnClickListener(this);
        findViewById(R.id.image_background_dial).setOnClickListener(this);
        findViewById(R.id.image_background_pointer).setOnClickListener(this);

        //图片设置部分View的初始化结束------------------------
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N_MR1){
            findViewById(R.id.show_setting_btn_body).setVisibility(View.GONE);
        }
        showSettingBtnSwitch = (SwitchCompat) findViewById(R.id.show_setting_btn_switch);
        showSettingBtnSwitch.setOnCheckedChangeListener(this);

        stableModeSwitch = (SwitchCompat) findViewById(R.id.stable_mode_switch);
        stableModeSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.image_background_root:
                getPhoto("选择背景图片",REQUEST_ROOT_BG);
                break;
            case R.id.image_background_dial:
                getPhoto("选择表盘背景图",REQUEST_DIAL_BG);
                break;
            case R.id.image_background_pointer:
                getPhoto("选择指针背景图",REQUEST_POINTER_BG);
                break;
        }
    }

    private void getPhoto(String title,int requestCode){
        LCropUtil.selectPhotos(this,requestCode,title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(onPhotoResult(requestCode,resultCode,data))
            return;
//        if(onPhotoCropResult(requestCode,resultCode,data))
//            return;
        onPhotoCropResult(requestCode,resultCode,data);

    }

    private boolean onPhotoResult(int requestCode,int resultCode,Intent data){
        if(resultCode!=SelectImagesActivity.RESULT_OK)
            return false;
        ArrayList<String> imgList = data.getStringArrayListExtra(SelectImagesActivity.RESULT_DATA);
        if(imgList==null||imgList.size()<1){
            S("选择的图片为空");
            return false;
        }
        String path = imgList.get(0);
        if(TextUtils.isEmpty(path) || !new File(path).exists()){
            S("您选择的图片地址是空的");
            return false;
        }
        switch (requestCode){
            case REQUEST_ROOT_BG:
//                LCropUtil.cropPhoto(this,REQUEST_CROP_ROOT_BG, path,9,16,OtherUtil.getBackground(this));
                UCrop.of(Uri.fromFile(new File(path)),Uri.fromFile(OtherUtil.getTempImgFile(this)))
                        .withOptions(getUCropOption("剪裁背景图片",false))
                        .withAspectRatio(9,16)
                        .withMaxResultSize(1080,1920)
                        .start(this,REQUEST_CROP_ROOT_BG);
                return true;
            case REQUEST_DIAL_BG:
//                LCropUtil.cropPhoto(this,REQUEST_CROP_DIAL_BG, path,1,1,OtherUtil.getBackground(this));
                UCrop.of(Uri.fromFile(new File(path)),Uri.fromFile(OtherUtil.getTempImgFile(this)))
                        .withAspectRatio(1,1)
                        .withOptions(getUCropOption("剪裁表盘图片",true))
                        .withMaxResultSize(512,512)
                        .start(this,REQUEST_CROP_DIAL_BG);
                return true;
            case REQUEST_POINTER_BG:
//                LCropUtil.cropPhoto(this,REQUEST_CROP_POINTER_BG, path,1,1,OtherUtil.getBackground(this));
                UCrop.of(Uri.fromFile(new File(path)),Uri.fromFile(OtherUtil.getTempImgFile(this)))
                        .withAspectRatio(1,1)
                        .withOptions(getUCropOption("剪裁指针图片",true))
                        .withMaxResultSize(512,512)
                        .start(this,REQUEST_CROP_POINTER_BG);
                return true;
        }
        return false;
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

    private boolean onPhotoCropResult(int requestCode,int resultCode,Intent data){
        if(UCrop.RESULT_ERROR==resultCode){
            final Throwable cropError = UCrop.getError(data);
            S("图片剪裁出错了："+cropError.toString());
            return false;
        }
//        if(UCrop.REQUEST_CROP!=resultCode)
//            return false;
//        Uri resultUri = UCrop.getOutput(data);
        switch (requestCode){
            case REQUEST_CROP_ROOT_BG:
                if(OtherUtil.saveBackground(this,OtherUtil.getTempImgPath(this)))
                    onRootBackgroundImgChange();
                else
                    S("保存失败");
                return true;
            case REQUEST_CROP_DIAL_BG:
                if(OtherUtil.saveDialBackground(this,OtherUtil.getTempImgPath(this)))
                    onDialBackgroundImgChange();
                else
                    S("保存失败");
                return true;
            case REQUEST_CROP_POINTER_BG:
                if(OtherUtil.savePointerBackground(this,OtherUtil.getTempImgPath(this)))
                    onPointerBackgroundImgChange();
                else
                    S("保存失败");
                return true;
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(!fromUser)
            return;
        switch (seekBar.getId()){
            case R.id.dial_bg_a:
            case R.id.dial_bg_r:
            case R.id.dial_bg_g:
            case R.id.dial_bg_b:
                onDialBgColorChange();
                break;
            case R.id.dial_scale_a:
            case R.id.dial_scale_r:
            case R.id.dial_scale_g:
            case R.id.dial_scale_b:
                onDialScaleColorChange();
                break;
            case R.id.dial_text_a:
            case R.id.dial_text_r:
            case R.id.dial_text_g:
            case R.id.dial_text_b:
                onDialTextColorChange();
                break;
            case R.id.dial_pointer_bg_a:
            case R.id.dial_pointer_bg_r:
            case R.id.dial_pointer_bg_g:
            case R.id.dial_pointer_bg_b:
                onPointerBgColorChange();
                break;
            case R.id.dial_pointer_a:
            case R.id.dial_pointer_r:
            case R.id.dial_pointer_g:
            case R.id.dial_pointer_b:
                onPointerColorChange();
                break;
            case R.id.root_bg_a:
            case R.id.root_bg_r:
            case R.id.root_bg_g:
            case R.id.root_bg_b:
                onRootBgColorChange();
                break;
            case R.id.location_text_color_a:
            case R.id.location_text_color_r:
            case R.id.location_text_color_g:
            case R.id.location_text_color_b:
                onLocationTextColorChange();
                break;
        }
    }

    private void onDialBgColorChange(){
        dialBgColor = Color.argb(
                dialBgColorAlphaBar.getProgress(),
                dialBgColorRedBar.getProgress(),
                dialBgColorGreenBar.getProgress(),
                dialBgColorBlueBar.getProgress());
        Settings.setDialBgColor(this,dialBgColor);
        onDialBgColorChange(dialBgColor);
    }

    private void onDialBgColorChange(int color){
        dialDrawable.setBgColor(color);
        dialBgColorDrawable.setColor(color);
        dialBgColorText.setText(getColorValue(color));
    }

    private void onDialScaleColorChange(){
        dialScaleColor = Color.argb(
                dialScaleColorAlphaBar.getProgress(),
                dialScaleColorRedBar.getProgress(),
                dialScaleColorGreenBar.getProgress(),
                dialScaleColorBlueBar.getProgress());
        Settings.setDialScaleColor(this,dialScaleColor);
        onDialScaleColorChange(dialScaleColor);
    }

    private void onDialScaleColorChange(int color){
        dialDrawable.setScaleColor(color);
        dialScaleColorDrawable.setColor(color);
        dialScaleColorText.setText(getColorValue(color));
    }

    private void onDialTextColorChange(){
        dialTextColor = Color.argb(
                dialTextColorAlphaBar.getProgress(),
                dialTextColorRedBar.getProgress(),
                dialTextColorGreenBar.getProgress(),
                dialTextColorBlueBar.getProgress());
        Settings.setDialTextColor(this,dialTextColor);
        onDialTextColorChange(dialTextColor);
    }

    private void onDialTextColorChange(int color){
        dialDrawable.setTextColor(color);
        dialTextColorDrawable.setColor(color);
        dialTextColorText.setText(getColorValue(color));
    }

    private void onPointerBgColorChange(){
        pointerBgColor = Color.argb(
                pointerBgColorAlphaBar.getProgress(),
                pointerBgColorRedBar.getProgress(),
                pointerBgColorGreenBar.getProgress(),
                pointerBgColorBlueBar.getProgress());
        Settings.setPointerBgColor(this,pointerBgColor);
        onPointerBgColorChange(pointerBgColor);
    }

    private void onPointerBgColorChange(int color){
        pointerDrawable.setBgColor(color);
        pointerBgColorDrawable.setColor(color);
        pointerBgColorText.setText(getColorValue(color));
    }

    private void onPointerColorChange(){
        pointerColor = Color.argb(
                pointerColorAlphaBar.getProgress(),
                pointerColorRedBar.getProgress(),
                pointerColorGreenBar.getProgress(),
                pointerColorBlueBar.getProgress());
        Settings.setPointerColor(this,pointerColor);
        onPointerColorChange(pointerColor);
    }

    private void onPointerColorChange(int color){
        pointerDrawable.setColor(color);
        pointerColorDrawable.setColor(color);
        pointerColorText.setText(getColorValue(color));
    }

    private void onRootBgColorChange(){
        rootBgColor = Color.argb(
                rootBgColorAlphaBar.getProgress(),
                rootBgColorRedBar.getProgress(),
                rootBgColorGreenBar.getProgress(),
                rootBgColorBlueBar.getProgress());
        Settings.setRootBgColor(this,rootBgColor);
        onRootBgColorChange(rootBgColor);
    }

    private void onRootBgColorChange(int color){
        rootBgView.setBackgroundColor(color);
        rootBgColorDrawable.setColor(color);
        rootBgColorText.setText(getColorValue(color));
    }

    private void onLocationTextColorChange(){
        locationTextColor = Color.argb(
                locationTextColorAlphaBar.getProgress(),
                locationTextColorRedBar.getProgress(),
                locationTextColorGreenBar.getProgress(),
                locationTextColorBlueBar.getProgress());
        Settings.setLocationTextColor(this,locationTextColor);
        onLocationTextColorChange(locationTextColor);
    }

    private void onLocationTextColorChange(int color){
        locationText.setTextColor(color);
        locationTextColorDrawable.setColor(color);
        locationTextColorText.setText(getColorValue(color));
    }

    private String getColorValue(int color){
        if(color==0){
            return "#00000000";
        }
        if(Color.alpha(color)==0){
            return "#00"+Integer.toHexString(color).toUpperCase();
        }
        if(Color.alpha(color)<0x10){
            return "#0"+Integer.toHexString(color).toUpperCase();
        }
        return "#"+Integer.toHexString(color).toUpperCase();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.image_background_root_switch:
                Settings.setShowRootBgImg(this,isChecked);
                onRootBackgroundImgChange();
                break;
            case R.id.image_background_dial_switch:
                Settings.setShowDialBgImg(this,isChecked);
                onDialBackgroundImgChange();
                break;
            case R.id.image_background_pointer_switch:
                Settings.setShowPointerBgImg(this,isChecked);
                onPointerBackgroundImgChange();
                break;
            case R.id.show_setting_btn_switch:
                Settings.setShowSettingBtn(this,isChecked);
                break;
            case R.id.stable_mode_switch:
                Settings.setStableMode(this,isChecked);
                break;
        }
    }

    private void onRootBackgroundImgChange(){
        glide.load(OtherUtil.getBackground(this)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(rootBgShowView);
        if(Settings.isShowRootBgImg(this)){
            glide.load(OtherUtil.getBackground(this)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(rootBgView);
        }else{
            rootBgView.setImageDrawable(null);
        }
    }

    private void onDialBackgroundImgChange(){
        glide.load(OtherUtil.getDialBackground(this)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(dialBgShowView);
        dialDrawable.setShowBitmap(Settings.isShowDialBgImg(this));
        glide.load(OtherUtil.getDialBackground(this)).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                dialDrawable.setBitmap(resource);
            }
        });
    }

    private void onPointerBackgroundImgChange(){
        glide.load(OtherUtil.getPointerBackground(this)).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(pointerBgShowView);
        pointerDrawable.setShowBitmap(Settings.isShowPointerBgImg(this));
        glide.load(OtherUtil.getPointerBackground(this)).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                pointerDrawable.setBitmap(resource);
            }
        });
    }

}
