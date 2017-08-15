package com.liang.lollipop.lcompass.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.activity.SettingActivity;
import com.liang.lollipop.lcompass.drawable.CircleBgDrawable;
import com.liang.lollipop.lcompass.drawable.DialDrawable;
import com.liang.lollipop.lcompass.drawable.PointerDrawable;
import com.liang.lollipop.lcompass.util.OtherUtil;
import com.liang.lollipop.lcompass.util.PermissionsUtil;
import com.liang.lollipop.lcompass.util.Settings;

import java.text.DecimalFormat;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Lollipop on 2017/08/15.
 * 指南针分页
 */
public class CompassFragment extends BaseFragment implements SensorEventListener, LocationListener {

    private static final int REQUEST_LOCATION = 23;
    private static final int REQUEST_SENSOR = 24;

    //传感器管理器
    private SensorManager sensorManager;
    // 加速度传感器
    private Sensor accelerometer;
    // 地磁场传感器
    private Sensor magnetic;
    // 老版的定位API
    private Sensor oldOrientation;

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
    //定位模式的名字
//    private String locationProviderName = null;

    private static final String NO_LOC_PERMISSION = "未授权使用位置权限，点击设置";
    private static final String NO_SENSOR_PERMISSION = "未授权使用传感器权限，点击设置";

    //表盘View
    private View dialView;
    //背景
    private ImageView rootBgView;
    //背景View
    private ImageView backgroundView;
    //背景渲染Drawable
    private DialDrawable backgroundDrawable;
    //前景View
    private ImageView foregroundView;
    //前景渲染Drawable
    private PointerDrawable foregroundDrawable;
    //角度的文本
    private TextView angleView;
    //圆形的渲染
    private CircleBgDrawable statusDrawable;
    //类型变化按钮
    private ImageView typeChangeView;
    //位置显示的View
    private TextView locationView;
    //模式切换
    private ImageView modelChangeView;
    //3D模式切换的View
    private ImageView model3DChangeView;

    //浮点数的格式化
    private DecimalFormat decimalFormat;

    //是否渲染前景
    private boolean isRotatingForeground;

    //老版模式
    private boolean isOldModel;
    //是否启用稳定模式
    private boolean isStableMode;
    //设置按钮
    private View settingBtn;
    //是否启用3D模式
    private boolean is3DMode;

    @Override
    public void onResume() {
        super.onResume();
        isRotatingForeground = Settings.isRotatingForeground(getContext());
        isOldModel = Settings.oldModel(getContext());
        isStableMode = Settings.isStableMode(getContext());
        is3DMode = Settings.is3DMode(getContext());
        initDial();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compass,container,false);
        initView(root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        decimalFormat = new DecimalFormat("#0.00000");
        PermissionsUtil.checkPermissions(getActivity(), REQUEST_LOCATION, PermissionsUtil.ACCESS_FINE_LOCATION);
        PermissionsUtil.checkPermissions(getActivity(), REQUEST_SENSOR, PermissionsUtil.BODY_SENSORS);
        isRotatingForeground = Settings.isRotatingForeground(getContext());
        isOldModel = Settings.oldModel(getContext());
        isStableMode = Settings.isStableMode(getContext());
        is3DMode = Settings.is3DMode(getContext());
        onTypeChange();
        onModelChange();
        on3DModelChange();
    }

