package com.liang.lollipop.lcompass.fragment;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.liang.lollipop.lcompass.R;
import com.liang.lollipop.lcompass.drawable.CircleBgDrawable;
import com.liang.lollipop.lcompass.drawable.DialDrawable;
import com.liang.lollipop.lcompass.drawable.PointerDrawable;
import com.liang.lollipop.lcompass.util.OtherUtil;
import com.liang.lollipop.lcompass.util.PermissionsUtil;
import com.liang.lollipop.lcompass.util.Settings;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Lollipop on 2017/08/15.
 * 指南针分页
 */
public class CompassFragment extends BaseFragment{
    //表盘View
    private View dialView;
    //背景
    private ImageView rootBgView;
    //背景View
    private ImageView dialImageView;
    //背景渲染Drawable
    private DialDrawable dialDrawable;
    //前景View
    private ImageView pointerImageView;
    //前景渲染Drawable
    private PointerDrawable pointerDrawable;
    //角度的文本
    private TextView angleView;
    //圆形的渲染
    private CircleBgDrawable statusDrawable;
    //位置显示的View
    private TextView locationView;
    //压力信息的显示View
    private TextView pressureView;

    //浮点数的格式化
    private DecimalFormat decimalFormat;

    //是否渲染前景
    private boolean isRotatingForeground;
    //是否启用稳定模式
    private boolean isStableMode;

    private boolean is3DMode;
    //检查读取内存卡权限的ID
    private static final int CHECK_WRITE_SD = 789;

    @Override
    public void onResume() {
        super.onResume();
        isRotatingForeground = Settings.isRotatingForeground(getContext());
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
        tryGetTypeface();
    }

