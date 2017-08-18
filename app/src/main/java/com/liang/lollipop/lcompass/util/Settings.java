package com.liang.lollipop.lcompass.util;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.liang.lollipop.lcompass.R;

import static com.liang.lollipop.lcompass.util.SharedPreferencesUtils.get;
import static com.liang.lollipop.lcompass.util.SharedPreferencesUtils.put;

/**
 * Created by Lollipop on 2017/08/15.
 * 个人偏好设置
 */
public class Settings {

    private static final String ROTATING_FOREGROUND = "ROTATING_FOREGROUND";
    private static final String OLD_MODEL = "OLD_MODEL";

    private static final String DIAL_BG_COLOR  = "DIAL_BG_COLOR";
    private static final String DIAL_SCALE_COLOR  = "DIAL_SCALE_COLOR";
    private static final String DIAL_TEXT_COLOR  = "DIAL_TEXT_COLOR";
    private static final String POINTER_BG_COLOR = "POINTER_BG_COLOR";
    private static final String POINTER_COLOR  = "POINTER_COLOR";
    private static final String ROOT_BG_COLOR  = "ROOT_BG_COLOR";
    private static final String LOCATION_TEXT_COLOR  = "LOCATION_TEXT_COLOR";

    private static final String ROOT_BG_IMG_IS_SHOW  = "ROOT_BG_IMG_IS_SHOW";
    private static final String POINTER_BG_IMG_IS_SHOW  = "POINTER_BG_IMG_IS_SHOW";
    private static final String DIAL_BG_IMG_IS_SHOW  = "DIAL_BG_IMG_IS_SHOW";

    private static final String SETTING_SHOW_SET  = "SETTING_SHOW_SET";

    private static final String STABLE_MODE  = "STABLE_MODE";

    private static final String MODE_3D  = "MODE_3D";

    private static final String CAMERA_MODE  = "CAMERA_MODE";

    public static boolean isRotatingForeground(Context context){
        return get(context,ROTATING_FOREGROUND,true);
    }

    public static void setRotatingForeground(Context context,boolean b){
        put(context,ROTATING_FOREGROUND,b);
    }

    public static boolean oldModel(Context context){
        return get(context,OLD_MODEL,false);
    }

    public static void setOldModel(Context context,boolean b){
        put(context,OLD_MODEL,b);
    }

    public static int dialBgColor(Context context){
        return get(context,DIAL_BG_COLOR, Color.WHITE);
    }

    public static void setDialBgColor(Context context,int b){
        put(context,DIAL_BG_COLOR,b);
    }

    public static int dialScaleColor(Context context){
        return get(context,DIAL_SCALE_COLOR, Color.GRAY);
    }

    public static void setDialScaleColor(Context context,int b){
        put(context,DIAL_SCALE_COLOR,b);
    }

    public static int dialTextColor(Context context){
        return get(context,DIAL_TEXT_COLOR, Color.GRAY);
    }

    public static void setDialTextColor(Context context,int b){
        put(context,DIAL_TEXT_COLOR,b);
    }

    public static int pointerBgColor(Context context){
        return get(context,POINTER_BG_COLOR, Color.TRANSPARENT);
    }

    public static void setPointerBgColor(Context context,int b){
        put(context,POINTER_BG_COLOR,b);
    }

    public static int pointerColor(Context context){
        return get(context,POINTER_COLOR, Color.GRAY);
    }

    public static void setPointerColor(Context context,int b){
        put(context,POINTER_COLOR,b);
    }

    public static int rootBgColor(Context context){
        return get(context,ROOT_BG_COLOR, ContextCompat.getColor(context, R.color.background));
    }

    public static void setRootBgColor(Context context,int b){
        put(context,ROOT_BG_COLOR,b);
    }

    public static int locationTextColor(Context context){
        return get(context,LOCATION_TEXT_COLOR, ContextCompat.getColor(context, R.color.colorPrimary));
    }

    public static void setLocationTextColor(Context context,int b){
        put(context,LOCATION_TEXT_COLOR,b);
    }

    public static boolean isShowRootBgImg(Context context){
        return get(context,ROOT_BG_IMG_IS_SHOW, false);
    }

    public static void setShowRootBgImg(Context context,boolean b){
        put(context,ROOT_BG_IMG_IS_SHOW,b);
    }

    public static boolean isShowDialBgImg(Context context){
        return get(context,DIAL_BG_IMG_IS_SHOW, false);
    }

    public static void setShowDialBgImg(Context context, boolean b){
        put(context,DIAL_BG_IMG_IS_SHOW,b);
    }

    public static boolean isShowPointerBgImg(Context context){
        return get(context,POINTER_BG_IMG_IS_SHOW, false);
    }

    public static void setShowPointerBgImg(Context context,boolean b){
        put(context,POINTER_BG_IMG_IS_SHOW,b);
    }

    public static boolean isShowSettingBtn(Context context){
        return get(context,SETTING_SHOW_SET, true);
    }

    public static void setShowSettingBtn(Context context,boolean b){
        put(context,SETTING_SHOW_SET,b);
    }

    public static boolean isStableMode(Context context){
        return get(context,STABLE_MODE, true);
    }

    public static void setStableMode(Context context,boolean b){
        put(context,STABLE_MODE,b);
    }

    public static boolean is3DMode(Context context){
        return get(context,MODE_3D, true);
    }

    public static void set3DMode(Context context,boolean b){
        put(context,MODE_3D,b);
    }

    public static boolean isCameraMode(Context context){
        return get(context,CAMERA_MODE, true);
    }

    public static void setCameraMode(Context context,boolean b){
        put(context,CAMERA_MODE,b);
    }

}
