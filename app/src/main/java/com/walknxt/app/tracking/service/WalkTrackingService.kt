package com.walknxt.app.tracking.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.walknxt.app.WalkNxtApplication
import com.walknxt.app.domain.model.SessionState
import kotlinx.coroutines.*

class WalkTrackingService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "WalkTrackingChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        
        val container = (application as WalkNxtApplication).container
        
        when (action) {
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, buildNotification())
                serviceScope.launch {
                    container.trackingEngine.start()
                }
            }
            ACTION_PAUSE -> {
                serviceScope.launch {
                    container.trackingEngine.pause()
                }
            }
            ACTION_RESUME -> {
                serviceScope.launch {
                    container.trackingEngine.resume()
                }
            }
            ACTION_STOP -> {
                serviceScope.launch {
                    container.trackingEngine.finish()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WalkNxt Tracking Active")
            .setContentText("Your walk is being tracked offline.")
            .setSmallIcon(android.R.drawable.ic_menu_compass) // Placeholder icon
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Walk Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used to track walks in the background."
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
