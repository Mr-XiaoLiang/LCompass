package com.liang.lollipop.lcompass.fragment

import android.location.Location
import android.view.View

/**
 * @date: 2018/12/12 00:15
 * @author: lollipop
 * 指南针View的接口
 */
interface CompassView: View.OnClickListener {

    override fun onClick(v: View) {}

    fun onLocationUpdate(location: Location?) {}

    fun onSensorUpdate(z: Float, x: Float, y: Float) {}

    fun onSensorStateUpdate(i: Int) {}

    fun onTypeChange(b: Boolean) {}
    fun onModelChange(b: Boolean) {}
    fun on3DModelChange(b: Boolean) {}
    fun onCameraModelChange(b: Boolean) {}

    fun onCameraOpened() {}

    fun onCameraClosed() {}

    fun onPressureChange(hPa: Float, altitude: Float) {}

}