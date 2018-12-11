package com.liang.lollipop.lcompass.util

import android.content.Context
import android.graphics.Color
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.liang.lollipop.lcompass.R

/**
 * Created by Lollipop on 2018/12/10
 * 个人偏好设置
 */
object Settings2 {

    inline fun <reified T> Context.put(key: String, value: T) {
        val shareConfig = PreferenceManager.getDefaultSharedPreferences(this)?:return
        val conEdit = shareConfig.edit()?:return
        when (value) {
            is String -> conEdit.putString(key.trim(), (value as String).trim())
            is Long -> conEdit.putLong(key, value as Long)
            is Boolean -> conEdit.putBoolean(key, value as Boolean)
            is Int -> conEdit.putInt(key, value as Int)
            is Float -> conEdit.putFloat(key, value as Float)
        }
        conEdit.apply()
    }

    inline fun <reified T> Context.get(key: String, defValue: T): T {
        var value: T? = null
        val shareConfig = PreferenceManager.getDefaultSharedPreferences(this)?:return defValue
        when (defValue) {
            is String -> value = shareConfig.getString(key, defValue as String) as T
            is Long -> value = java.lang.Long.valueOf(shareConfig.getLong(key, defValue as Long)) as T
            is Boolean -> value = java.lang.Boolean.valueOf(shareConfig.getBoolean(key, defValue as Boolean)) as T
            is Int -> value = Integer.valueOf(shareConfig.getInt(key, defValue as Int)) as T
            is Float -> value = java.lang.Float.valueOf(shareConfig.getFloat(key, defValue as Float)) as T
        }
        return value ?: defValue
    }

    class ContextModel(private val context: Context) {
        var isRotatingForeground: Boolean
            get() {
                return context.get(context.getString(R.string.key_rotating_foreground), true)
            }
            set(value) {
                context.put(context.getString(R.string.key_rotating_foreground), value)
            }

        var isOldModel: Boolean
            get() {
                return context.get(context.getString(R.string.key_old_api), false)
            }
            set(value) {
                context.put(context.getString(R.string.key_old_api), value)
            }

        var dialBgColor: Int
            get() {
                return context.get(context.getString(R.string.key_dial_bg_color), Color.WHITE)
            }
            set(value) {
                context.put(context.getString(R.string.key_dial_bg_color), value)
            }

        var dialScaleColor: Int
            get() {
                return context.get(context.getString(R.string.key_dial_scale_color), Color.GRAY)
            }
            set(value) {
                context.put(context.getString(R.string.key_dial_scale_color), value)
            }

        var dialTextColor: Int
            get() {
                return context.get(context.getString(R.string.key_dial_text_color), Color.GRAY)
            }
            set(value) {
                context.put(context.getString(R.string.key_dial_text_color), value)
            }

        var pointerBgColor: Int
            get() {
                return context.get(context.getString(R.string.key_pointer_bg_color), Color.TRANSPARENT)
            }
            set(value) {
                context.put(context.getString(R.string.key_pointer_bg_color), value)
            }


        var pointerColor: Int
            get() {
                return context.get(context.getString(R.string.key_pointer_color), Color.GRAY)
            }
            set(value) {
                context.put(context.getString(R.string.key_pointer_color), value)
            }


        var rootBgColor: Int
            get() {
                return context.get(context.getString(R.string.key_root_bg_color),
                        ContextCompat.getColor(context, R.color.background))

            }
            set(value) {
                context.put(context.getString(R.string.key_root_bg_color), value)
            }


        var locationTextColor: Int
            get() {
                return context.get(context.getString(R.string.key_location_text_color),
                        ContextCompat.getColor(context, R.color.colorPrimary))

            }
            set(value) {
                context.put(context.getString(R.string.key_location_text_color), value)
            }

        var isShowRootBgImg: Boolean
            get() {
                return context.get(context.getString(R.string.key_root_bg_image_state), false)
            }
            set(value) {
                context.put(context.getString(R.string.key_root_bg_image_state), value)
            }

        var rootBgImg: String
            get() {
                return context.get(context.getString(R.string.key_root_bg_image), "")
            }
            set(value) {
                context.put(context.getString(R.string.key_root_bg_image), value)
            }

        var isShowDialBgImg: Boolean
            get() {
                return context.get(context.getString(R.string.key_dial_bg_image_state), false)
            }
            set(value) {
                context.put(context.getString(R.string.key_dial_bg_image_state), value)
            }

        var dialBgImg: String
            get() {
                return context.get(context.getString(R.string.key_dial_bg_image), "")
            }
            set(value) {
                context.put(context.getString(R.string.key_dial_bg_image), value)
            }

        var isShowPointerBgImg: Boolean
            get() {
                return context.get(context.getString(R.string.key_pointer_bg_image_state), false)
            }
            set(value) {
                context.put(context.getString(R.string.key_pointer_bg_image_state), value)
            }

