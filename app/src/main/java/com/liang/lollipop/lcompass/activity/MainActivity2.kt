package com.liang.lollipop.lcompass.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.location.*
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.liang.lollipop.lcompass.R
import com.liang.lollipop.lcompass.fragment.CompassView
import com.liang.lollipop.lcompass.util.*
import kotlinx.android.synthetic.main.activity_main2.*

/**
 * @date: 2018/12/10 20:47
 * @author: lollipop
 * 新的主页
 */
class MainActivity2 : BaseActivity2(), SensorEventListener, LocationListener {

    companion object {
        private const val REQUEST_CAMERA = 235

        private const val REQUEST_LOCATION = 23
        private const val REQUEST_SENSOR = 24
    }

    private lateinit var surfaceUtil: LSurfaceUtil

    /**
     * 传感器管理器
     */
    private var sensorManager: SensorManager? = null
    /**
     * 加速度传感器
     */
    private var accelerometer: Sensor? = null
    /**
     * 地磁场传感器
     */
    private var magnetic: Sensor? = null
    /**
     * 老版的定位API
     */
    private var oldOrientation: Sensor? = null
    /**
     * 气压传感器
     */
    private var pressureSensor: Sensor? = null

    /**
     * 加速度
     */
    private var accelerometerValues = FloatArray(3)
    /**
     * 地磁场
     */
    private var magneticFieldValues = FloatArray(3)

    /**
     * 方向的实际值
     */
    private val orientationValues: FloatArray = FloatArray(3)
    /**
     * 方向的矩阵数据
     */
    private val orientationR: FloatArray = FloatArray(9)

    /**
     * 位置服务
     */
    private var locationManager: LocationManager? = null

    /**
     * 触发相机模式的值
     */
    private val triggerCameraSize = 45f

    /**
     * Z 轴的滤波器
     */
    private val zAxisFilter = FilterUtil(20)

    /**
     * X 轴的滤波器
     */
    private val xAxisFilter = FilterUtil(20)

    /**
     * Y 轴的滤波器
     */
    private val yAxisFilter = FilterUtil(20)

    private lateinit var contextModel: Settings2.ContextModel

