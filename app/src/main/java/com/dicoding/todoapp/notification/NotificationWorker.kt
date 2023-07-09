package com.dicoding.todoapp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_DESC
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.todoapp.utils.TASK_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)
    private val context = ctx

    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun doWork(): Result {
        //TODO 14 : If notification preference on, get nearest active task from repository and show notification with pending intent
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val isNotify =
            pref.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        if (isNotify) {
            notificationAllert(applicationContext)
        }
        return Result.success()
    }

    private fun notificationAllert(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val task = TaskRepository.getInstance(this.context).getNearestActiveTask()
        val message = "$channelName ${DateConverter.convertMillisToString(task.dueDateMillis)}"
        val title = task.title
        val pendingIntent : PendingIntent? = getPendingIntent(task)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = NOTIFICATION_CHANNEL_ID
            val channelName = channelName
            val channelDescription = NOTIFICATION_CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            notificationManager.createNotificationChannel(channel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(task.id, notificationBuilder.build())
        } else {
            val builder = NotificationCompat.Builder(applicationContext, channelName ?: "")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            builder.setAutoCancel(true)
            val notification = builder.build()
            notification.flags = Notification.FLAG_AUTO_CANCEL or Notification.FLAG_ONGOING_EVENT
            notificationManager.notify(TASK_NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        const val  TASK_NOTIFICATION_ID = 102
    }
}
