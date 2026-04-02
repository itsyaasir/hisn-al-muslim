package com.yasir.hisnalmuslim.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.yasir.hisnalmuslim.R
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.ReminderKind
import com.yasir.hisnalmuslim.core.model.displayTitle
import com.yasir.hisnalmuslim.core.model.reminderPreferences
import com.yasir.hisnalmuslim.core.model.shouldDeliverReminder
import com.yasir.hisnalmuslim.data.repository.DhikrRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dhikrRepository: DhikrRepository,
) {
    suspend fun syncAll(settings: AppSettings) {
        sync(ReminderKind.MORNING, settings)
        sync(ReminderKind.EVENING, settings)
        sync(ReminderKind.SLEEPING, settings)
        sync(ReminderKind.REPEATABLE, settings)
    }

    suspend fun sync(
        kind: ReminderKind,
        settings: AppSettings,
        nowMillis: Long = System.currentTimeMillis(),
    ) {
        if (kind == ReminderKind.REPEATABLE) {
            syncRepeatable(settings, nowMillis)
            return
        }

        val config = settings.reminderPreferences(kind)
        if (!settings.shouldDeliverReminder(kind)) {
            cancel(context, kind)
            return
        }

        val collection = dhikrRepository.getCollectionById(checkNotNull(kind.fixedCollectionId))
            ?: run {
                cancel(context, kind)
                return
            }

        schedule(
            context = context,
            payload = ReminderPayload(
                kind = kind,
                title = collection.displayTitle(settings),
                dhikrId = collection.firstDhikrId,
                collectionId = collection.id,
                triggerMinutes = config.triggerMinutes,
                ringtoneUri = config.ringtoneUri,
            ),
            nowMillis = nowMillis,
        )
    }

    private suspend fun syncRepeatable(
        settings: AppSettings,
        nowMillis: Long,
    ) {
        val config = settings.reminderPreferences(ReminderKind.REPEATABLE)
        if (!settings.shouldDeliverReminder(ReminderKind.REPEATABLE)) {
            cancel(context, ReminderKind.REPEATABLE)
            return
        }

        val dhikr = dhikrRepository.getRandomDhikr()
            ?: run {
                cancel(context, ReminderKind.REPEATABLE)
                return
            }

        schedule(
            context = context,
            payload = ReminderPayload(
                kind = ReminderKind.REPEATABLE,
                title = context.getString(R.string.repeatable_reminder_notification_title),
                dhikrId = dhikr.id,
                collectionId = dhikr.collectionId,
                triggerMinutes = config.triggerMinutes,
                ringtoneUri = config.ringtoneUri,
            ),
            nowMillis = nowMillis,
        )
    }

    companion object {
        fun cancel(
            context: Context,
            kind: ReminderKind,
        ) {
            val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
            alarmManager.cancel(alarmPendingIntent(context, kind))
            NotificationManagerCompat.from(context).cancel(kind.notificationId)
        }

        fun schedule(
            context: Context,
            payload: ReminderPayload,
            nowMillis: Long = System.currentTimeMillis(),
        ) {
            val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
            val pendingIntent = alarmPendingIntent(context, payload)
            alarmManager.cancel(pendingIntent)
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextTriggerAtMillis(payload, nowMillis),
                pendingIntent,
            )
        }

        private fun alarmPendingIntent(
            context: Context,
            kind: ReminderKind,
        ): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                kind.requestCode,
                ReminderContract.createAlarmIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private fun alarmPendingIntent(
            context: Context,
            payload: ReminderPayload,
        ): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                payload.kind.requestCode,
                ReminderContract.createAlarmIntent(context, payload),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        private fun nextTriggerAtMillis(
            payload: ReminderPayload,
            nowMillis: Long,
        ): Long {
            if (payload.kind == ReminderKind.REPEATABLE) {
                return nowMillis + (payload.triggerMinutes * 60_000L)
            }

            val calendar = Calendar.getInstance().apply {
                timeInMillis = nowMillis
                set(Calendar.HOUR_OF_DAY, payload.triggerMinutes / 60)
                set(Calendar.MINUTE, payload.triggerMinutes % 60)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= nowMillis) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            return calendar.timeInMillis
        }
    }
}
