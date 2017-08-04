package com.liang.lollipop.lcrop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.liang.lollipop.lcrop.R;
import com.liang.lollipop.lcrop.view.LCropImageView;

/**
 * @author Lollipop on 2017-08-02
 */
public class CropActivity extends BaseActivity {

    public static final String ARG_IMAGE_URL = "ARG_IMAGE_URL";
    public static final String ARG_TITLE = "ARG_TITLE";

    private LCropImageView cropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_crop_toolbar);
        setSupportActionBar(toolbar);
        initView();
        initData();
    }

    private void initView(){
        cropImageView = (LCropImageView) findViewById(R.id.activity_crop_crop);
    }

    private void initData(){
        Intent intent = getIntent();
        String title = intent.getStringExtra(ARG_TITLE);
        if(!TextUtils.isEmpty(title)&&getSupportActionBar()!=null)
            getSupportActionBar().setTitle(title);
        String path = intent.getStringExtra(ARG_IMAGE_URL);
        try{
            if(!TextUtils.isEmpty(path)){
                //TODO
//                glide.asBitmap().load(path).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        cropImageView.setImageBitmap(resource);
//                    }
//                });//.into(cropImageView);
                glide.load(path).asBitmap().into(cropImageView);
            }
        }catch (Exception e){
            S("无效的图片地址，Exception："+e.toString());
        }
    }

}
