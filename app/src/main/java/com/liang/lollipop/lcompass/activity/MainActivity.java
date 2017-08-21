package com.liang.lollipop.lcompass.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.fragment.BaseFragment;
import com.liang.lollipop.lcompass.util.LSurfaceUtil;
import com.liang.lollipop.lcompass.util.OtherUtil;
import com.liang.lollipop.lcompass.util.PermissionsUtil;
import com.liang.lollipop.lcompass.util.Settings;

/**
 * 主页
 * @author Lollipop
 */
public class MainActivity extends BaseActivity implements SensorEventListener, LocationListener {

    private LSurfaceUtil surfaceUtil;

    private static final int REQUEST_CAMERA = 235;

    private static final int REQUEST_LOCATION = 23;
    private static final int REQUEST_SENSOR = 24;

    private static final String NO_LOC_PERMISSION = "您未授权使用位置权限，将无法显示位置信息。是否前往开启？";
    private static final String NO_SENSOR_PERMISSION = "您未授权使用传感器，将无法显示方位信息。是否前往开启？";
    private static final String NO_CAMERA_PERMISSION = "您未授权使用摄像头，将无法显示方位信息。是否前往开启？";

    //传感器管理器
    private SensorManager sensorManager;
    // 加速度传感器
    private Sensor accelerometer;
    // 地磁场传感器
    private Sensor magnetic;
    // 老版的定位API
    private Sensor oldOrientation;
    //气压传感器
    private Sensor pressureSensor;

    // 加速度
    private float[] accelerometerValues = new float[3];
    // 地磁场
    private float[] magneticFieldValues = new float[3];

    //方向的实际值
    private float[] orientationValues = new float[3];
    //方向的矩阵数据
    private float[] orientationR = new float[9];
    //位置服务
    private LocationManager locationManager;

    //模式切换
    private ImageView modelChangeView;
    //3D模式切换的View
    private ImageView model3DChangeView;
    //相机模式的按钮
    private ImageView cameraModeChangeView;
    //类型变化按钮
    private ImageView typeChangeView;
    //设置按钮
    private View settingBtn;

    //是否渲染前景
    private boolean isRotatingForeground;
    //老版模式
    private boolean isOldModel;
    //是否启用稳定模式
    private boolean isStableMode;
    //是否启用3D模式
    private boolean is3DMode;
    //是否是相机模式
    private boolean isCameraMode;

    private BaseFragment fragment;

