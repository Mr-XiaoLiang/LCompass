package com.liang.lollipop.lcompass.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager

/**
 * @date: 2018/12/12 00:36
 * @author: lollipop
 * Activity的基类
 */
open class BaseActivity2: AppCompatActivity(), View.OnClickListener {

    protected var handler: Handler = Handler {
        if (it == null) {
            return@Handler false
        } else {
            return@Handler onHandler(it)
        }
    }

    override fun onClick(v: View?) {
    }

    open fun onHandler(msg: Message): Boolean {
        return true
    }

    protected fun setFullScreen(isFull: Boolean) {
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        val decorView = window.decorView
        if (isFull) {
            //清除非全屏的flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            //设置全屏的flag
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        } else {
            //清除全屏的flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            //非全屏
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            val uiOptions = 0
            decorView.systemUiVisibility = uiOptions
        }
    }

    protected fun setScreenOrientation(isLandscape: Boolean) {
        requestedOrientation = if (isLandscape) {//横屏
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {//竖屏
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    protected fun setShowActionBar(isShow: Boolean) {
        if (isShow) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun startActivity(intent: Intent, vararg pair: Pair<View, String>) {
        val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pair)
        super.startActivity(intent, optionsCompat.toBundle())
    }

}