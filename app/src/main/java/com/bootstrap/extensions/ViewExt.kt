package com.bootstrap.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isGone
import androidx.core.view.isVisible

fun View.onClick(on: () -> Unit) = setOnClickListener { on() }

fun String?.isInvalidEmail(): Boolean {
    if (this == null) return true
    return !android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showHide() {
    isVisible = isGone
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}