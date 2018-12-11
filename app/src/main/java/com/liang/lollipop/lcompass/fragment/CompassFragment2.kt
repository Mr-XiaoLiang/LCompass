package com.liang.lollipop.lcompass.fragment

import android.graphics.Bitmap
import android.graphics.Typeface
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.liang.lollipop.lcompass.R
import com.liang.lollipop.lcompass.drawable.CircleBgDrawable
import com.liang.lollipop.lcompass.drawable.DialDrawable
import com.liang.lollipop.lcompass.drawable.PointerDrawable
import com.liang.lollipop.lcompass.util.*
import kotlinx.android.synthetic.main.fragment_compass2.*
import java.io.File
import java.text.DecimalFormat

/**
 * @date: 2018/12/11 22:52
 * @author: lollipop
 * 指南针分页
 */
class CompassFragment2: Fragment(), CompassView {

    companion object {
        //检查读取内存卡权限的ID
        private const val CHECK_WRITE_SD = 789
    }

    //浮点数的格式化
    private val decimalFormat: DecimalFormat = DecimalFormat("#,##0.00")

    //圆形的渲染
    private val statusDrawable: CircleBgDrawable = CircleBgDrawable()

    //背景渲染Drawable
    private val dialDrawable: DialDrawable = DialDrawable()

    //前景渲染Drawable
    private val pointerDrawable: PointerDrawable = PointerDrawable()

    private var is3DMode = true
    private var isOverflowAvoid = true
    private var isRotatingForeground = true
    private var isStableMode = true

