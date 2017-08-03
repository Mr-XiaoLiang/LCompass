package com.liang.lollipop.lcompass.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.liang.lollipop.lcompass.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * @author Lollipop
 * 2017-07-28
 */
public class SharedPreferencesUtils {

    private static  final String USER = "LCompass";

    public static <T> void put( Context context, String key, T value ) {
        if(context==null)
            return;
        if ( notNull( value ) ) {
            SharedPreferences mShareConfig =
                    context.getSharedPreferences( USER, Context.MODE_PRIVATE );
            Editor conEdit = mShareConfig.edit();
            if ( value instanceof String ) {
                conEdit.putString( key.trim(), ( (String) value ).trim() );
            } else if ( value instanceof Long ) {
                conEdit.putLong( key, (Long) value );
            } else if ( value instanceof Boolean ) {
                conEdit.putBoolean( key, (Boolean) value );
            }else if(value instanceof Integer){
                conEdit.putInt(key,  (Integer)value );
            }else if(value instanceof Float){
                conEdit.putFloat(key, (Float) value);
            }
            conEdit.commit();
        }
    }

    public static <T> T get( Context context, String key, T defValue ) {
        if(context==null)
            return null;
        T value = null;
        if ( notNull( key ) ) {
            SharedPreferences mShareConfig =
                    context.getSharedPreferences(USER, Context.MODE_PRIVATE);
            if ( null != mShareConfig ) {
                if (defValue instanceof  String) {
                    value = (T) mShareConfig.getString(key, (String)defValue);
                } else if (defValue instanceof Long) {
                    value = (T) Long.valueOf( mShareConfig.getLong(key, (Long) defValue) );
                } else if (defValue instanceof Boolean) {
                    value = (T) Boolean.valueOf( mShareConfig.getBoolean(key, (Boolean) defValue) );
                } else if (defValue instanceof Integer) {
                    value = (T) Integer.valueOf( mShareConfig.getInt(key, (Integer)defValue) );
                }else if (defValue instanceof Float) {
                    value = (T) Float.valueOf( mShareConfig.getFloat(key, (Float) defValue) );
                }
            }
        }
        return value;
    }

    /**
     * 设置一个集合
     *
     */
    public static void setMapKey(Context context,Map<String, Object> informations) {
        SharedPreferences mShareConfig =
                context.getSharedPreferences(USER, Context.MODE_PRIVATE);
        Editor configEditor = mShareConfig.edit();
        Set<Map.Entry<String, Object>> entries = informations.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            Object obj = next.getValue();
            String key = next.getKey();
            if (key != null) {
                if (obj instanceof String) {
                    configEditor.putString(key, obj.toString());
                } else if (obj instanceof Boolean) {
                    configEditor.putBoolean(key, (Boolean) obj);
                } else if (obj instanceof Integer) {
                    configEditor.putInt(key, (Integer) obj);
                }else if (obj instanceof Float) {
                    configEditor.putFloat(key, (Float) obj);
                }else if (obj instanceof Long) {
                    configEditor.putLong(key, (Long) obj);
                }
            }
        }
        configEditor.commit();
        // 用完及时清空
        informations.clear();
    }



    /**
     * object not null
     */
    public static boolean notNull( Object obj ) {
        if ( null != obj) {
            return true;
        }
        return false;
    }