    private var is3DMode = true
    private var isCameraMode = true
    private var isNoiseReduction = true
    private var isOldModel = true
    private var isRotatingForeground = true
    private var isStableMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contextModel = Settings2.ContextModel(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_main2)
        initView()
        PermissionsUtil.checkPermissions(this, REQUEST_LOCATION, PermissionsUtil.ACCESS_FINE_LOCATION)
        PermissionsUtil.checkPermissions(this, REQUEST_SENSOR, PermissionsUtil.BODY_SENSORS)
        PermissionsUtil.checkPermissions(this, REQUEST_CAMERA, PermissionsUtil.CAMERA_)
    }

    private fun initView() {
        surfaceUtil = LSurfaceUtil.withTexture(surfaceView)

        typeChangeBtn.setOnClickListener(this)
        modelChangeBtn.setOnClickListener(this)
        d3ModelChangeBtn.setOnClickListener(this)
        cameraModeChangeBtn.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        surfaceUtil.onStart()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1 || contextModel.isShowSettingBtn) {
            settingBtn.visibility = View.VISIBLE
            settingBtn.setOnClickListener(this)
        }

        is3DMode = contextModel.is3DMode
        isCameraMode = contextModel.isCameraMode
        isNoiseReduction = contextModel.isNoiseReduction
        isOldModel = contextModel.isOldModel
        isRotatingForeground = contextModel.isRotatingForeground
        isStableMode = contextModel.isStableMode

        onTypeChange()
        onModelChange()
        on3DModelChange()
        onCameraModelChange()

        registerSensorService()
        initLocation()
    }

    override fun onStop() {
        super.onStop()
        surfaceUtil.onStop()
        // 解除注册
        sensorManager?.unregisterListener(this)
        locationManager?.removeUpdates(this)
    }

    private fun initSensor() {
        // 实例化传感器管理者
        if (sensorManager == null) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        if (isOldModel) {
            //老版方位获取器
            if (oldOrientation == null) {
                oldOrientation = sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)
            }
        } else {
            // 初始化加速度传感器
            if (accelerometer == null) {
                accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            }
            // 初始化地磁场传感器
            if (magnetic == null) {
                magnetic = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            }
        }
        if (pressureSensor == null) {
            pressureSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PRESSURE)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION -> initLocation()
            REQUEST_SENSOR -> registerSensorService()
            REQUEST_CAMERA -> if (!PermissionsUtil.checkPermission(this, PermissionsUtil.CAMERA_)) {
                isCameraMode = false
                onCameraModelChange()
            }
            else -> {
            }
        }
    }

    private fun registerSensorService() {
        if (PermissionsUtil.checkPermissions(this, PermissionsUtil.BODY_SENSORS)) {
            //取消监听器
            sensorManager?.unregisterListener(this)
            //如果是老版模式
            if (isOldModel) {
                if (sensorManager == null || oldOrientation == null) {
                    initSensor()
                }
                sensorManager?.registerListener(this, oldOrientation,
                        SensorManager.SENSOR_DELAY_FASTEST)
            } else {//新模式
                if (sensorManager == null || accelerometer == null || magnetic == null) {
                    initSensor()
                }
                // 注册监听
                if (isStableMode) {
                    sensorManager?.registerListener(this,
                            accelerometer, SensorManager.SENSOR_DELAY_GAME)
                    sensorManager?.registerListener(this, magnetic,
                            SensorManager.SENSOR_DELAY_GAME)
                } else {
                    sensorManager?.registerListener(this,
                            accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
                    sensorManager?.registerListener(this, magnetic,
                            SensorManager.SENSOR_DELAY_FASTEST)
                }
            }
            if (pressureSensor == null) {
                initSensor()
            }
            sensorManager?.registerListener(this,
                    pressureSensor, SensorManager.SENSOR_DELAY_FASTEST)
        } else {
            PermissionsUtil.popPermissionsDialog(this, getString(R.string.no_sensor_permission))
            onSensorUpdate(0f, 0f, 0f)
        }
    }

    private fun openCamera() {
        if (!PermissionsUtil.checkPermissions(this, PermissionsUtil.CAMERA_)) {
            PermissionsUtil.popPermissionsDialog(this, getString(R.string.no_camera_permission))
            return
        }
        if (surfaceUtil.isCameraOpened) {
            return
        }
        try {
            surfaceUtil.withCamera2(this)
            val ids = surfaceUtil.cameraIdList
            if (ids?.isNotEmpty() == true) {
                surfaceUtil.openCamera(this, ids[0], handler, windowManager)
                onCameraOpened()
            } else {
                S(getString(R.string.no_find_camera))
            }
        } catch (e: CameraAccessException) {
            S(LSurfaceUtil.getCodeError(e.reason))
        }

    }

    private fun closeCamera() {
        onCameraClosed()
        surfaceUtil.closeCameras()
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        //气压传感器
        if (sensorEvent.sensor.type == Sensor.TYPE_PRESSURE) {
            val hPa = sensorEvent.values[0]
            // 计算海拔
            val height = (44330000 * (1 - Math.pow(hPa / 1013.25, 1.0 / 5255.0))).toFloat()
            onPressureChange(hPa, height)
            return
        }

        if (isOldModel) {
            //方位传感器（旧API）
            if (sensorEvent.sensor.type == Sensor.TYPE_ORIENTATION) {
                val newValue = sensorEvent.values
                for (index in 0 until Math.min(orientationValues.size, newValue.size)) {
                    orientationValues[index] = newValue[index]
                }
            }
        } else {
            //重力传感器以及加速度传感器配合来检查方位
            if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values
            }
            if (sensorEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = sensorEvent.values
            }
            SensorManager.getRotationMatrix(orientationR, null, accelerometerValues,
                    magneticFieldValues)
            SensorManager.getOrientation(orientationR, orientationValues)
            orientationValues[0] = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
            orientationValues[1] = Math.toDegrees(orientationValues[1].toDouble()).toFloat()
            orientationValues[2] = Math.toDegrees(orientationValues[2].toDouble()).toFloat()
            //            orientationValues[1] *= -1;
            orientationValues[2] *= -1f
        }
        if (orientationValues[0] > 180) {
            //有些计量方式是360有些是180
            orientationValues[0] = orientationValues[0] - 360
        }
        onSensorUpdate(orientationValues[0], orientationValues[1], orientationValues[2])
    }

    private fun initLocation() {
        if (!PermissionsUtil.checkPermission(this, PermissionsUtil.ACCESS_FINE_LOCATION) && !PermissionsUtil.checkPermission(this, PermissionsUtil.ACCESS_COARSE_LOCATION)) {
            PermissionsUtil.popPermissionsDialog(this, getString(R.string.no_loc_permission))
            onLocationChanged(null)
            return
        }
        locationManager?.removeUpdates(this)
        if (locationManager == null) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        // 条件对象，即指定条件过滤获得LocationProvider
        val criteria = Criteria()
        // 较高精度
        criteria.accuracy = Criteria.ACCURACY_FINE
        // 是否需要高度信息
        criteria.isAltitudeRequired = true
        // 是否需要方向信息
        criteria.isBearingRequired = true
        // 是否产生费用
        criteria.isCostAllowed = true
        // 设置低电耗
        criteria.powerRequirement = Criteria.POWER_LOW
        val locationProviderName = locationManager?.getBestProvider(criteria, true)

        if (TextUtils.isEmpty(locationProviderName)) {
            S(getString(R.string.no_find_location))
            onLocationChanged(null)
            return
        }
        onLocationChanged(locationManager?.getLastKnownLocation(locationProviderName))
        locationManager?.requestLocationUpdates(locationProviderName, 2000, 10f, this)// 2秒或者距离变化10米时更新一次地理位置
    }

    private fun onTypeChange() {
        val color = if (isRotatingForeground) ContextCompat.getColor(this, R.color.colorPrimary) else Color.GRAY
        typeChangeBtn.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_explore_black_24dp, color))
        compassView?.onTypeChange(isRotatingForeground)
    }

    private fun onModelChange() {
        val color = if (isOldModel) Color.GRAY else ContextCompat.getColor(this, R.color.colorPrimary)
        modelChangeBtn.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_bubble_chart_black_24dp, color))
        compassView?.onModelChange(isOldModel)
    }

    private fun on3DModelChange() {
        val color = if (is3DMode) ContextCompat.getColor(this, R.color.colorPrimary) else Color.GRAY
        d3ModelChangeBtn.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_3d_rotation_black_24dp, color))
        compassView?.on3DModelChange(is3DMode)
    }

    private fun onCameraModelChange() {
        val color = if (isCameraMode) ContextCompat.getColor(this, R.color.colorPrimary) else Color.GRAY
        cameraModeChangeBtn.setImageDrawable(OtherUtil.tintDrawable(this, R.drawable.ic_camera_black_24dp, color))
        compassView?.onCameraModelChange(isCameraMode)
    }

    private fun onPressureChange(hPa: Float, altitude: Float) {
        compassView?.onPressureChange(hPa, altitude)
    }

    override fun onClick(v: View?) {
        when (v) {
            typeChangeBtn -> {
                isRotatingForeground = !isRotatingForeground
                contextModel.isRotatingForeground = isRotatingForeground
                onTypeChange()
                S(if (isRotatingForeground) {
                    getString(R.string.pointer_model)
                } else {
                    getString(R.string.dial_model)
                }, getString(R.string.difference), View.OnClickListener{
                    showTypeInfo()
                })
            }
            modelChangeBtn -> {
                isOldModel = !isOldModel
                contextModel.isOldModel = isOldModel
                onModelChange()
                S(if (isOldModel) {
                    getString(R.string.old_api_model)
                } else {
                    getString(R.string.new_api_model)
                }, getString(R.string.difference), View.OnClickListener{
                    showModelInfo()
                })
                registerSensorService()
            }
            settingBtn -> {
                // startActivity(new Intent(this,SettingActivity.class), Pair.create((View)backgroundView,"DIAL"),Pair.create((View)foregroundView,"POINTER"));
                startActivity(Intent(this, SettingActivity::class.java))
            }

            d3ModelChangeBtn -> {
                is3DMode = !is3DMode
                contextModel.is3DMode = is3DMode
                on3DModelChange()
                S(if (is3DMode) {
                    getString(R.string.c3d_model)
                } else {
                    getString(R.string.c2d_model)
                })
            }
            cameraModeChangeBtn -> {
                isCameraMode = !isCameraMode
                contextModel.isCameraMode = isCameraMode
                onCameraModelChange()
                S(if (isCameraMode) {
                    getString(R.string.open_camera_mode)
                } else {
                    getString(R.string.off_camera_mode)
                })
                if (isCameraMode) {
                    PermissionsUtil.checkPermissions(this, REQUEST_CAMERA, PermissionsUtil.CAMERA_)
                }
            }
            else -> {
            }
        }
    }

    private fun showModelInfo() {
        AlertDialog.Builder(this).setTitle(getString(R.string.api_difference))
                .setMessage(R.string.api_info)
                .show()
    }

    private fun showTypeInfo() {
        AlertDialog.Builder(this).setTitle(getString(R.string.difference))
                .setMessage(R.string.type_info)
                .show()
    }

    private fun onSensorUpdate(z: Float, x: Float, y: Float) {
        if (isNoiseReduction) {
            compassView?.onSensorUpdate(zAxisFilter.filter(z), xAxisFilter.filter(x), yAxisFilter.filter(y))
        } else {
            compassView?.onSensorUpdate(zAxisFilter.filter(z), xAxisFilter.filter(x), yAxisFilter.filter(y))
        }
        if (isOpenCamera(x, y)) {
            openCamera()
        } else {
            closeCamera()
        }
    }

    private fun isOpenCamera(x: Float, y: Float): Boolean {
        if (!isCameraMode) {
            return false
        }
        if (Math.abs(x) > triggerCameraSize || Math.abs(y) > triggerCameraSize) {
            return true
        }
        return (Math.abs(x) + Math.abs(y)) > 90
    }

    private fun onSensorStateUpdate(accuracy: Int) {
        compassView?.onSensorStateUpdate(accuracy)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        onSensorStateUpdate(accuracy)
    }

    override fun onLocationChanged(location: Location?) {
        compassView?.onLocationUpdate(location)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        if (LocationProvider.OUT_OF_SERVICE == status) {
            S(getString(R.string.location_service_disconnect))
            return
        }
        if (LocationProvider.TEMPORARILY_UNAVAILABLE == status) {
            S(getString(R.string.location_service_disable))
            return
        }
        if (LocationProvider.AVAILABLE == status) {
            S(getString(R.string.location_service_restore))
            initLocation()
        }
        onLocationChanged(null)
    }

    override fun onProviderEnabled(provider: String) {
        S(getString(R.string.new_location_provider) + provider.toUpperCase())
        initLocation()
    }

    override fun onProviderDisabled(provider: String) {
        S(getString(R.string.disable_location_provider) + provider.toUpperCase())
        initLocation()
    }

    private fun onCameraOpened() {
        compassView?.onCameraOpened()
    }

    private fun onCameraClosed() {
        compassView?.onCameraClosed()
    }

    private val compassView: CompassView?
        get() {
            return if (compassFragment is CompassView) {
                compassFragment as CompassView
            } else {
                null
            }
        }

    class FilterUtil(private val calculateCount: Int = 5) {

        private val lastPointArray = FloatArray(calculateCount)

        private var arraySize = 0

        fun filter(value: Float): Float {

            val newValue = value + 180F

            push(lastPointArray, newValue)
            if (arraySize < calculateCount) {
                arraySize++
                return newValue
            }
            var result = process(lastPointArray) - 180
            while (Math.abs(result) > 180) {
                if (result < 0) {
                    result += 360
                } else {
                    result -= 360
                }
            }
            return result
        }

        private fun push(array: FloatArray, value: Float) {
            for (index in 1 until array.size) {
                array[index-1] = array[index]
            }
            var newValue = value
            val def = array[array.size-1] - value
            if (def > 180) {
                newValue += 360
            } else if (def < -180) {
                newValue -= 360
            }
            array[array.size-1] = newValue
        }

        private fun process(array: FloatArray): Float{
            if (array.isEmpty()) {
                return 0F
            }
            if (array.size < 2) {
                return array[0]
            }
            return mean(array)
        }

        private fun mean(array: FloatArray): Float {
            var sum = 0F
            var allMoreThan = true
            var allLessThan = true
            for (i in array) {
                if (i > -360) {
                    allLessThan = false
                }
                if (i < 360) {
                    allMoreThan = false
                }
                sum += i
            }
            if (allLessThan) {
                for (i in 0 until array.size) {
                    array[i] += 360F
                }
            } else if (allMoreThan) {
                for (i in 0 until array.size) {
                    array[i] -= 360F
                }
            }
            return sum / array.size
        }

    }

}