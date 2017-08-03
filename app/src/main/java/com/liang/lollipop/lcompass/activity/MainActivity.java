package com.liang.lollipop.lcompass.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.drawable.CircleBgDrawable;
import com.liang.lollipop.lcompass.drawable.DialDrawable;
import com.liang.lollipop.lcompass.drawable.PointerDrawable;
import com.liang.lollipop.lcompass.util.OtherUtil;
import com.liang.lollipop.lcompass.util.PermissionsUtil;
import com.liang.lollipop.lcompass.util.SharedPreferencesUtils;

import java.text.DecimalFormat;

/**
 * 主页
 */
public class MainActivity extends BaseActivity implements SensorEventListener, LocationListener {

    private static final int REQUEST_LOCATION = 23;
    private static final int REQUEST_SENSOR = 24;

    //背景
    private View rootBgView;
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

    //传感器管理器
    private SensorManager sensorManager;
    // 加速度传感器
    private Sensor accelerometer;
    // 地磁场传感器
    private Sensor magnetic;
    // 老版的定位API
    private Sensor oldOrientation;
    //是否渲染前景
    private boolean isRotatingForeground;
    //老版模式
    private boolean isOldModel;


    // 加速度
    private float[] accelerometerValues = new float[3];
    // 地磁场
    private float[] magneticFieldValues = new float[3];
    //浮点数的格式化
    private DecimalFormat decimalFormat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        initView();
        decimalFormat = new DecimalFormat("#0.000");
        PermissionsUtil.checkPermissions(this, REQUEST_LOCATION, PermissionsUtil.ACCESS_FINE_LOCATION);
        PermissionsUtil.checkPermissions(this, REQUEST_SENSOR, PermissionsUtil.BODY_SENSORS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRotatingForeground = SharedPreferencesUtils.isRotatingForeground(this);
        isOldModel = SharedPreferencesUtils.oldModel(this);
        onTypeChange();
        onModelChange();
        initDial();
    }

