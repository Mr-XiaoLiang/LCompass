package com.liang.lollipop.lcompass.activity;

import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.WindowManager;

import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.util.LSurfaceUtil;
import com.liang.lollipop.lcompass.util.PermissionsUtil;

/**
 * 主页
 * @author Lollipop
 */
public class MainActivity extends BaseActivity {

    private LSurfaceUtil surfaceUtil;

    private static final int REQUEST_CAMERA = 235;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(surfaceUtil!=null)
            surfaceUtil.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(surfaceUtil!=null)
            surfaceUtil.onStop();
    }

    private void initView() {
        TextureView textureView = (TextureView) findViewById(R.id.activity_main_background_surfaceview);
        surfaceUtil = LSurfaceUtil.withTexture(textureView);
    }

    private void openCamera(){
        PermissionsUtil.checkPermissions(this, REQUEST_CAMERA, new PermissionsUtil.OnPermissionsPass() {
            @Override
            public void onPermissionsPass() {
                try {
                    surfaceUtil.withCamera2(MainActivity.this);
                    String[] ids = surfaceUtil.getCameraIdList();
                    if(ids!=null&&ids.length>0){
                        surfaceUtil.openCamera(MainActivity.this,ids[0],handler,getWindowManager());
                    }else{
                        S("找不到相机");
                    }
                } catch (CameraAccessException e) {
                    S(LSurfaceUtil.getCodeError(e.getReason()));
                }
            }
        },PermissionsUtil.CAMERA_);
    }

}
