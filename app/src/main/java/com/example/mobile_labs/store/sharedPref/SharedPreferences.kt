package com.example.mobile_labs.store.sharedPref

import android.content.Context
import androidx.core.content.edit

class SharedPreferences(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    val notification = sharedPreferences.getBoolean(NOTIFICATION_KEY, false)

    fun setNotification(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(NOTIFICATION_KEY, enabled)
        }
    }

    companion object {
        private const val NOTIFICATION_KEY = "settings.notification"
    }
}