    //触发相机模式的值
    private float triggerCameraSize = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        initView();
        PermissionsUtil.checkPermissions(this, REQUEST_LOCATION, PermissionsUtil.ACCESS_FINE_LOCATION);
        PermissionsUtil.checkPermissions(this, REQUEST_SENSOR, PermissionsUtil.BODY_SENSORS);
        PermissionsUtil.checkPermissions(this, REQUEST_CAMERA,PermissionsUtil.CAMERA_);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(surfaceUtil!=null)
            surfaceUtil.onStart();

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N_MR1
                ||Settings.isShowSettingBtn(this)){
            settingBtn.setVisibility(View.VISIBLE);
            settingBtn.setOnClickListener(this);
        }

        isOldModel = Settings.oldModel(this);
        isStableMode = Settings.isStableMode(this);
        is3DMode = Settings.is3DMode(this);
        isCameraMode = Settings.isCameraMode(this);
        onTypeChange();
        onModelChange();
        on3DModelChange();
        onCameraModelChange();

        registerSensorService();
        initLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(surfaceUtil!=null)
            surfaceUtil.onStop();
        // 解除注册
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        if(locationManager!=null)
            locationManager.removeUpdates(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                initLocation();
                break;
            case REQUEST_SENSOR:
                registerSensorService();
                break;
            case REQUEST_CAMERA:
                if(!PermissionsUtil.checkPermission(this,PermissionsUtil.CAMERA_)){
                    isCameraMode = false;
                    Settings.setCameraMode(this,false);
                    onCameraModelChange();
                }
                break;
        }
    }

    private void initSensor() {
        // 实例化传感器管理者
        if (sensorManager == null)
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (isOldModel) {
            //老版方位获取器
            if (oldOrientation == null)
                oldOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        } else {
            // 初始化加速度传感器
            if (accelerometer == null)
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // 初始化地磁场传感器
            if (magnetic == null)
                magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
        if(pressureSensor==null)
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

    }

    private void registerSensorService() {
        if (PermissionsUtil.checkPermissions(this, PermissionsUtil.BODY_SENSORS)) {
            //取消监听器
            if (sensorManager != null)
                sensorManager.unregisterListener(this);
            //如果是老版模式
            if (isOldModel) {
                if (sensorManager == null || oldOrientation == null)
                    initSensor();
                sensorManager.registerListener(this, oldOrientation,
                        SensorManager.SENSOR_DELAY_FASTEST);
            } else {//新模式
                if (sensorManager == null || accelerometer == null || magnetic == null)
                    initSensor();
                // 注册监听
                if(isStableMode){
                    sensorManager.registerListener(this,
                            accelerometer, SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(this, magnetic,
                            SensorManager.SENSOR_DELAY_GAME);
                }else{
                    sensorManager.registerListener(this,
                            accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
                    sensorManager.registerListener(this, magnetic,
                            SensorManager.SENSOR_DELAY_FASTEST);
                }
            }
            if(pressureSensor==null)
                initSensor();
            if(pressureSensor!=null)
                sensorManager.registerListener(this,
                        pressureSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            PermissionsUtil.popPermissionsDialog(this,NO_SENSOR_PERMISSION);
            onSensorUpdate(0,0,0);
        }
    }

    private void initView() {
        TextureView textureView = (TextureView) findViewById(R.id.activity_main_background_surfaceview);
        surfaceUtil = LSurfaceUtil.withTexture(textureView);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (BaseFragment) fragmentManager.findFragmentById(R.id.fragment_compass);

        settingBtn = findViewById(R.id.setting_img);

        typeChangeView = (ImageView) findViewById(R.id.type_change_img);
        typeChangeView.setOnClickListener(this);

        modelChangeView = (ImageView) findViewById(R.id.model_change_img);
        modelChangeView.setOnClickListener(this);

        model3DChangeView = (ImageView) findViewById(R.id.model_3d_change_img);
        model3DChangeView.setOnClickListener(this);

        cameraModeChangeView = (ImageView) findViewById(R.id.camera_mode_change_img);
        cameraModeChangeView.setOnClickListener(this);
    }

    private void openCamera(){
        if(!PermissionsUtil.checkPermissions(this,PermissionsUtil.CAMERA_)){
            PermissionsUtil.popPermissionsDialog(this,NO_CAMERA_PERMISSION);
            return;
        }
        if(surfaceUtil.isCameraOpened())
            return;
        try {
            surfaceUtil.withCamera2(MainActivity.this);
            String[] ids = surfaceUtil.getCameraIdList();
            if(ids!=null&&ids.length>0){
                surfaceUtil.openCamera(MainActivity.this,ids[0],handler,getWindowManager());
                onCameraOpened();
            }else{
                S("找不到相机");
            }
        } catch (CameraAccessException e) {
            S(LSurfaceUtil.getCodeError(e.getReason()));
        }
    }

    private void closeCamera(){
        onCameraClosed();
        surfaceUtil.closeCameras();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //气压传感器
        if(sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE){
            float hPa = sensorEvent.values[0];
            // 计算海拔
            float height = (float) (44330000*(1-(Math.pow((hPa/1013.25),1.0/5255.0))));
            onPressureChange(hPa,height);
            return;
        }

        if (isOldModel) {
            //方位传感器（旧API）
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){
                orientationValues = sensorEvent.values;
            }
        } else {
            //重力传感器以及加速度传感器配合来检查方位
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values;
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = sensorEvent.values;
            }
            if (orientationValues == null)
                orientationValues = new float[3];
            if (orientationR == null)
                orientationR = new float[9];
            SensorManager.getRotationMatrix(orientationR, null, accelerometerValues,
                    magneticFieldValues);
            SensorManager.getOrientation(orientationR, orientationValues);
            orientationValues[0] = (float) Math.toDegrees(orientationValues[0]);
            orientationValues[1] = (float) Math.toDegrees(orientationValues[1]);
            orientationValues[2] = (float) Math.toDegrees(orientationValues[2]);
//            orientationValues[1] *= -1;
            orientationValues[2] *= -1;
        }
        if (orientationValues[0] > 180) {
            //有些计量方式是360有些是180
            orientationValues[0] = (orientationValues[0] - 360);
        }
        onSensorUpdate(orientationValues[0],orientationValues[1],orientationValues[2]);
    }

    private void initLocation() {
        if(!PermissionsUtil.checkPermission(this,PermissionsUtil.ACCESS_FINE_LOCATION)
                &&!PermissionsUtil.checkPermission(this,PermissionsUtil.ACCESS_COARSE_LOCATION)){
            PermissionsUtil.popPermissionsDialog(this,NO_LOC_PERMISSION);
            onLocationChanged(null);
            return;
        }
        if(locationManager!=null)
            locationManager.removeUpdates(this);
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();// 条件对象，即指定条件过滤获得LocationProvider
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 较高精度
        criteria.setAltitudeRequired(true);// 是否需要高度信息
        criteria.setBearingRequired(true);// 是否需要方向信息
        criteria.setCostAllowed(true);// 是否产生费用
        criteria.setPowerRequirement(Criteria.POWER_LOW);// 设置低电耗
        String locationProviderName = locationManager.getBestProvider(criteria, true);

        if (TextUtils.isEmpty(locationProviderName)) {
            S("未找到位置提供装置，请检查是否开启定位");
            onLocationChanged(null);
            return;
        }
        onLocationChanged(locationManager.getLastKnownLocation(locationProviderName));
        locationManager.requestLocationUpdates(locationProviderName, 2000, 10, this);// 2秒或者距离变化10米时更新一次地理位置
    }

    private void onTypeChange(){
        int color = isRotatingForeground? ContextCompat.getColor(this, R.color.colorPrimary): Color.GRAY;
        typeChangeView.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_explore_black_24dp, color));
        fragment.onTypeChange(isRotatingForeground);
    }

    private void onModelChange(){
        int color = isOldModel ? Color.GRAY : ContextCompat.getColor(this, R.color.colorPrimary);
        modelChangeView.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_bubble_chart_black_24dp, color));
        fragment.onModelChange(isOldModel);
    }

    private void on3DModelChange(){
        int color = is3DMode ? ContextCompat.getColor(this, R.color.colorPrimary) : Color.GRAY;
        model3DChangeView.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_3d_rotation_black_24dp, color));
        fragment.on3DModelChange(is3DMode);
    }

    private void onCameraModelChange(){
        int color = isCameraMode ? ContextCompat.getColor(this, R.color.colorPrimary) : Color.GRAY;
        cameraModeChangeView.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_camera_black_24dp, color));
        fragment.onCameraModelChange(isCameraMode);
    }

    private void onPressureChange(float hPa,float altitude){
        fragment.onPressureChange(hPa,altitude);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.type_change_img:
                isRotatingForeground = !isRotatingForeground;
                Settings.setRotatingForeground(this,isRotatingForeground);
                onTypeChange();
                S(isRotatingForeground ? "当前为指针模式" : "当前为表盘模式","查看区别", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTypeInfo();
                    }
                });
                break;
            case R.id.model_change_img:
                isOldModel = !isOldModel;
                Settings.setOldModel(this,isOldModel);
                onModelChange();
                S(isOldModel ? "当前为旧版API模式" : "当前为新版API模式","查看区别", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showModelInfo();
                    }
                });
                registerSensorService();
                break;
            case R.id.setting_img:
