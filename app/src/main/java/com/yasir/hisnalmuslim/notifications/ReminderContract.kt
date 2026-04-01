package com.yasir.hisnalmuslim.notifications

import android.content.Context
import android.content.Intent
import com.yasir.hisnalmuslim.app.MainActivity
import com.yasir.hisnalmuslim.core.model.ReminderKind

data class ReminderPayload(
    val kind: ReminderKind,
    val title: String,
    val dhikrId: Long,
    val collectionId: Long,
    val triggerMinutes: Int,
    val ringtoneUri: String?,
)

data class NotificationOpenTarget(
    val dhikrId: Long,
    val collectionId: Long,
)

internal val ReminderKind.fixedCollectionId: Long?
    get() = when (this) {
        ReminderKind.MORNING -> 1L
        ReminderKind.EVENING -> 2L
        ReminderKind.SLEEPING -> 4L
        ReminderKind.REPEATABLE -> null
    }

internal val ReminderKind.requestCode: Int
    get() = when (this) {
        ReminderKind.MORNING -> 1001
        ReminderKind.EVENING -> 1002
        ReminderKind.SLEEPING -> 1003
        ReminderKind.REPEATABLE -> 1004
    }

internal val ReminderKind.notificationId: Int
    get() = when (this) {
        ReminderKind.MORNING -> 1001
        ReminderKind.EVENING -> 1002
        ReminderKind.SLEEPING -> 1003
        ReminderKind.REPEATABLE -> 1004
    }

internal val ReminderKind.channelIdPrefix: String
    get() = when (this) {
        ReminderKind.MORNING -> "morning_reminder"
        ReminderKind.EVENING -> "evening_reminder"
        ReminderKind.SLEEPING -> "sleeping_reminder"
        ReminderKind.REPEATABLE -> "repeatable_reminder"
    }

internal object ReminderContract {
    private const val ACTION_TRIGGER = "com.yasir.hisnalmuslim.notifications.ACTION_TRIGGER_REMINDER"
    private const val ACTION_OPEN = "com.yasir.hisnalmuslim.notifications.ACTION_OPEN_REMINDER"

    private const val EXTRA_KIND = "extra_kind"
    private const val EXTRA_TITLE = "extra_title"
    private const val EXTRA_DHIKR_ID = "extra_dhikr_id"
    private const val EXTRA_COLLECTION_ID = "extra_collection_id"
    private const val EXTRA_TRIGGER_MINUTES = "extra_trigger_minutes"
    private const val EXTRA_RINGTONE_URI = "extra_ringtone_uri"

    fun createAlarmIntent(
        context: Context,
        payload: ReminderPayload? = null,
    ): Intent {
        return Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_TRIGGER
            if (payload != null) {
                putExtra(EXTRA_KIND, payload.kind.name)
                putExtra(EXTRA_TITLE, payload.title)
                putExtra(EXTRA_DHIKR_ID, payload.dhikrId)
                putExtra(EXTRA_COLLECTION_ID, payload.collectionId)
                putExtra(EXTRA_TRIGGER_MINUTES, payload.triggerMinutes)
                putExtra(EXTRA_RINGTONE_URI, payload.ringtoneUri)
            }
        }
    }

    fun createOpenAppIntent(
        context: Context,
        payload: ReminderPayload,
    ): Intent {
        return Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_DHIKR_ID, payload.dhikrId)
            putExtra(EXTRA_COLLECTION_ID, payload.collectionId)
        }
    }

    fun extractPayload(intent: Intent?): ReminderPayload? {
        val sourceIntent = intent ?: return null
        if (sourceIntent.action != ACTION_TRIGGER) return null

        val kind = sourceIntent.getStringExtra(EXTRA_KIND)
            ?.let(ReminderKind::valueOf)
            ?: return null
        val title = sourceIntent.getStringExtra(EXTRA_TITLE) ?: return null
        val dhikrId = sourceIntent.getLongExtra(EXTRA_DHIKR_ID, -1L)
        val collectionId = sourceIntent.getLongExtra(EXTRA_COLLECTION_ID, -1L)
        val triggerMinutes = sourceIntent.getIntExtra(EXTRA_TRIGGER_MINUTES, -1)
        if (dhikrId <= 0L || collectionId <= 0L || triggerMinutes !in 0..(24 * 60 - 1)) {
            return null
        }
        return ReminderPayload(
            kind = kind,
            title = title,
            dhikrId = dhikrId,
            collectionId = collectionId,
            triggerMinutes = triggerMinutes,
            ringtoneUri = sourceIntent.getStringExtra(EXTRA_RINGTONE_URI),
        )
    }

    fun extractOpenTarget(intent: Intent?): NotificationOpenTarget? {
        val sourceIntent = intent ?: return null
        if (sourceIntent.action != ACTION_OPEN) return null

        val dhikrId = sourceIntent.getLongExtra(EXTRA_DHIKR_ID, -1L)
        val collectionId = sourceIntent.getLongExtra(EXTRA_COLLECTION_ID, -1L)
        if (dhikrId <= 0L || collectionId <= 0L) return null

        return NotificationOpenTarget(
            dhikrId = dhikrId,
            collectionId = collectionId,
        )
    }
}
