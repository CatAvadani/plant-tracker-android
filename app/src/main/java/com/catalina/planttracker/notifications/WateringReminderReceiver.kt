package com.catalina.planttracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WateringReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != WateringReminderScheduler.ACTION_WATERING_REMINDER) return

        val plantId = intent.getIntExtra(WateringReminderScheduler.EXTRA_PLANT_ID, -1)
        val plantName = intent.getStringExtra(WateringReminderScheduler.EXTRA_PLANT_NAME)
            ?: context.getString(com.catalina.planttracker.R.string.notification_unknown_plant)

        if (plantId > 0) {
            WateringReminderScheduler.showWateringNotification(context, plantId, plantName)
        }
    }
}