//                startActivity(new Intent(this,SettingActivity.class), Pair.create((View)backgroundView,"DIAL"),Pair.create((View)foregroundView,"POINTER"));
                startActivity(new Intent(this,SettingActivity.class));
                break;
            case R.id.model_3d_change_img:
                is3DMode = !is3DMode;
                Settings.set3DMode(this,is3DMode);
                on3DModelChange();
                S(is3DMode ? "当前为3D模式" : "当前为2D模式");
                break;
            case R.id.camera_mode_change_img:
                isCameraMode = !isCameraMode;
                Settings.setCameraMode(this,isCameraMode);
                onCameraModelChange();
                S(isCameraMode ? "当前为相机模式" : "当前为普通模式");
                if(isCameraMode)
                    PermissionsUtil.checkPermissions(this,REQUEST_CAMERA,PermissionsUtil.CAMERA_);
                break;
        }
    }

    private void showModelInfo() {
        new AlertDialog.Builder(this).setTitle("API区别")
                .setMessage(R.string.api_info)
                .show();
    }

    private void showTypeInfo() {
        new AlertDialog.Builder(this).setTitle("模式区别")
                .setMessage(R.string.type_info)
                .show();
    }

    private void onSensorUpdate(float z,float x,float y){
        fragment.onSensorUpdate(z,x,y);
        if(isOpenCamera(x,y)){
            openCamera();
        }else{
            closeCamera();
        }
    }

    private boolean isOpenCamera(float x,float y){
        if(!isCameraMode)
            return false;
        if(Math.abs(x)>triggerCameraSize||Math.abs(y)>triggerCameraSize)
            return true;
        if(Math.abs(x)+Math.abs(y)>90)
            return true;
        return false;
    }

    private void onSensorStateUpdate(int accuracy){
        fragment.onSensorStateUpdate(accuracy);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        onSensorStateUpdate(accuracy);
    }

    @Override
    public void onLocationChanged(Location location) {
        fragment.onLocationUpdate(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(LocationProvider.OUT_OF_SERVICE==status){
            S("位置服务已断开");
            return;
        }
        if(LocationProvider.TEMPORARILY_UNAVAILABLE==status){
            S("位置服务暂不可用");
            return;
        }
        if(LocationProvider.AVAILABLE==status){
            S("位置服务已恢复运行");
            initLocation();
        }
        onLocationChanged(null);
    }

    @Override
    public void onProviderEnabled(String provider) {
        S("有新的位置提供源可用:"+provider.toUpperCase());
        initLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
        S("位置提供源被禁用:"+provider.toUpperCase());
        initLocation();
    }

    public void onCameraOpened(){
        fragment.onCameraOpened();
    }

    public void onCameraClosed(){
        fragment.onCameraClosed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(surfaceUtil!=null)
            surfaceUtil.onDestroy();
    }
}
