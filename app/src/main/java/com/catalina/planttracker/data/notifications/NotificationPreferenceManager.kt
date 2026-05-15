package com.catalina.planttracker.data.notifications

import android.content.Context

class NotificationPreferenceManager(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun areWateringNotificationsEnabled(): Boolean {
        return preferences.getBoolean(KEY_WATERING_NOTIFICATIONS_ENABLED, true)
    }

    fun setWateringNotificationsEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_WATERING_NOTIFICATIONS_ENABLED, enabled)
            .apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "notification_preferences"
        private const val KEY_WATERING_NOTIFICATIONS_ENABLED = "watering_notifications_enabled"
    }
}