    private lateinit var fragmentModel: Settings2.FragmentModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentModel = Settings2.FragmentModel(this)
    }

    override fun onResume() {
        super.onResume()
        is3DMode = fragmentModel.is3DMode
        isOverflowAvoid = fragmentModel.isOverflowAvoid
        isRotatingForeground = fragmentModel.isRotatingForeground
        isStableMode = fragmentModel.isStableMode
        initDial()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_compass2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        statusView.setImageDrawable(statusDrawable)
        dialView.setImageDrawable(dialDrawable)
        pointerView.setImageDrawable(pointerDrawable)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tryGetTypeface()
    }

    private fun tryGetTypeface() {
        PermissionsUtil.checkPermissions(activity, CHECK_WRITE_SD, PermissionsUtil.OnPermissionsPass { getTypeface() }, PermissionsUtil.WRITE_EXTERNAL_STORAGE)
    }

    private fun getTypeface() {
        try {
            val dir = File(OtherUtil.getSDFontPath())
            if (!dir.exists()) {
                dir.mkdirs()
                return
            }
            val ttfFile = File(dir, "fonts.ttf")
            if (ttfFile.exists()) {
                val typeFace = Typeface.createFromFile(ttfFile)
                if (typeFace != null)
                    dialDrawable.setTypeface(typeFace)
                return
            }

            val otfFile = File(dir, "fonts.otf")
            if (otfFile.exists()) {
                val typeFace = Typeface.createFromFile(otfFile)
                if (typeFace != null)
                    dialDrawable.setTypeface(typeFace)
            }
        } catch (e: Exception) {
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CHECK_WRITE_SD -> if (PermissionsUtil.checkPermission(context, PermissionsUtil.WRITE_EXTERNAL_STORAGE)) {
                getTypeface()
            } else {
                PermissionsUtil.popPermissionsDialog(context, getString(R.string.no_write_sd_with_fonts))
            }
        }
    }

    private fun initDial() {
        dialDrawable.setTextColor(fragmentModel.dialTextColor)
        dialDrawable.setBgColor(fragmentModel.dialBgColor)
        dialDrawable.setScaleColor(fragmentModel.dialScaleColor)
        dialDrawable.setChinase(fragmentModel.isChinaseScale)
        pointerDrawable.setBgColor(fragmentModel.pointerBgColor)
        pointerDrawable.setColor(fragmentModel.pointerColor)
        backgroundBody.setBackgroundColor(fragmentModel.rootBgColor)
        locationText.setTextColor(fragmentModel.locationTextColor)
        pressureText.setTextColor(fragmentModel.locationTextColor)
        angleText.setTextColor(fragmentModel.locationTextColor)
        if (fragmentModel.isShowRootBgImg) {
            glide.load(fragmentModel.rootBgImg).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(backgroundBody)
        } else {
            backgroundBody.setImageDrawable(null)
        }
        dialDrawable.setShowBitmap(fragmentModel.isShowDialBgImg)
        glide.load(fragmentModel.dialBgImg).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                dialDrawable.setBitmap(resource)
            }
        })
        pointerDrawable.setShowBitmap(fragmentModel.isShowPointerBgImg)
        glide.load(fragmentModel.pointerBgImg).asBitmap().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                pointerDrawable.setBitmap(resource)
            }
        })

        if (isOverflowAvoid && is3DMode) {
            dialDrawable.setScaleColor(0)
        } else {
            dialDrawable.setScaleColor(fragmentModel.dialScaleColor)
        }
    }

    override fun onTypeChange(b: Boolean) {
        isRotatingForeground = b
        if (b) {
            dialView.rotation = 0f
        } else {
            pointerView.rotation = 0f
        }
    }

    override fun on3DModelChange(b: Boolean) {
        is3DMode = b
        dialView.setBackgroundResource(if (b) 0 else R.drawable.bg_circle)
        if (isOverflowAvoid && b) {
            dialDrawable.setScaleColor(0)
        } else {
            dialDrawable.setScaleColor(fragmentModel.dialScaleColor)
        }
    }

    private fun formatText(angle: Float) {
        angleText.text = getOrientation(angle)
    }

    private fun rotating(a: Float) {
        var angle = a
        if (isStableMode)
            angle = (angle + 0.5f).toInt().toFloat()
        if (isRotatingForeground) {
            pointerView.rotation = -angle
        } else {
            dialView.rotation = -angle
        }
    }

    override fun onSensorUpdate(z: Float, x: Float, y: Float) {
        formatText(z)
        rotating(z)
        onPitchRollChange(x, y)
    }

    override fun onSensorStateUpdate(i: Int) {
        val con = context?:return
        when (i) {
            SensorManager.SENSOR_STATUS_UNRELIABLE -> statusDrawable.setColor(ContextCompat.getColor(con, R.color.sensorStatusUnreliable))
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> statusDrawable.setColor(ContextCompat.getColor(con, R.color.sensorStatusAccuracyLow))
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> statusDrawable.setColor(ContextCompat.getColor(con, R.color.sensorStatusAccuracyMedium))
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> statusDrawable.setColor(ContextCompat.getColor(con, R.color.sensorStatusAccuracyHigh))
        }
    }

    private fun getOrientation(angle: Float): String {
        //倒退5度，然后分成360份
        if (angle.exist(-1F, 1F)) {
            return getOrientationValue(R.string.north, angle)
        } else if (angle.exist(1F, 45F)) {
            return getOrientationValue(R.string.north_east, angle)
        } else if (angle.exist(45F, 89F)) {
            return getOrientationValue(R.string.east_north, (90 - angle))
        } else if (angle.exist(89F, 91F)) {
            return getOrientationValue(R.string.east, (angle - 90))
        } else if (angle.exist(91F, 135F)) {
            return getOrientationValue(R.string.east_south, (angle - 90))
        } else if (angle.exist(135F, 179F)) {
            return getOrientationValue(R.string.south_east, (180 - angle))
        } else if (angle > 179 || angle <= -179) {
            return getOrientationValue(R.string.south, (180 - Math.abs(angle)))
        } else if (angle.exist(-45F, -1F)) {
            return getOrientationValue(R.string.north_west, (-angle))
        } else if (angle.exist(-89F, -45F)) {
            return getOrientationValue(R.string.west_north, (90 + angle))
        } else if (angle.exist(-91F, -89F)) {
            return getOrientationValue(R.string.west, (-angle - 90))
        } else if (angle.exist(-135F, -91F)) {
            return getOrientationValue(R.string.west_south, (-angle - 90))
        } else if (angle.exist(-179F, -135F)) {
            return getOrientationValue(R.string.south_west, (180 + angle))
        }
        return ""
    }

    private fun Float.exist(min: Float, max: Float): Boolean {
        return this > min && this <= max
    }

    private fun getOrientationValue(resId: Int, num: Float): String {
        return getString(resId) + num.format()
    }

    private fun Float.format(): String {
        return decimalFormat.format(this.toDouble())
    }

    /**
     * 更新位置信息
     */
    override fun onLocationUpdate(location: Location?) {
        super.onLocationUpdate(location)
        if (null == location) {
            locationText.text = ""
            if (locationText.visibility != View.GONE)
                locationText.visibility = View.GONE
            return
        } else {
            if (locationText.visibility != View.VISIBLE)
                locationText.visibility = View.VISIBLE
        }
        val sb = StringBuilder()
        val longitude = location.longitude
        val latitude = location.latitude

        sb.append(if (latitude >= 0.0f) getString(R.string.north_latitude) else getString(R.string.south_latitude))
        sb.append(Math.abs(latitude).toString())
        sb.append("\n")
        sb.append(if (longitude >= 0.0f) getString(R.string.east_longitude) else getString(R.string.west_longitude))
        sb.append(Math.abs(longitude).toString())
        if (location.hasAccuracy()) {
            //如果有精度描述
            val accuracy = location.accuracy
            sb.append("\n")
            sb.append(getString(R.string.accuracy))
            sb.append(Math.abs(accuracy).toString())
            sb.append(getString(R.string.meter))
        }
        if (location.hasAltitude()) {
            //如果有海拔描述
            sb.append("\n")
            sb.append(getString(R.string.altitude))
            sb.append(Math.abs(location.altitude).toString())
            sb.append(getString(R.string.meter))
        }
        if (location.hasBearing()) {
            //如果有方向
        }
        if (location.hasSpeed()) {
            //如果有速度
            sb.append("\n")
            sb.append(getString(R.string.speed))
            sb.append(Math.abs(location.speed).toString())
            sb.append(getString(R.string.meters_per_second))
        }

        locationText.text = sb.toString()
    }

    override fun onPressureChange(hPa: Float, altitude: Float) {
        super.onPressureChange(hPa, altitude)
        if (pressureText.visibility != View.VISIBLE)
            pressureText.visibility = View.VISIBLE
        val str = locationText.text.toString()
        val stringBuilder = StringBuilder()
        stringBuilder.append(getString(R.string.pressure))
        stringBuilder.append(decimalFormat.format(hPa.toDouble()))
        stringBuilder.append(getString(R.string.hpa))
        if (!str.contains(getString(R.string.altitude))) {
            stringBuilder.append("\n")
            stringBuilder.append(getString(R.string.altitude))
            stringBuilder.append(decimalFormat.format(altitude.toDouble()))
            stringBuilder.append(getString(R.string.meter))
        }
        pressureText.text = stringBuilder.toString()
    }

    //当手机XY轴变化时，得到回调，修改指南针的XY轴旋转，说到模拟3D效果
    private fun onPitchRollChange(x: Float, y: Float) {
        if (is3DMode) {
            dialView.rotationX = -x
            dialView.rotationY = y
            pointerView.rotationX = -x
            pointerView.rotationY = y
        } else {
            dialView.rotationX = 0f
            dialView.rotationY = 0f
            pointerView.rotationX = 0f
            pointerView.rotationY = 0f
        }
    }

    override fun onCameraOpened() {
        super.onCameraOpened()
        dialGroupView.visibility = View.INVISIBLE
        backgroundBody.visibility = View.INVISIBLE
    }

    override fun onCameraClosed() {
        super.onCameraClosed()
        dialGroupView.visibility = View.VISIBLE
        backgroundBody.visibility = View.VISIBLE
    }

}