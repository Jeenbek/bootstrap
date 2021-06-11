package com.bootstrap.extensions

import android.content.res.Resources

val Int.pxToDp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()