    private void initView() {

        rootBgView = findViewById(R.id.background_body);
        backgroundView = (ImageView) findViewById(R.id.background_img);
        foregroundView = (ImageView) findViewById(R.id.foreground_img);
        angleView = (TextView) findViewById(R.id.angle_text);
        angleView.setOnClickListener(this);
        locationView = (TextView) findViewById(R.id.location_text);
        locationView.setOnClickListener(this);

        ImageView statusView = (ImageView) findViewById(R.id.status_img);
        statusView.setImageDrawable(statusDrawable = new CircleBgDrawable());
        typeChangeView = (ImageView) findViewById(R.id.type_change_img);
        typeChangeView.setOnClickListener(this);

        modelChangeView = (ImageView) findViewById(R.id.model_change_img);
        modelChangeView.setOnClickListener(this);

        backgroundView.setImageDrawable(backgroundDrawable = new DialDrawable());
        foregroundView.setImageDrawable(foregroundDrawable = new PointerDrawable());
        initDial();

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N_MR1){
            View setting = findViewById(R.id.setting_img);
            setting.setVisibility(View.VISIBLE);
            setting.setOnClickListener(this);
        }
    }

    private void initDial(){
        backgroundDrawable.setTextColor(SharedPreferencesUtils.dialTextColor(this));
        backgroundDrawable.setBgColor(SharedPreferencesUtils.dialBgColor(this));
        backgroundDrawable.setScaleColor(SharedPreferencesUtils.dialScaleColor(this));
        foregroundDrawable.setBgColor(SharedPreferencesUtils.pointerBgColor(this));
        foregroundDrawable.setColor(SharedPreferencesUtils.pointerColor(this));
        rootBgView.setBackgroundColor(SharedPreferencesUtils.rootBgColor(this));
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
                sensorManager.registerListener(this,
                        accelerometer, Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, magnetic,
                        Sensor.TYPE_MAGNETIC_FIELD);
            }
        } else {
            angleView.setText(NO_SENSOR_PERMISSION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerSensorService();
        initLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 解除注册
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        if(locationManager!=null)
            locationManager.removeUpdates(this);
    }

    private void formatText(float angle) {
        angleView.setText(getOrientation(angle));
    }

    private void rotating(float angle) {
        if (isRotatingForeground) {
            foregroundView.setRotation(-angle);
        } else {
            backgroundView.setRotation(-angle);
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
        }
        if (orientationValues[0] > 180) {
            //有些计量方式是360有些是180
            orientationValues[0] = (orientationValues[0] - 360);
        }
        formatText(orientationValues[0]);
        rotating(orientationValues[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        switch (i) {
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                statusDrawable.setColor(ContextCompat.getColor(this, R.color.sensorStatusUnreliable));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                statusDrawable.setColor(ContextCompat.getColor(this, R.color.sensorStatusAccuracyLow));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                statusDrawable.setColor(ContextCompat.getColor(this, R.color.sensorStatusAccuracyMedium));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                statusDrawable.setColor(ContextCompat.getColor(this, R.color.sensorStatusAccuracyHigh));
                break;
        }
    }

    private void onTypeChange() {
        int color = isRotatingForeground?ContextCompat.getColor(this, R.color.colorPrimary):Color.GRAY;
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
        typeChangeView.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_explore_black_24dp, color));
    }

    private void onModelChange() {
        int color = isOldModel ? Color.GRAY : ContextCompat.getColor(this, R.color.colorPrimary);
        String str = isOldModel ? "当前为旧版API模式" : "当前为新版API模式";

        S(str,"查看区别", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModelInfo();
            }
        });
        modelChangeView.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_bubble_chart_black_24dp, color));
        registerSensorService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.type_change_img:
                isRotatingForeground = !isRotatingForeground;
                SharedPreferencesUtils.setRotatingForeground(this,isRotatingForeground);
                onTypeChange();
                break;
            case R.id.location_text:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    PermissionsUtil.popPermissionsDialog(this, NO_LOC_PERMISSION);
                }else{
                    initLocation();
                }
                break;
            case R.id.angle_text:
                if(!PermissionsUtil.checkPermissions(this,PermissionsUtil.BODY_SENSORS)){
                    PermissionsUtil.checkPermissions(this, REQUEST_SENSOR, PermissionsUtil.BODY_SENSORS);
                }else{
                    registerSensorService();
                }
                break;
            case R.id.model_change_img:
                isOldModel = !isOldModel;
                SharedPreferencesUtils.setOldModel(this,isOldModel);
                onModelChange();
                break;
            case R.id.setting_img:
//                startActivity(new Intent(this,SettingActivity.class), Pair.create((View)backgroundView,"DIAL"),Pair.create((View)foregroundView,"POINTER"));
                startActivity(new Intent(this,SettingActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                initLocation();
                break;
            case REQUEST_SENSOR:
                if (PermissionsUtil.checkPermissions(this, PermissionsUtil.BODY_SENSORS)) {
                    registerSensorService();
                } else {
                    PermissionsUtil.popPermissionsDialog(this, NO_SENSOR_PERMISSION);
                }
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

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationView.setText(NO_LOC_PERMISSION);
            return;
        }
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
            locationView.setText("未找到位置提供装置，请检查是否开启定位,点击重试");
            return;
        }
        updateLocation(locationManager.getLastKnownLocation(locationProviderName));
        locationManager.requestLocationUpdates(locationProviderName, 2000, 10, this);// 2秒或者距离变化10米时更新一次地理位置
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
        sb.append(format(Math.abs(latitude)));
        sb.append(" - ");
        sb.append(longitude>= 0.0f?"东经:":"西经:");
        sb.append(format(Math.abs(longitude)));
        sb.append("\n海拔:");
        sb.append(format(Math.abs(altitude)));
        locationView.setText(sb.toString());
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
}