    private void initView(View root) {
        rootBgView = (ImageView) root.findViewById(R.id.background_body);
        backgroundView = (ImageView) root.findViewById(R.id.background_img);
        foregroundView = (ImageView) root.findViewById(R.id.foreground_img);
        angleView = (TextView) root.findViewById(R.id.angle_text);
        angleView.setOnClickListener(this);
        locationView = (TextView) root.findViewById(R.id.location_text);
        locationView.setOnClickListener(this);

        ImageView statusView = (ImageView) root.findViewById(R.id.status_img);
        statusView.setImageDrawable(statusDrawable = new CircleBgDrawable());
        typeChangeView = (ImageView) root.findViewById(R.id.type_change_img);
        typeChangeView.setOnClickListener(this);

        modelChangeView = (ImageView) root.findViewById(R.id.model_change_img);
        modelChangeView.setOnClickListener(this);

        model3DChangeView = (ImageView) root.findViewById(R.id.model_3d_change_img);
        model3DChangeView.setOnClickListener(this);

        backgroundView.setImageDrawable(backgroundDrawable = new DialDrawable());
        foregroundView.setImageDrawable(foregroundDrawable = new PointerDrawable());

        settingBtn = root.findViewById(R.id.setting_img);

        dialView = root.findViewById(R.id.dial_view);
    }