    private void tryGetTypeface(){
        PermissionsUtil.checkPermissions(getActivity(), CHECK_WRITE_SD, new PermissionsUtil.OnPermissionsPass() {
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
                if(PermissionsUtil.checkPermission(getContext(),PermissionsUtil.WRITE_EXTERNAL_STORAGE)){
                    getTypeface();
                }else{
                    PermissionsUtil.popPermissionsDialog(getContext(),getString(R.string.no_write_sd_with_fonts));
                }
                break;
        }
    }

    private void initView(View root) {
        rootBgView = (ImageView) root.findViewById(R.id.background_body);
        dialImageView = (ImageView) root.findViewById(R.id.background_img);
        pointerImageView = (ImageView) root.findViewById(R.id.foreground_img);
        angleView = (TextView) root.findViewById(R.id.angle_text);
        locationView = (TextView) root.findViewById(R.id.location_text);
        pressureView = (TextView) root.findViewById(R.id.pressure_text);

        ImageView statusView = (ImageView) root.findViewById(R.id.status_img);
        statusView.setImageDrawable(statusDrawable = new CircleBgDrawable());

        dialImageView.setImageDrawable(dialDrawable = new DialDrawable());
        pointerImageView.setImageDrawable(pointerDrawable = new PointerDrawable());

        dialView = root.findViewById(R.id.dial_view);

        decimalFormat = new DecimalFormat("#0.00000");
    }

    private void initDial(){
        dialDrawable.setTextColor(Settings.dialTextColor(getContext()));
        dialDrawable.setBgColor(Settings.dialBgColor(getContext()));
        dialDrawable.setScaleColor(Settings.dialScaleColor(getContext()));
        dialDrawable.setChinase(Settings.isChinaseScale(getContext()));
        pointerDrawable.setBgColor(Settings.pointerBgColor(getContext()));
        pointerDrawable.setColor(Settings.pointerColor(getContext()));
        rootBgView.setBackgroundColor(Settings.rootBgColor(getContext()));
        locationView.setTextColor(Settings.locationTextColor(getContext()));
        pressureView.setTextColor(Settings.locationTextColor(getContext()));
        angleView.setTextColor(Settings.locationTextColor(getContext()));
        if(Settings.isShowRootBgImg(getContext())){
            glide.load(Settings.getRootBgImg(getContext())).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(rootBgView);
        }else{
            rootBgView.setImageDrawable(null);
        }
        dialDrawable.setShowBitmap(Settings.isShowDialBgImg(getContext()));
        glide.load(Settings.getDialBgImg(getContext())).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                dialDrawable.setBitmap(resource);
            }
        });
        pointerDrawable.setShowBitmap(Settings.isShowPointerBgImg(getContext()));
        glide.load(Settings.getPointerBgImg(getContext())).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                pointerDrawable.setBitmap(resource);
            }
        });

        if(Settings.isOverflowAvoid(getContext())&&is3DMode){
            dialDrawable.setScaleColor(0);
        }else{
            dialDrawable.setScaleColor(Settings.dialScaleColor(getContext()));
        }
    }

    @Override
    public void onTypeChange(boolean b) {
        isRotatingForeground = b;
        if (b) {
            dialImageView.setRotation(0);
        } else {
            pointerImageView.setRotation(0);
        }
    }

    @Override
    public void on3DModelChange(boolean b) {
        is3DMode = b;
        dialImageView.setBackgroundResource(b?0:R.drawable.bg_circle);
        if(Settings.isOverflowAvoid(getContext())&&b){
            dialDrawable.setScaleColor(0);
        }else{
            dialDrawable.setScaleColor(Settings.dialScaleColor(getContext()));
        }
    }

    private void formatText(float angle) {
        angleView.setText(getOrientation(angle));
    }

    private void rotating(float angle) {
        if(isStableMode)
            angle = (int)(angle+0.5f);
        if (isRotatingForeground) {
            pointerImageView.setRotation(-angle);
        } else {
            dialImageView.setRotation(-angle);
        }
    }

    @Override
    public void onSensorUpdate(float z,float x,float y) {
        formatText(z);
        rotating(z);
        onPitchRollChange(x,y);
    }

    @Override
    public void onSensorStateUpdate(int i) {
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
            orien = getString(R.string.north) + format(angle);
        } else if (angle > 1 && angle < 45) {
            orien = getString(R.string.north_east) + format(angle);
        } else if (angle > 45 && angle < 89) {
            orien = getString(R.string.east_north) + format(90 - angle);
        } else if (angle > 89 && angle < 91) {
            orien = getString(R.string.east) + format(angle - 90);
        } else if (angle > 91 && angle < 135) {
            orien = getString(R.string.east_south) + format(angle - 90);
        } else if (angle > 135 && angle < 179) {
            orien = getString(R.string.south_east) + format(180 - angle);
        } else if (angle > 179 || angle < -179) {
            orien = getString(R.string.south) + format(angle - 180);
        } else if (angle < -1 && angle > -45) {
            orien = getString(R.string.north_west) + format(-angle);
        } else if (angle < -45 && angle > -89) {
            orien = getString(R.string.west_north) + format(90 + angle);
        } else if (angle < -89 && angle > -91) {
            orien = getString(R.string.west) + format(-angle - 90);
        } else if (angle < -91 && angle > -134) {
            orien = getString(R.string.west_south) + format(-angle - 90);
        } else if (angle < -136 && angle > -179) {
            orien = getString(R.string.south_west) + format(180 + angle);
        }
        return orien;
    }

    private String format(double value) {
        return decimalFormat.format(value);
    }

    /**
     * 更新位置信息
     */
    @Override
    public void onLocationUpdate(Location location) {
        super.onLocationUpdate(location);
        if (null == location) {
            locationView.setText("");
            if(locationView.getVisibility()!=View.GONE)
                locationView.setVisibility(View.GONE);
            return;
        }else{
            if(locationView.getVisibility()!=View.VISIBLE)
                locationView.setVisibility(View.VISIBLE);
        }
        StringBuilder sb = new StringBuilder();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        sb.append(latitude>= 0.0f?getString(R.string.north_latitude):getString(R.string.south_latitude));
        sb.append(String.valueOf(Math.abs(latitude)));
        sb.append("\n");
        sb.append(longitude>= 0.0f?getString(R.string.east_longitude):getString(R.string.west_longitude));
        sb.append(String.valueOf(Math.abs(longitude)));
        if(location.hasAccuracy()){
            //如果有精度描述
            float accuracy = location.getAccuracy();
            sb.append("\n");
            sb.append(getString(R.string.accuracy));
            sb.append(String.valueOf(Math.abs(accuracy)));
            sb.append(getString(R.string.meter));
        }
        if(location.hasAltitude()){
            //如果有海拔描述
            sb.append("\n");
            sb.append(getString(R.string.altitude));
            sb.append(String.valueOf(Math.abs(location.getAltitude())));
            sb.append(getString(R.string.meter));
        }
        if(location.hasBearing()){
            //如果有方向
        }
        if(location.hasSpeed()){
            //如果有速度
            sb.append("\n");
            sb.append(getString(R.string.speed));
            sb.append(String.valueOf(Math.abs(location.getSpeed())));
            sb.append(getString(R.string.meters_per_second));
        }

        locationView.setText(sb.toString());
    }

    @Override
    public void onPressureChange(float hPa, float altitude) {
        super.onPressureChange(hPa, altitude);
        if(pressureView.getVisibility()!=View.VISIBLE)
            pressureView.setVisibility(View.VISIBLE);
        String str = locationView.getText().toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.pressure));
        stringBuilder.append(decimalFormat.format(hPa));
        stringBuilder.append(getString(R.string.hpa));
        if(!str.contains(getString(R.string.altitude))){
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.altitude));
            stringBuilder.append(decimalFormat.format(altitude));
            stringBuilder.append(getString(R.string.meter));
        }
        pressureView.setText(stringBuilder.toString());
    }

    //当手机XY轴变化时，得到回调，修改指南针的XY轴旋转，说到模拟3D效果
    private void onPitchRollChange(float x,float y){
        if(is3DMode){
            dialImageView.setRotationX(-x);
            dialImageView.setRotationY(y);
            pointerImageView.setRotationX(-x);
            pointerImageView.setRotationY(y);
        }else{
            dialImageView.setRotationX(0);
            dialImageView.setRotationY(0);
            pointerImageView.setRotationX(0);
            pointerImageView.setRotationY(0);
        }
    }

    @Override
    public void onCameraOpened() {
        super.onCameraOpened();
        dialView.setVisibility(View.INVISIBLE);
        rootBgView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCameraClosed() {
        super.onCameraClosed();
        dialView.setVisibility(View.VISIBLE);
        rootBgView.setVisibility(View.VISIBLE);
    }
}
