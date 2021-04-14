package com.bootstrap.manager

import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferencesManager(
    private val sharedPreferences: SharedPreferences,
) {

    var token: String?
        get() = sharedPreferences.getString(TOKEN, null)
        set(value) = sharedPreferences.edit { putString(TOKEN, value) }

    var deviceUid: String
        get() = sharedPreferences.getString(DEVICE_UID, "") ?: ""
        set(value) = sharedPreferences.edit { putString(DEVICE_UID, value) }

    fun clear() = sharedPreferences.edit { clear() }

    companion object {
        const val DEVICE_UID = "device_uid"
        const val TOKEN = "token"
    }
}