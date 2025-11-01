package com.joaosant0s.pockettools.core.floating

import android.annotation.SuppressLint
import android.app.Service.LAYOUT_INFLATER_SERVICE
import android.content.Context
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.content.Context.WINDOW_SERVICE
import android.widget.FrameLayout
import androidx.core.content.edit

import kotlin.math.absoluteValue

import com.joaosant0s.pockettools.R

@SuppressLint("ClickableViewAccessibility")
class FloatingArea(service: FloatingService) {

    private val limitOffset = 78
    private val clickThreshold = 10
    private val floatingPrefs = "floating_prefs"

    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    private var context: FloatingService = service
    private var floatingArea: FrameLayout
    private var floatingGridTools: FloatingGridTools

    private var dragEnabled: Boolean = true

    private var windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

    init {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("InflateParams")
        floatingArea = inflater.inflate(R.layout.floating_action_area, null) as FrameLayout
        floatingGridTools = FloatingGridTools(context)

        floatingArea.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val floatingAreaParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        floatingAreaParams.gravity = Gravity.TOP or Gravity.START
        val position = loadPosition()
        floatingAreaParams.x = position.first
        floatingAreaParams.y = position.second

        floatingArea.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (!dragEnabled) return false

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = floatingAreaParams.x
                        initialY = floatingAreaParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val newX = (initialX + (event.rawX - initialTouchX)).toInt()
                        val newY = (initialY + (event.rawY - initialTouchY)).toInt()

                        // Clamp within screen limits (subtract button size)
                        floatingAreaParams.x = newX.coerceIn(0, screenWidth - floatingArea.width)
                        floatingAreaParams.y = newY.coerceIn(0, screenHeight - floatingArea.height)

                        windowManager.updateViewLayout(floatingArea, floatingAreaParams)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val dx = event.rawX - initialTouchX
                        val dy = event.rawY - initialTouchY

                        if (dx.absoluteValue < clickThreshold && dy.absoluteValue < clickThreshold) {
                            v.performClick()
                        } else {
                            floatingAreaParams.x =
                                if (floatingAreaParams.x + floatingArea.width / 2 < screenWidth / 2) {
                                    limitOffset
                                } else {
                                    screenWidth - floatingArea.width - limitOffset
                                }

                            floatingAreaParams.y =
                                floatingAreaParams.y.coerceIn(0, screenHeight - floatingArea.height)

                            windowManager.updateViewLayout(floatingArea, floatingAreaParams)
                        }

                        savePosition(floatingAreaParams.x, floatingAreaParams.y)
                        return true
                    }
                }
                return false
            }
        })

        floatingArea.setOnClickListener {
            if (floatingGridTools.isVisible()) {
                floatingGridTools.setVisibility(View.INVISIBLE)
                dragEnabled = true
            } else {
                dragEnabled = false

                val xPos = floatingAreaParams.x + floatingArea.width + 25
                val yPos = floatingAreaParams.y + floatingArea.height / 2

                floatingGridTools.setPosition(xPos, yPos)
                floatingGridTools.setVisibility(View.VISIBLE)
            }
        }

        windowManager.addView(floatingArea, floatingAreaParams)
    }

    fun destroy() {
        floatingGridTools.destroy()
        windowManager.removeView(floatingArea)
    }

    private fun savePosition(x: Int, y: Int) {
        val prefs = context.getSharedPreferences(floatingPrefs, Context.MODE_PRIVATE)
        prefs.edit() { putInt("x_pos", x).putInt("y_pos", y) }
    }

    private fun loadPosition(): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(floatingPrefs, Context.MODE_PRIVATE)
        val x = prefs.getInt("x_pos", limitOffset) // default X
        val y = prefs.getInt("y_pos", 100) // default Y
        return Pair(x, y)
    }
}