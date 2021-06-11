package com.bootstrap.extensions

import android.text.InputFilter
import android.widget.EditText

inline var EditText.maxLength: Int?
    get() = text.length
    set(value) {
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(value ?: 0))
    }
