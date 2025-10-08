package com.joaosant0s.pockettools.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.content.Context
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import kotlin.math.absoluteValue

import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatButton: ImageButton
    private val limitOffset = 78
    private val clickThreshold = 10 // small movement threshold

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        val channelId = "floating_service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Floating Tool", NotificationManager.IMPORTANCE_LOW)

            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Floating Tool active")
                .setSmallIcon(R.drawable.ic_tool)
                .build()

            startForeground(1, notification)
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatButton = inflater.inflate(R.layout.floating_button, null) as ImageButton

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        val position = loadPosition()
        params.x = position.first
        params.y = position.second

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        floatButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val newX = (initialX + (event.rawX - initialTouchX)).toInt()
                        val newY = (initialY + (event.rawY - initialTouchY)).toInt()

                        // Clamp within screen limits (subtract button size)
                        params.x = newX.coerceIn(0, screenWidth - floatButton.width)
                        params.y = newY.coerceIn(0, screenHeight - floatButton.height)

                        windowManager.updateViewLayout(floatButton, params)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val dx = event.rawX - initialTouchX
                        val dy = event.rawY - initialTouchY

                        if (dx.absoluteValue < clickThreshold && dy.absoluteValue < clickThreshold) {
                            v.performClick()
                            return true
                        } else {
                            params.x = if (params.x + floatButton.width / 2 < screenWidth / 2) {
                                limitOffset // snap to left
                            } else {
                                screenWidth - floatButton.width - limitOffset // snap to right
                            }

                            // Optionally, keep Y as is but clamp to top/bottom
                            params.y = params.y.coerceIn(0, screenHeight - floatButton.height)

                            windowManager.updateViewLayout(floatButton, params)
                        }

                        savePosition(params.x, params.y)
                    }
                }
                return false
            }
        })

        floatButton.setOnClickListener {
            openMainActivity()
        }

        windowManager.addView(floatButton, params)
    }

    private fun savePosition(x: Int, y: Int) {
        val prefs = getSharedPreferences("floating_prefs", Context.MODE_PRIVATE)
        prefs.edit() { putInt("x_pos", x).putInt("y_pos", y) }
    }

    private fun loadPosition(): Pair<Int, Int> {
        val prefs = getSharedPreferences("floating_prefs", Context.MODE_PRIVATE)
        val x = prefs.getInt("x_pos", limitOffset) // default X
        val y = prefs.getInt("y_pos", 100) // default Y
        return Pair(x, y)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatButton.isInitialized) windowManager.removeView(floatButton)
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

//class AccessibleFloatButton(context: Context, attrs: AttributeSet? = null) : ImageButton(context, attrs) {
//    override fun performClick(): Boolean {
//        return super.performClick()
//    }
//}