//    /**
//     * save object to SharedPreferences file
//     * @param context
//     * @param clazz
//     * @param content
//     * 使用方法  serializeObject  saveObject
//     */
//    public static void saveObject(Context context,Class clazz,String content) {
//        mShareConfig =context.getSharedPreferences( USER, Context.MODE_PRIVATE );
//        Editor conEdit = mShareConfig.edit();
//        conEdit.putString(clazz.toString(), content);
//        conEdit.commit();
//    }
//
//    /**
//     * read object to memory
//     * @param context
//     * @param clazz
//     * @return
//     *  getObject deSerializationObject
//     */
//    public static String getObject(Context context,Class clazz) {
//        mShareConfig =context.getSharedPreferences( USER, Context.MODE_PRIVATE );
//        return mShareConfig.getString(clazz.toString(), null);
//    }


    /**
     * 保存实例对象
     * @param object
     * @return
     * @throws Exception
     */
    public static void saveSerializeObject(Context context,Object object){
        ByteArrayOutputStream byteArrayOutputStream = null ;
        ObjectOutputStream objectOutputStream = null;
        String serStr = "" ;
        try {

            byteArrayOutputStream = new ByteArrayOutputStream();

            objectOutputStream = new ObjectOutputStream(
                    byteArrayOutputStream);
            objectOutputStream.writeObject(object);

            serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");

            SharedPreferences mShareConfig =context.getSharedPreferences( USER, Context.MODE_PRIVATE );
            Editor conEdit = mShareConfig.edit();
            conEdit.putString(object.getClass().toString(), serStr);
            conEdit.commit();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(objectOutputStream != null){
                    objectOutputStream.close();
                }
                if(byteArrayOutputStream != null){
                    byteArrayOutputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取实例对象
     * @param clazz 对象的实例
     */
    public static Object getSerializationObject(Context context,Class clazz) {
        ByteArrayInputStream byteArrayInputStream = null ;
        ObjectInputStream objectInputStream = null ;
        Object object = null ;
        SharedPreferences mShareConfig =context.getSharedPreferences(USER, Context.MODE_PRIVATE );
        String str = mShareConfig.getString(clazz.toString(), null);
        if(TextUtils.isEmpty(str)){
            return object;
        }
        try{
            String redStr = java.net.URLDecoder.decode(str, "UTF-8");
            byteArrayInputStream = new ByteArrayInputStream(
                    redStr.getBytes("ISO-8859-1"));
            objectInputStream = new ObjectInputStream(
                    byteArrayInputStream);
            object =  objectInputStream.readObject();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                objectInputStream.close();
                byteArrayInputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return object;
        }
    }

    public static final String ROTATING_FOREGROUND = "ROTATING_FOREGROUND";
    public static final String OLD_MODEL = "OLD_MODEL";

    public static final String DIAL_BG_COLOR  = "DIAL_BG_COLOR";
    public static final String DIAL_SCALE_COLOR  = "DIAL_SCALE_COLOR";
    public static final String DIAL_TEXT_COLOR  = "DIAL_TEXT_COLOR";
    public static final String POINTER_BG_COLOR = "POINTER_BG_COLOR";
    public static final String POINTER_COLOR  = "POINTER_COLOR";
    public static final String ROOT_BG_COLOR  = "ROOT_BG_COLOR";

    public static final String ROOT_BG_IMG_IS_SHOW  = "ROOT_BG_IMG_IS_SHOW";
    public static final String POINTER_BG_IMG_IS_SHOW  = "POINTER_BG_IMG_IS_SHOW";
    public static final String DIAL_BG_IMG_IS_SHOW  = "DIAL_BG_IMG_IS_SHOW";

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

    public static boolean isShowRootBgImg(Context context){
        return get(context,ROOT_BG_IMG_IS_SHOW, false);
    }

    public static void setShowRootBgImg(Context context,boolean b){
        put(context,ROOT_BG_IMG_IS_SHOW,b);
    }

    public static boolean isShowDialBgImg(Context context){
        return get(context,DIAL_BG_IMG_IS_SHOW, false);
    }

    public static void setShowDialBgImg(Context context,boolean b){
        put(context,DIAL_BG_IMG_IS_SHOW,b);
    }

    public static boolean isShowPointerBgImg(Context context){
        return get(context,POINTER_BG_IMG_IS_SHOW, false);
    }

    public static void setShowPointerBgImg(Context context,boolean b){
        put(context,POINTER_BG_IMG_IS_SHOW,b);
    }

}
