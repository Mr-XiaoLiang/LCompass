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

    //浮点数的格式化
    private DecimalFormat decimalFormat;

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
    //检查读取内存卡权限的ID
    private static final int CHECK_WRITE_SD = 789;

    @Override
    public void onResume() {
        super.onResume();
        isRotatingForeground = Settings.isRotatingForeground(getContext());
        isOldModel = Settings.oldModel(getContext());
        isStableMode = Settings.isStableMode(getContext());
        is3DMode = Settings.is3DMode(getContext());
        isCameraMode = Settings.isCameraMode(getContext());
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
                    PermissionsUtil.popPermissionsDialog(getContext(),
                            "您未授权读写储存空间，应用将无法获取储存空间中的自定义字体，您将使用系统默认的字体。" +
                                    "\n开启后，重启应用生效。是否手动开启？");
                }
                break;
        }
    }

    private void initView(View root) {
        rootBgView = (ImageView) root.findViewById(R.id.background_body);
        dialImageView = (ImageView) root.findViewById(R.id.background_img);
        pointerImageView = (ImageView) root.findViewById(R.id.foreground_img);
        angleView = (TextView) root.findViewById(R.id.angle_text);
        angleView.setOnClickListener(this);
        locationView = (TextView) root.findViewById(R.id.location_text);
        locationView.setOnClickListener(this);

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
        angleView.setTextColor(Settings.locationTextColor(getContext()));
        if(Settings.isShowRootBgImg(getContext())){
            glide.load(OtherUtil.getBackground(getContext())).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(rootBgView);
        }else{
            rootBgView.setImageDrawable(null);
        }
        dialDrawable.setShowBitmap(Settings.isShowDialBgImg(getContext()));
        glide.load(OtherUtil.getDialBackground(getContext())).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                dialDrawable.setBitmap(resource);
            }
        });
        pointerDrawable.setShowBitmap(Settings.isShowPointerBgImg(getContext()));
        glide.load(OtherUtil.getPointerBackground(getContext())).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                pointerDrawable.setBitmap(resource);
            }
        });
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
    public void onModelChange(boolean b) {
        isOldModel = b;
    }

    @Override
    public void on3DModelChange(boolean b) {
        is3DMode = b;
        if(b){
            dialImageView.setBackgroundResource(0);
        }else{
            dialImageView.setBackgroundResource(R.drawable.bg_circle);
        }
    }

    @Override
    public void onCameraModelChange(boolean b) {
        isCameraMode = b;
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
    @Override
    public void onLocationUpdate(Location location) {
        super.onLocationUpdate(location);
        if (null == location) {
            locationView.setText("无位置信息");
            return;
        }
        StringBuilder sb = new StringBuilder();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        sb.append(latitude>= 0.0f?"北纬:":"南纬:");
        sb.append(String.valueOf(Math.abs(latitude)));
        sb.append("\n");
        sb.append(longitude>= 0.0f?"东经:":"西经:");
        sb.append(String.valueOf(Math.abs(longitude)));
        if(location.hasAccuracy()){
            //如果有精度描述
            location.getAccuracy();
            sb.append("\n精度:");
            sb.append(String.valueOf(Math.abs(location.getAccuracy())));
            sb.append("m");
        }
        if(location.hasAltitude()){
            //如果有海拔描述
            sb.append("\n海拔:");
            sb.append(String.valueOf(Math.abs(location.getAltitude())));
        }
        if(location.hasBearing()){
            //如果有方向
        }
        if(location.hasSpeed()){
            //如果有速度
            sb.append("\n速度:");
            sb.append(String.valueOf(Math.abs(location.getSpeed())));
            sb.append("m/s");
        }

        locationView.setText(sb.toString());
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
