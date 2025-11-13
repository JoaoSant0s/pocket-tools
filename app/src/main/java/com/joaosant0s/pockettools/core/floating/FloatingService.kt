package com.joaosant0s.pockettools.core.floating

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

import com.joaosant0s.pockettools.R

class FloatingService : Service() {

    private lateinit var floatingArea: FloatingArea
    private val NOTIFICATION_ID_FOREGROUND_SERVICE = 1
    private val channelId = "floating_service"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        tryCreateNotification()
        floatingArea = FloatingArea(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        floatingArea.destroy()
    }

    private fun tryCreateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Floating Tool", NotificationManager.IMPORTANCE_LOW)

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Floating Tool active")
                .setSmallIcon(R.drawable.ic_tool)
                .setOngoing(true)
                .build()

            startForeground(NOTIFICATION_ID_FOREGROUND_SERVICE, notification)
        } else {
            startForeground(NOTIFICATION_ID_FOREGROUND_SERVICE, Notification())
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
