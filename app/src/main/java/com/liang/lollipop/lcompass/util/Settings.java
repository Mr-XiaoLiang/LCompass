package com.liang.lollipop.lcompass.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.liang.lollipop.lcompass.R;
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

    private static final String CHINASE_SCALE  = "CHINASE_SCALE";

    public static boolean isRotatingForeground(Context context){
        return get(context,context.getString(R.string.key_rotating_foreground),true);
    }

    public static void setRotatingForeground(Context context,boolean b){
        put(context,context.getString(R.string.key_rotating_foreground),b);
    }

    public static boolean oldModel(Context context){
        return get(context,context.getString(R.string.key_old_api),false);
    }

    public static void setOldModel(Context context,boolean b){
        put(context,context.getString(R.string.key_old_api),b);
    }

    public static int dialBgColor(Context context){
        return get(context,context.getString(R.string.key_dial_bg_color), Color.WHITE);
    }

    public static int dialScaleColor(Context context){
        return get(context,context.getString(R.string.key_dial_scale_color), Color.GRAY);
    }

    public static int dialTextColor(Context context){
        return get(context,context.getString(R.string.key_dial_text_color), Color.GRAY);
    }

    public static int pointerBgColor(Context context){
        return get(context,context.getString(R.string.key_pointer_bg_color), Color.TRANSPARENT);
    }

    public static int pointerColor(Context context){
        return get(context,context.getString(R.string.key_pointer_color), Color.GRAY);
    }

    public static int rootBgColor(Context context){
        return get(context,context.getString(R.string.key_root_bg_color), ContextCompat.getColor(context, R.color.background));
    }

    public static int locationTextColor(Context context){
        return get(context,context.getString(R.string.key_location_text_color), Color.GRAY);
    }

    public static boolean isShowRootBgImg(Context context){
        return get(context,context.getString(R.string.key_root_bg_image_state), true);
    }

    public static boolean isShowDialBgImg(Context context){
        return get(context,context.getString(R.string.key_dial_bg_image_state), true);
    }

    public static boolean isShowPointerBgImg(Context context){
        return get(context,context.getString(R.string.key_pointer_bg_image_state), true);
    }

    public static boolean isShowSettingBtn(Context context){
        return get(context,context.getString(R.string.key_show_setting_btn), true);
    }

    public static boolean isStableMode(Context context){
        return get(context,context.getString(R.string.key_stable_mode), true);
    }

    public static boolean is3DMode(Context context){
        return get(context,context.getString(R.string.key_3d_mode), true);
    }

    public static void set3DMode(Context context,boolean b){
        put(context,context.getString(R.string.key_3d_mode),b);
    }

    public static boolean isCameraMode(Context context){
        return get(context,context.getString(R.string.key_camera_mode), true);
    }

    public static void setCameraMode(Context context,boolean b){
        put(context,context.getString(R.string.key_camera_mode),b);
    }

    public static boolean isChinaseScale(Context context){
        return get(context,context.getString(R.string.key_chinese_mode), true);
    }

    public static boolean isOverflowAvoid(Context context){
        return get(context,context.getString(R.string.key_overflow_avoid), true);
    }

    public static String getRootBgImg(Context context){
        return get(context,context.getString(R.string.key_root_bg_image), "");
    }

    public static String getDialBgImg(Context context){
        return get(context,context.getString(R.string.key_dial_bg_image), "");
    }

    public static String getPointerBgImg(Context context){
        return get(context,context.getString(R.string.key_pointer_bg_image), "");
    }


    public static void saveString(Context context,String key,String value){
        put(context,key,value);
    }

    private static <T> void put(Context context,String key,T value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferencesUtils.put(sharedPreferences,key,value);
    }

    private static <T> T get(Context context,String key,T value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return SharedPreferencesUtils.get(sharedPreferences,key,value);
    }

    public static void cloneSetting(Context context){
        put(context,context.getString(R.string.key_rotating_foreground),SPUGet(context,ROTATING_FOREGROUND,true));
        put(context,context.getString(R.string.key_old_api),SPUGet(context,OLD_MODEL,false));
        put(context,context.getString(R.string.key_dial_bg_color),SPUGet(context,DIAL_BG_COLOR,Color.WHITE));
        put(context,context.getString(R.string.key_dial_scale_color),SPUGet(context,DIAL_SCALE_COLOR,Color.GRAY));
        put(context,context.getString(R.string.key_dial_text_color),SPUGet(context,DIAL_TEXT_COLOR,Color.GRAY));
        put(context,context.getString(R.string.key_pointer_bg_color),SPUGet(context,POINTER_BG_COLOR,Color.TRANSPARENT));
        put(context,context.getString(R.string.key_pointer_color),SPUGet(context,POINTER_COLOR,Color.GRAY));
        put(context,context.getString(R.string.key_root_bg_color),SPUGet(context,ROOT_BG_COLOR,ContextCompat.getColor(context, R.color.background)));
        put(context,context.getString(R.string.key_location_text_color),SPUGet(context,LOCATION_TEXT_COLOR,ContextCompat.getColor(context, R.color.colorPrimary)));
        put(context,context.getString(R.string.key_root_bg_image_state),SPUGet(context,ROOT_BG_IMG_IS_SHOW,true));
        put(context,context.getString(R.string.key_pointer_bg_image_state),SPUGet(context,POINTER_BG_IMG_IS_SHOW,true));
        put(context,context.getString(R.string.key_dial_bg_image_state),SPUGet(context,DIAL_BG_IMG_IS_SHOW,true));
        put(context,context.getString(R.string.key_show_setting_btn),SPUGet(context,SETTING_SHOW_SET,true));
        put(context,context.getString(R.string.key_stable_mode),SPUGet(context,STABLE_MODE,true));
        put(context,context.getString(R.string.key_3d_mode),SPUGet(context,MODE_3D,true));
        put(context,context.getString(R.string.key_camera_mode),SPUGet(context,CAMERA_MODE,true));
        put(context,context.getString(R.string.key_chinese_mode),SPUGet(context,CHINASE_SCALE,true));
        put(context,context.getString(R.string.key_root_bg_image),OtherUtil.getBackground(context));
        put(context,context.getString(R.string.key_pointer_bg_image),OtherUtil.getPointerBackground(context));
        put(context,context.getString(R.string.key_dial_bg_image),OtherUtil.getDialBackground(context));
    }

    private static <T> T SPUGet(Context context,String key,T d){
        return SharedPreferencesUtils.get(context,key,d);
    }

    public static void checkFirstOpen(final Context context){
        if(get(context,"FirstOpen",true)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.dialog_title_hint)
                    .setMessage(context.getString(R.string.dialog_message_clone))
                    .setNegativeButton(R.string.dialog_btn_no_remind, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            put(context,"FirstOpen",false);
                            dialog.dismiss();
                        }
                    })
            .setPositiveButton(R.string.dialog_btn_clone, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Settings.cloneSetting(context);
                    put(context,"FirstOpen",false);
                    dialog.dismiss();
                }
            }).setNeutralButton(R.string.dialog_btn_remind, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

}
