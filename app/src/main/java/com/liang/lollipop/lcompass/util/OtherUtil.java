package com.liang.lollipop.lcompass.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class OtherUtil {

//    public static final String USER_INFO_BG = "background.png";

    public static final String USER_INFO_BG = ".png";

    public static String getUserInfoBg(String token){
        return token+USER_INFO_BG;
    }

    /**
     * 跳转到qq
     *
     * @param context 上下文
     * @param qqNum   qq号码
     * @return 是否成功
     */
    public static boolean toTheQQ(Context context, String qqNum) {
        if (null == qqNum || qqNum.length() < 5) {
            return false;
        }
        String url11 = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum + "&version=1";
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url11)));
        return true;
    }

    /**
     * 保存方法
     */
    public static void saveBitmap(Bitmap bm, String picName, String path) {
        File pathFile = new File(path);
        File f = new File(path, picName);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveFile(String file, String fileName, String path){
        return saveFile(new File(file),fileName,path);
    }

    public static boolean saveFile(File file, String fileName, String path) {
        if(file==null)
            return false;
        File pathFile = new File(path);
        File f = new File(path, fileName);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        if (f.exists()) {
            f.delete();
        }
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(f);
            // 把数据存入路径+文件名
            byte[] buf = new byte[1024];
            do {
                // 循环读取
                int numread = in.read(buf);
                if (numread == -1) {
                    break;
                }
                out.write(buf, 0, numread);
            } while (true);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            if(out!=null){
                try {
                    out.flush();
                } catch (IOException e) {
                }
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return true;
    }

    public static String getAppImgPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/img";
    }

    public static String getCacheImgPath(Context context) {
        return context.getCacheDir().getPath() + "/img";
    }

    public static String getCacheSmallImgPath(Context context) {
        return context.getCacheDir().getPath() + "/img/small";
    }

    public static String getCacheVoicePath(Context context) {
        return context.getCacheDir().getPath() + "/voice";
    }

    public static String getSDImgPath() {
        return Environment.getExternalStorageDirectory() + "/LCompass/img";
    }

    public static String getBGImgPath() {
        return Environment.getExternalStorageDirectory() + "/LCompass/img/bg";
    }

    public static String getSDSmallImgPath() {
        return Environment.getExternalStorageDirectory() + "/LCompass/img/small";
    }

    public static String getSDVoicePath() {
        return Environment.getExternalStorageDirectory() + "/LCompass/voice";
    }

    public static String getSDTxtPath() {
        return Environment.getExternalStorageDirectory() + "/LCompass/txt";
    }

    public static String getSDAppPath(Context context) {
        return Environment.getExternalStorageDirectory() + "/LCompass/app";
    }

    /**
     * 保存到SD卡
     *
     * @param bm
     * @param picName
     */
    public static void saveBitmapToSD(Bitmap bm, String picName) {
        saveBitmap(bm, picName, getSDImgPath());
    }

    /**
     * 保存到APP
     *
     * @param context
     * @param bm
     * @param picName
     */
    public static void saveBitmapToApp(Context context, Bitmap bm, String picName) {
        saveBitmap(bm, picName, getAppImgPath(context));
    }

    public static String getContextImagePath(Context context){
//        return context.getFilesDir().getAbsolutePath() + "/bg";
        return getBGImgPath();
    }

    /**
     * 保存背景图
     *
     * @param context
     * @param path
     */
    public static boolean saveBackground(Context context, String path) {
        return saveFile(path,"Background.png",getContextImagePath(context));
    }

    public static String getBackground(Context context) {
        return new File(getContextImagePath(context),"Background.png").getAbsolutePath();
    }

    public static boolean saveDialBackground(Context context, String path) {
        return saveFile(path,"DialBackground.png",getContextImagePath(context));
    }

    public static String getDialBackground(Context context) {
        return new File(getContextImagePath(context),"DialBackground.png").getAbsolutePath();
    }

    public static boolean savePointerBackground(Context context, String path) {
        return saveFile(path, "PointerBackground.png",getContextImagePath(context));
    }

    public static String getPointerBackground(Context context) {
        return new File(getContextImagePath(context),"PointerBackground.png").getAbsolutePath();
    }

    public static String getTempImgPath(Context context){
        return getTempImgFile(context).getAbsolutePath();
    }
    public static File getTempImgFile(Context context){
        File file = new File(getSDImgPath());
        if(!file.exists())
            file.mkdirs();
        return new File(file,"temp.png");
    }

    /**
     * 获取网络的类型
     *
     * @param context
     * @return
     */
//    public static int getNetworkType(Context context) {
//        ConnectivityManager connectMgr = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
//        if (networkInfo == null || !networkInfo.isConnected()) {
//            return Constant.NetWord_NULL;
//        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            return Constant.NetWord_WIFI;
//        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//            return Constant.NetWord_MOBILE;
//        } else {
//            return Constant.NetWord_Other;
//        }
//    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static String getNetworkTypeStr(Context context) {
        String strNetworkType = "";
        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionCode+"";
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 安装apk方法
     */
    public static void installApk(Context context, String filename) {
        File file = new File(filename);
        installApk(context,Uri.fromFile(file));
    }

    public static void installApk(Context context, Uri uri) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(uri, type);
        context.startActivity(intent);
    }

    /**
     * 资源转图片
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 对资源进行渲染
     *
     * @param context
     * @param resId
     * @param color
     * @return
     */
    public static Drawable tintDrawable(Context context, int resId, int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(context.getResources().getDrawable(resId));
        return tintDrawable(wrappedDrawable, ColorStateList.valueOf(color));
    }

    /**
     * 对资源进行渲染
     *
     * @param context
     * @param resId
     * @param color
     * @param independent
     * @return
     */
    public static Drawable tintDrawable(Context context, int resId, int color, boolean independent) {
        final Drawable wrappedDrawable;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            wrappedDrawable = context.getResources().getDrawable(resId,context.getTheme());
        else
            wrappedDrawable = context.getResources().getDrawable(resId);

        if (independent) {
            wrappedDrawable.mutate();
        }
        return tintDrawable(wrappedDrawable, ColorStateList.valueOf(color));
    }

    public static Drawable tintDrawable(Context context,Bitmap bitmap, int color) {
        final BitmapDrawable wrappedDrawable;
//        if (independent) {
//            wrappedDrawable = context.getResources().getDrawable(resId).mutate();
//        } else {
//            wrappedDrawable = DrawableCompat.wrap(context.getResources().getDrawable(resId));
//        }
        wrappedDrawable = new BitmapDrawable(context.getResources(),bitmap);
        return tintDrawable(wrappedDrawable, ColorStateList.valueOf(color));
    }

    /**
     * 对资源进行渲染
     *
     * @param drawable
     * @param colors
     * @return
     */
    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors, boolean independent) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        if (independent)
            wrappedDrawable.mutate();
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    /**
     * 对资源进行渲染
     *
     * @param drawable
     * @param colors
     * @return
     */
    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        return tintDrawable(drawable, colors, false);
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        return tintDrawable(drawable, ColorStateList.valueOf(color));
    }

    public static Drawable tintDrawable(Drawable drawable, int color,boolean type) {
        return tintDrawable(drawable, ColorStateList.valueOf(color), type);
    }

    /**
     * 分享
     *
     * @param context       上下文
     * @param activityTitle 页面标题
     * @param msgTitle      被内容标题
     * @param msgText       内容
     * @param imgPath       图片地址
     */
    public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                                String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (TextUtils.isEmpty(imgPath)) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f.exists() && f.isFile()) {
                intent.setType("image/*");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