    private void initDial(){
        backgroundDrawable.setTextColor(Settings.dialTextColor(getContext()));
        backgroundDrawable.setBgColor(Settings.dialBgColor(getContext()));
        backgroundDrawable.setScaleColor(Settings.dialScaleColor(getContext()));
        foregroundDrawable.setBgColor(Settings.pointerBgColor(getContext()));
        foregroundDrawable.setColor(Settings.pointerColor(getContext()));
        rootBgView.setBackgroundColor(Settings.rootBgColor(getContext()));
        locationView.setTextColor(Settings.locationTextColor(getContext()));
        angleView.setTextColor(Settings.locationTextColor(getContext()));
        if(Settings.isShowRootBgImg(getContext())){
            glide.load(OtherUtil.getBackground(getContext())).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(rootBgView);
        }else{
            rootBgView.setImageDrawable(null);
        }
        backgroundDrawable.setShowBitmap(Settings.isShowDialBgImg(getContext()));
        glide.load(OtherUtil.getDialBackground(getContext())).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                backgroundDrawable.setBitmap(resource);
            }
        });
        foregroundDrawable.setShowBitmap(Settings.isShowPointerBgImg(getContext()));
        glide.load(OtherUtil.getPointerBackground(getContext())).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                foregroundDrawable.setBitmap(resource);
            }
        });
    }

    private void initSensor() {
        // 实例化传感器管理者
        if (sensorManager == null)
            sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
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

    }

    private void registerSensorService() {
        if (PermissionsUtil.checkPermissions(getActivity(), PermissionsUtil.BODY_SENSORS)) {
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
                            accelerometer, Sensor.TYPE_ACCELEROMETER);
                    sensorManager.registerListener(this, magnetic,
                            Sensor.TYPE_MAGNETIC_FIELD);
                }
            }
        } else {
            angleView.setText(NO_SENSOR_PERMISSION);
        }
    }

    private void onTypeChange() {
        int color = isRotatingForeground? ContextCompat.getColor(getContext(), R.color.colorPrimary): Color.GRAY;
        String str = isRotatingForeground ? "当前为指针模式" : "当前为表盘模式";
        if (isRotatingForeground) {
            backgroundView.setRotation(0);
        } else {
            foregroundView.setRotation(0);
        }
        S(str,"查看区别", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeInfo();
            }
        });
        typeChangeView.setImageDrawable(OtherUtil.tintDrawable(getContext(), R.drawable.ic_explore_black_24dp, color));
    }

    private void onModelChange() {
        int color = isOldModel ? Color.GRAY : ContextCompat.getColor(getContext(), R.color.colorPrimary);
        String str = isOldModel ? "当前为旧版API模式" : "当前为新版API模式";

        S(str,"查看区别", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModelInfo();
            }
        });
        modelChangeView.setImageDrawable(OtherUtil.tintDrawable(getContext(), R.drawable.ic_bubble_chart_black_24dp, color));
        registerSensorService();
    }

    private void on3DModelChange() {
        int color = is3DMode ? ContextCompat.getColor(getContext(), R.color.colorPrimary) : Color.GRAY;
        String str = is3DMode ? "当前为3D模式" : "当前为2D模式";

        S(str);
        model3DChangeView.setImageDrawable(OtherUtil.tintDrawable(getContext(), R.drawable.ic_3d_rotation_black_24dp, color));
        if(is3DMode){
            backgroundView.setBackgroundResource(0);
        }else{
            backgroundView.setBackgroundResource(R.drawable.bg_circle);
        }
    }

    private void showModelInfo() {
        new AlertDialog.Builder(getContext()).setTitle("API区别")
                .setMessage(R.string.api_info)
                .show();
    }

    private void showTypeInfo() {
        new AlertDialog.Builder(getContext()).setTitle("模式区别")
                .setMessage(R.string.type_info)
                .show();
    }

    private void formatText(float angle) {
        angleView.setText(getOrientation(angle));
    }

    private void rotating(float angle) {
        if(isStableMode)
            angle = (int)(angle+0.5f);
        if (isRotatingForeground) {
            foregroundView.setRotation(-angle);
        } else {
            backgroundView.setRotation(-angle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.type_change_img:
                isRotatingForeground = !isRotatingForeground;
                Settings.setRotatingForeground(getContext(),isRotatingForeground);
                onTypeChange();
                break;
            case R.id.location_text:
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    PermissionsUtil.popPermissionsDialog(getContext(), NO_LOC_PERMISSION);
                }else{
                    initLocation();
                }
                break;
            case R.id.angle_text:
                if(!PermissionsUtil.checkPermissions(getActivity(),PermissionsUtil.BODY_SENSORS)){
                    PermissionsUtil.checkPermissions(getActivity(), REQUEST_SENSOR, PermissionsUtil.BODY_SENSORS);
                }else{
                    registerSensorService();
                }
                break;
            case R.id.model_change_img:
                isOldModel = !isOldModel;
                Settings.setOldModel(getContext(),isOldModel);
                onModelChange();
                break;
            case R.id.setting_img:
//                startActivity(new Intent(this,SettingActivity.class), Pair.create((View)backgroundView,"DIAL"),Pair.create((View)foregroundView,"POINTER"));
                startActivity(new Intent(getContext(),SettingActivity.class));
                break;
            case R.id.model_3d_change_img:
                is3DMode = !is3DMode;
                Settings.set3DMode(getContext(),is3DMode);
                on3DModelChange();
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (isOldModel) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION)
                orientationValues = sensorEvent.values;
        } else {
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
        formatText(orientationValues[0]);
        rotating(orientationValues[0]);
        onPitchRollChange(orientationValues[1],orientationValues[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch (i) {
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                statusDrawable.setColor(ContextCompat.getColor(getContext(), R.color.sensorStatusUnreliable));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                statusDrawable.setColor(ContextCompat.getColor(getContext(), R.color.sensorStatusAccuracyLow));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                statusDrawable.setColor(ContextCompat.getColor(getContext(), R.color.sensorStatusAccuracyMedium));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                statusDrawable.setColor(ContextCompat.getColor(getContext(), R.color.sensorStatusAccuracyHigh));
                break;
        }
    }

    private String getOrientation(float angle) {
        //倒退5度，然后分成360份
        String orien = "";
        if (angle > -1 && angle < 1) {
            orien = "正北" + format(angle);
        } else if (angle > 1 && angle < 45) {
            orien = "北偏东" + format(angle);
        } else if (angle > 45 && angle < 89) {
            orien = "东偏北" + format(90 - angle);
        } else if (angle > 89 && angle < 91) {
            orien = "正东" + format(angle - 90);
        } else if (angle > 91 && angle < 135) {
            orien = "东偏南" + format(angle - 90);
        } else if (angle > 135 && angle < 179) {
            orien = "南偏东" + format(180 - angle);
        } else if (angle > 179 || angle < -179) {
            orien = "正南" + format(angle - 180);
        } else if (angle < -1 && angle > -45) {
            orien = "北偏西" + format(-angle);
        } else if (angle < -45 && angle > -89) {
            orien = "西偏北" + format(90 + angle);
        } else if (angle < -89 && angle > -91) {
            orien = "正西" + format(-angle - 90);
        } else if (angle < -91 && angle > -134) {
            orien = "西偏南" + format(-angle - 90);
        } else if (angle < -136 && angle > -179) {
            orien = "南偏西" + format(180 + angle);
        }
        return orien;
    }

    private String format(double value) {
        return decimalFormat.format(value);
    }

    /**
     * 更新位置信息
     */
    private void updateLocation(Location location) {
        if (null == location) {
            locationView.setText("无位置信息");
            return;
        }
        StringBuilder sb = new StringBuilder();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        double altitude = location.getAltitude();

        sb.append(latitude>= 0.0f?"北纬:":"南纬:");
        sb.append(String.valueOf(Math.abs(latitude)));
        sb.append("\n");
        sb.append(longitude>= 0.0f?"东经:":"西经:");
        sb.append(String.valueOf(Math.abs(longitude)));
        sb.append("\n海拔:");
        sb.append(String.valueOf(Math.abs(altitude)));
        locationView.setText(sb.toString());
    }

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationView.setText(NO_LOC_PERMISSION);
            return;
        }
        if(locationManager!=null)
            locationManager.removeUpdates(this);
        if (locationManager == null)
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();// 条件对象，即指定条件过滤获得LocationProvider
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 较高精度
        criteria.setAltitudeRequired(true);// 是否需要高度信息
        criteria.setBearingRequired(true);// 是否需要方向信息
        criteria.setCostAllowed(true);// 是否产生费用
        criteria.setPowerRequirement(Criteria.POWER_LOW);// 设置低电耗
        String locationProviderName = locationManager.getBestProvider(criteria, true);

        if (TextUtils.isEmpty(locationProviderName)) {
            locationView.setText("未找到位置提供装置，请检查是否开启定位,点击重试");
            return;
        }
        updateLocation(locationManager.getLastKnownLocation(locationProviderName));
        locationManager.requestLocationUpdates(locationProviderName, 2000, 10, this);// 2秒或者距离变化10米时更新一次地理位置
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(LocationProvider.OUT_OF_SERVICE==status){
            locationView.setText("位置服务已断开,点击重试");
            return;
        }
        if(LocationProvider.TEMPORARILY_UNAVAILABLE==status){
            locationView.setText("位置服务暂不可用,点击重试");
            return;
        }
        if(LocationProvider.AVAILABLE==status){
            S("位置服务已恢复运行");
            initLocation();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                initLocation();
                break;
            case REQUEST_SENSOR:
                if (PermissionsUtil.checkPermissions(getActivity(), PermissionsUtil.BODY_SENSORS)) {
                    registerSensorService();
                } else {
                    PermissionsUtil.popPermissionsDialog(getActivity(), NO_SENSOR_PERMISSION);
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N_MR1||Settings.isShowSettingBtn(getContext())){
            settingBtn.setVisibility(View.VISIBLE);
            settingBtn.setOnClickListener(this);
        }
        registerSensorService();
        initLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        // 解除注册
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        if(locationManager!=null)
            locationManager.removeUpdates(this);
    }

    //当手机XY轴变化时，得到回调，修改指南针的XY轴旋转，说到模拟3D效果
    private void onPitchRollChange(float x,float y){
        if(is3DMode){
//            if(pivotX<0)
//                pivotX = dialView.getWidth()*0.5f;
//            if(pivotY<0)
//                pivotY = dialView.getHeight()*0.5f;
//            dialView.setPivotX(pivotX);
//            dialView.setPivotY(pivotY);
            backgroundView.setRotationX(-x);
            backgroundView.setRotationY(y);
            foregroundView.setRotationX(-x);
            foregroundView.setRotationY(y);
        }else{
            backgroundView.setRotationX(0);
            backgroundView.setRotationY(0);
            foregroundView.setRotationX(0);
            foregroundView.setRotationY(0);
        }
    }

}
