package com.liang.lollipop.lcompass.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.liang.lollipop.lcompass.R
import com.liang.lollipop.lsnackbar.LSnackBar
import com.liang.lollipop.lsnackbar.LToastUtil

/**
 * @date: 2018/12/12 00:18
 * @author: lollipop
 * 简单的
 */
inline val Fragment.glide: RequestManager
    get() = Glide.with(this)

inline val Activity.glide: RequestManager
    get() = Glide.with(this)

inline fun Fragment.S(msg: String, btnName: String = "", btnClick: View.OnClickListener? = null) {
    S(this.context, this.view, msg, btnName, btnClick)
}

inline fun Activity.S(msg: String, btnName: String = "", btnClick: View.OnClickListener? = null) {
    val contentParent = findViewById<View>(android.R.id.content) as ViewGroup
    val rootView = if (contentParent.childCount > 0) {
        contentParent.getChildAt(0)
    } else {
        contentParent
    }
    S(this, rootView, msg, btnName, btnClick)
}

inline fun S(context: Context?, view: View?, msg: String, btnName: String, btnClick: View.OnClickListener?) {
    if (view == null) {
        if (context == null) {
            return
        }
        LToastUtil.T(context, msg, Color.WHITE,
                ContextCompat.getColor(context, R.color.colorPrimary), R.mipmap.ic_launcher_round)
        btnClick?.onClick(null)
        return
    }
    val d = if (TextUtils.isEmpty(btnName) || btnClick == null) {
        LSnackBar.LENGTH_SHORT
    } else {
        LSnackBar.LENGTH_LONG
    }
    LSnackBar.make(view, msg, d).setAction(btnName, btnClick)
            .setActionTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
            .setMessageTextColor(Color.WHITE)
            .setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorPrimary))
            .setLogo(R.mipmap.ic_launcher_round).show()
}