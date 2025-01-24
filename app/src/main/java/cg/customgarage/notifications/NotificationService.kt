package cg.customgarage.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import cg.customgarage.MainActivity
import cg.customgarage.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val maintenanceChannel = NotificationChannel(
                CHANNEL_MAINTENANCE,
                context.getString(R.string.channel_maintenance_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_maintenance_description)
            }

            val projectChannel = NotificationChannel(
                CHANNEL_PROJECT,
                context.getString(R.string.channel_project_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_project_description)
            }

            notificationManager.createNotificationChannels(listOf(maintenanceChannel, projectChannel))
        }
    }

    fun showMaintenanceNotification(vehicleName: String, maintenanceDescription: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MAINTENANCE)
            .setSmallIcon(R.drawable.ic_maintenance)
            .setContentTitle(context.getString(R.string.maintenance_due, vehicleName))
            .setContentText(maintenanceDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(MAINTENANCE_NOTIFICATION_ID, notification)
    }

    fun showProjectUpdateNotification(projectName: String, status: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PROJECT)
            .setSmallIcon(R.drawable.ic_project)
            .setContentTitle(context.getString(R.string.project_update, projectName))
            .setContentText(status)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(PROJECT_NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_MAINTENANCE = "maintenance_channel"
        private const val CHANNEL_PROJECT = "project_channel"
        private const val MAINTENANCE_NOTIFICATION_ID = 1
        private const val PROJECT_NOTIFICATION_ID = 2
    }
} 