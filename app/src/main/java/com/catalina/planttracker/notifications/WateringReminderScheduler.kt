package com.catalina.planttracker.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.catalina.planttracker.MainActivity
import com.catalina.planttracker.R
import com.catalina.planttracker.data.notifications.NotificationPreferenceManager
import com.catalina.planttracker.model.Plant
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object WateringReminderScheduler {
    const val ACTION_WATERING_REMINDER = "com.catalina.planttracker.WATERING_REMINDER"
    const val EXTRA_PLANT_ID = "plant_id"
    const val EXTRA_PLANT_NAME = "plant_name"
    private const val EXTRA_DUE_AT = "due_at"

    private const val CHANNEL_ID = "watering_reminders"
    private const val REMINDER_PREFERENCES = "watering_reminder_schedule"
    private const val KEY_REMINDERS = "scheduled_reminders"
    private const val REMINDER_HOUR = 9
    private const val IMMEDIATE_DELAY_MILLIS = 15_000L
    private const val ONE_DAY_MILLIS = 24L * 60L * 60L * 1000L

    fun scheduleForPlants(context: Context, plants: List<Plant>) {
        val appContext = context.applicationContext
        if (!NotificationPreferenceManager(appContext).areWateringNotificationsEnabled()) {
            cancelStoredReminders(appContext)
            return
        }

        createNotificationChannel(appContext)

        val knownPlantIds = plants.map { it.id }.toSet()
        storedReminders(appContext)
            .filterNot { it.plantId in knownPlantIds }
            .forEach { cancelReminder(appContext, it.plantId) }

        val reminders = plants.mapNotNull { plant ->
            val dueAt = dueAtMillis(plant) ?: return@mapNotNull null
            ScheduledReminder(
                plantId = plant.id,
                plantName = plant.name,
                dueAtMillis = dueAt
            )
        }

        reminders.forEach { scheduleReminder(appContext, it) }
        saveReminders(appContext, reminders)
    }

    fun cancelStoredReminders(context: Context) {
        val appContext = context.applicationContext
        storedReminders(appContext).forEach { cancelReminder(appContext, it.plantId) }
        saveReminders(appContext, emptyList())
    }

    fun restoreStoredReminders(context: Context) {
        val appContext = context.applicationContext
        if (!NotificationPreferenceManager(appContext).areWateringNotificationsEnabled()) return

        createNotificationChannel(appContext)
        val now = System.currentTimeMillis()
        val reminders = storedReminders(appContext).map { reminder ->
            if (reminder.dueAtMillis <= now) {
                reminder.copy(dueAtMillis = now + IMMEDIATE_DELAY_MILLIS)
            } else {
                reminder
            }
        }

        reminders.forEach { scheduleReminder(appContext, it) }
        saveReminders(appContext, reminders)
    }

    fun showWateringNotification(context: Context, plantId: Int, plantName: String) {
        val appContext = context.applicationContext
        if (!NotificationPreferenceManager(appContext).areWateringNotificationsEnabled()) return
        if (!canPostNotifications(appContext)) return

        createNotificationChannel(appContext)

        val launchIntent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            appContext,
            plantId,
            launchIntent,
            pendingIntentFlags()
        )

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(appContext.getString(R.string.notification_watering_title, plantName))
            .setContentText(appContext.getString(R.string.notification_watering_message))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(appContext).notify(plantId, notification)
        rescheduleStoredReminderForTomorrow(appContext, plantId)
    }

    private fun scheduleReminder(context: Context, reminder: ScheduledReminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WateringReminderReceiver::class.java).apply {
            action = ACTION_WATERING_REMINDER
            putExtra(EXTRA_PLANT_ID, reminder.plantId)
            putExtra(EXTRA_PLANT_NAME, reminder.plantName)
            putExtra(EXTRA_DUE_AT, reminder.dueAtMillis)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.plantId,
            intent,
            pendingIntentFlags()
        )

        val triggerAt = maxOf(reminder.dueAtMillis, System.currentTimeMillis() + IMMEDIATE_DELAY_MILLIS)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    private fun cancelReminder(context: Context, plantId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WateringReminderReceiver::class.java).apply {
            action = ACTION_WATERING_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plantId,
            intent,
            pendingIntentFlags()
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun dueAtMillis(plant: Plant): Long? {
        val frequency = plant.wateringFrequencyDays?.takeIf { it > 0 } ?: return null
        val lastWatered = parsePlantDateMillis(plant.lastWatered) ?: return null
        return Calendar.getInstance().apply {
            timeInMillis = lastWatered
            add(Calendar.DAY_OF_YEAR, frequency)
            set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun parsePlantDateMillis(value: String?): Long? {
        if (value.isNullOrBlank()) return null
        return runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .parse(value.substringBefore("T"))
                ?.time
        }.getOrNull()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_watering_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_watering_description)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun canPostNotifications(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pendingIntentFlags(): Int {
        return PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    }

    private fun storedReminders(context: Context): List<ScheduledReminder> {
        val raw = reminderPreferences(context).getString(KEY_REMINDERS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        ScheduledReminder(
                            plantId = item.getInt("plantId"),
                            plantName = item.getString("plantName"),
                            dueAtMillis = item.getLong("dueAtMillis")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun saveReminders(context: Context, reminders: List<ScheduledReminder>) {
        val array = JSONArray()
        reminders.forEach { reminder ->
            array.put(
                JSONObject()
                    .put("plantId", reminder.plantId)
                    .put("plantName", reminder.plantName)
                    .put("dueAtMillis", reminder.dueAtMillis)
            )
        }
        reminderPreferences(context).edit()
            .putString(KEY_REMINDERS, array.toString())
            .apply()
    }

    private fun rescheduleStoredReminderForTomorrow(context: Context, plantId: Int) {
        val reminders = storedReminders(context).map { reminder ->
            if (reminder.plantId == plantId) {
                reminder.copy(dueAtMillis = System.currentTimeMillis() + ONE_DAY_MILLIS)
            } else {
                reminder
            }
        }
        reminders.firstOrNull { it.plantId == plantId }?.let { scheduleReminder(context, it) }
        saveReminders(context, reminders)
    }

    private fun reminderPreferences(context: Context) = context.getSharedPreferences(
        REMINDER_PREFERENCES,
        Context.MODE_PRIVATE
    )

    private data class ScheduledReminder(
        val plantId: Int,
        val plantName: String,
        val dueAtMillis: Long
    )
}