        var pointerBgImg: String
            get() {
                return context.get(context.getString(R.string.key_pointer_bg_image), "")
            }
            set(value) {
                context.put(context.getString(R.string.key_pointer_bg_image), value)
            }

        var isShowSettingBtn: Boolean
            get() {
                return context.get(context.getString(R.string.key_show_setting_btn), true)
            }
            set(value) {
                context.put(context.getString(R.string.key_show_setting_btn), value)
            }


        var isStableMode: Boolean
            get() {
                return context.get(context.getString(R.string.key_stable_mode), true)
            }
            set(value) {
                context.put(context.getString(R.string.key_stable_mode), value)
            }


        var is3DMode: Boolean
            get() {
                return context.get(context.getString(R.string.key_3d_mode), true)
            }
            set(value) {
                context.put(context.getString(R.string.key_3d_mode), value)
            }


        var isCameraMode: Boolean
            get() {
                return context.get(context.getString(R.string.key_camera_mode), true)
            }
            set(value) {
                context.put(context.getString(R.string.key_camera_mode), value)
            }

        var isChinaseScale: Boolean
            get() {
                return context.get(context.getString(R.string.key_chinese_mode), false)
            }
            set(value) {
                context.put(context.getString(R.string.key_chinese_mode), value)
            }

        var isOverflowAvoid: Boolean
            get() {
                return context.get(context.getString(R.string.key_overflow_avoid), true)
            }
            set(value) {
                context.put(context.getString(R.string.key_overflow_avoid), value)
            }

        var isNoiseReduction: Boolean
            get() {
                return context.get(context.getString(R.string.key_noise_reduction), true)
            }
            set(value) {
                context.put(context.getString(R.string.key_noise_reduction), value)
            }
    }

    class FragmentModel(private val fragment: Fragment) {
        var isRotatingForeground: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_rotating_foreground), true)?:true
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_rotating_foreground), value)
            }

        var isOldModel: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_old_api), false)?:false
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_old_api), value)
            }

        var dialBgColor: Int
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_dial_bg_color), Color.WHITE)?:Color.WHITE
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_dial_bg_color), value)
            }

        var dialScaleColor: Int
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_dial_scale_color), Color.GRAY)?:Color.GRAY
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_dial_scale_color), value)
            }

        var dialTextColor: Int
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_dial_text_color), Color.GRAY)?:Color.GRAY
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_dial_text_color), value)
            }

        var pointerBgColor: Int
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_pointer_bg_color), Color.TRANSPARENT)?:Color.TRANSPARENT
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_pointer_bg_color), value)
            }


        var pointerColor: Int
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_pointer_color), Color.GRAY)?:Color.GRAY
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_pointer_color), value)
            }


        var rootBgColor: Int
            get() {
                val c = fragment.context?:return Color.GRAY
                return c.get(fragment.getString(R.string.key_root_bg_color),
                        ContextCompat.getColor(c, R.color.background))

            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_root_bg_color), value)
            }


        var locationTextColor: Int
            get() {
                val c = fragment.context?:return Color.GRAY
                return c.get(fragment.getString(R.string.key_location_text_color),
                        ContextCompat.getColor(c, R.color.colorPrimary))

            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_location_text_color), value)
            }

        var isShowRootBgImg: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_root_bg_image_state), false)?:false
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_root_bg_image_state), value)
            }

        var rootBgImg: String
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_root_bg_image), "")?:""
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_root_bg_image), value)
            }

        var isShowDialBgImg: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_dial_bg_image_state), false)?:false
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_dial_bg_image_state), value)
            }

        var dialBgImg: String
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_dial_bg_image), "")?:""
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_dial_bg_image), value)
            }

        var isShowPointerBgImg: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_pointer_bg_image_state), false)?:false
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_pointer_bg_image_state), value)
            }

        var pointerBgImg: String
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_pointer_bg_image), "")?:""
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_pointer_bg_image), value)
            }

        var isShowSettingBtn: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_show_setting_btn), true)?:true
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_show_setting_btn), value)
            }


        var isStableMode: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_stable_mode), true)?:true
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_stable_mode), value)
            }


        var is3DMode: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_3d_mode), true)?:true
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_3d_mode), value)
            }


        var isCameraMode: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_camera_mode), true)?:true
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_camera_mode), value)
            }

        var isChinaseScale: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_chinese_mode), false)?:false
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_chinese_mode), value)
            }

        var isOverflowAvoid: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_overflow_avoid), true)?:true
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_overflow_avoid), value)
            }

        var isNoiseReduction: Boolean
            get() {
                return fragment.context?.get(fragment.getString(R.string.key_noise_reduction), true)?:true
            }
            set(value) {
                fragment.context?.put(fragment.getString(R.string.key_noise_reduction), value)
            }

    }

}


