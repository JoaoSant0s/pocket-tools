package com.joaosant0s.pockettools.core.floating

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

import com.joaosant0s.pockettools.R

class FloatingService : Service() {

    private lateinit var floatButton: FloatingButton
    private val channelId = "floating_service"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        tryCreateNotification()
        floatButton = FloatingButton(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        floatButton.destroy()
    }

    private fun tryCreateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Floating Tool", NotificationManager.IMPORTANCE_LOW)

            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Floating Tool active")
                .setSmallIcon(R.drawable.ic_tool)
                .build()

            startForeground(1, notification)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
