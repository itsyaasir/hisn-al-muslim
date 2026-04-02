package com.yasir.hisnalmuslim.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.yasir.hisnalmuslim.R
import com.yasir.hisnalmuslim.core.model.ReminderKind
import com.yasir.hisnalmuslim.core.model.shouldDeliverReminder
import com.yasir.hisnalmuslim.data.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val payload = ReminderContract.extractPayload(intent) ?: return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = settingsRepository.observeSettings().first()
                reminderScheduler.sync(payload.kind, settings)
                if (!settings.shouldDeliverReminder(payload.kind)) {
                    NotificationManagerCompat.from(context).cancel(payload.kind.notificationId)
                    return@launch
                }

                if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS,
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@launch
                }

                val channelId = ensureChannel(context, payload)
                val openPendingIntent = PendingIntent.getActivity(
                    context,
                    payload.kind.notificationId,
                    ReminderContract.createOpenAppIntent(context, payload),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification_small)
                    .setContentTitle(payload.title)
                    .setContentIntent(openPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .apply {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            setSound(resolveSoundUri(payload.ringtoneUri))
                        }
                    }
                    .build()

                NotificationManagerCompat.from(context).notify(payload.kind.notificationId, notification)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun ensureChannel(
        context: Context,
        payload: ReminderPayload,
    ): String {
        val channelId = buildChannelId(payload)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return channelId

        val notificationManager = context.getSystemService(NotificationManager::class.java) ?: return channelId
        notificationManager.notificationChannels
            .asSequence()
            .filter { it.id.startsWith(payload.kind.channelIdPrefix) && it.id != channelId }
            .forEach { notificationManager.deleteNotificationChannel(it.id) }
        if (notificationManager.getNotificationChannel(channelId) != null) return channelId

        val soundUri = resolveSoundUri(payload.ringtoneUri)
        val soundAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                payload.kind.channelName(context),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = payload.kind.channelDescription(context)
                setSound(soundUri, soundAttributes)
            },
        )

        return channelId
    }

    private fun resolveSoundUri(ringtoneUri: String?): Uri {
        return ringtoneUri?.let(Uri::parse)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }

    private fun buildChannelId(payload: ReminderPayload): String {
        val suffix = payload.ringtoneUri?.hashCode()?.toUInt()?.toString(16)
        return if (suffix == null) {
            payload.kind.channelIdPrefix
        } else {
            "${payload.kind.channelIdPrefix}_$suffix"
        }
    }

    private fun ReminderKind.channelName(context: Context): String {
        return when (this) {
            ReminderKind.MORNING -> context.getString(R.string.morning_reminder_channel_name)
            ReminderKind.EVENING -> context.getString(R.string.evening_reminder_channel_name)
            ReminderKind.SLEEPING -> context.getString(R.string.sleeping_reminder_channel_name)
            ReminderKind.REPEATABLE -> context.getString(R.string.repeatable_reminder_channel_name)
        }
    }

    private fun ReminderKind.channelDescription(context: Context): String {
        return when (this) {
            ReminderKind.MORNING -> context.getString(R.string.morning_reminder_channel_description)
            ReminderKind.EVENING -> context.getString(R.string.evening_reminder_channel_description)
            ReminderKind.SLEEPING -> context.getString(R.string.sleeping_reminder_channel_description)
            ReminderKind.REPEATABLE -> context.getString(R.string.repeatable_reminder_channel_description)
        }
    }
}
