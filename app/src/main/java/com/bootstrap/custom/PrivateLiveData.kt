package com.bootstrap.custom

import androidx.lifecycle.LiveData

open class PrivateLiveData<T>() : LiveData<T>() {
    constructor(default: T) : this() {
        set(default)
    }

    internal fun set(value: T?) = setValue(value)
    internal fun post(value: T?) = postValue(value)
}