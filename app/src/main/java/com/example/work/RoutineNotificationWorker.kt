package com.example.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.R
import com.example.data.local.AppDatabase
import com.example.util.AppFeatureFlags
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class RoutineNotificationWorker(
    private val ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    companion object {
        private const val CHANNEL_ID   = "routine_reminders"
        private const val WORK_TAG     = "routine_notification_daily"
        private const val NOTIF_ID_BASE = 9100

        fun schedule(context: Context) {
            if (!AppFeatureFlags.isRoutineNotifEnabled) return
            val req = PeriodicWorkRequestBuilder<RoutineNotificationWorker>(
                24, TimeUnit.HOURS
            ).addTag(WORK_TAG).build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    req
                )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
        }
    }

    override suspend fun doWork(): Result {
        if (!AppFeatureFlags.isRoutineNotifEnabled) return Result.success()

        val db = AppDatabase.getInstance(ctx)
        val bots = try { db.botDao().getAllBotsList() } catch (_: Exception) { return Result.success() }
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(nm)

        var notifId = NOTIF_ID_BASE
        for (bot in bots) {
            val routines = try {
                db.routineDao().getRoutinesForBot(bot.id)
            } catch (_: Exception) { continue }

            for (routine in routines) {
                if (routine.lastMentionedStoryDate == today) continue
                val dayMatch = routine.dayOfWeek.isNullOrBlank() ||
                        routine.dayOfWeek == LocalDate.now().dayOfWeek.name.lowercase()
                if (!dayMatch) continue

                val launchIntent = ctx.packageManager
                    .getLaunchIntentForPackage(ctx.packageName)
                    ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

                val pi = PendingIntent.getActivity(
                    ctx, notifId, launchIntent ?: Intent(),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notif = NotificationCompat.Builder(ctx, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_vai_logo)
                    .setContentTitle("${bot.aiName} seni bekliyor")
                    .setContentText(routine.routineDescription.take(80))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build()

                nm.notify(notifId++, notif)
            }
        }
        return Result.success()
    }

    private fun ensureChannel(nm: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rutin Hatırlatıcılar",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Günlük rutin etkinlikleri hatırlatır." }
            nm.createNotificationChannel(channel)
        }
    }
}
