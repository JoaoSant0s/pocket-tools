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
import android.content.Intent
import android.widget.FrameLayout
import androidx.core.content.edit

import kotlin.math.absoluteValue

import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R
import androidx.core.view.isVisible

@SuppressLint("ClickableViewAccessibility")
class FloatingButton(service: FloatingService) {

    private val limitOffset = 78
    private val clickThreshold = 10
    private val floatingPrefs = "floating_prefs"

    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    private var context: FloatingService = service
    private var floatingArea: FrameLayout
    private var grid: FrameLayout
    private var dragEnabled: Boolean = true

    private var windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

    init {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("InflateParams")
        floatingArea = inflater.inflate(R.layout.floating_action_area, null) as FrameLayout
        grid = inflater.inflate(R.layout.floating_panel, null) as FrameLayout
        grid.visibility = View.INVISIBLE

        grid.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        floatingArea.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val buttonParams = WindowManager.LayoutParams(
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

        val gridParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        buttonParams.gravity = Gravity.TOP or Gravity.START
        val position = loadPosition()
        buttonParams.x = position.first
        buttonParams.y = position.second

        floatingArea.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (!dragEnabled) return false

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = buttonParams.x
                        initialY = buttonParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val newX = (initialX + (event.rawX - initialTouchX)).toInt()
                        val newY = (initialY + (event.rawY - initialTouchY)).toInt()

                        // Clamp within screen limits (subtract button size)
                        buttonParams.x = newX.coerceIn(0, screenWidth - floatingArea.width)
                        buttonParams.y = newY.coerceIn(0, screenHeight - floatingArea.height)

                        windowManager.updateViewLayout(floatingArea, buttonParams)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val dx = event.rawX - initialTouchX
                        val dy = event.rawY - initialTouchY

                        if (dx.absoluteValue < clickThreshold && dy.absoluteValue < clickThreshold) {
                            v.performClick()
                        } else {
                            buttonParams.x =
                                if (buttonParams.x + floatingArea.width / 2 < screenWidth / 2) {
                                    limitOffset
                                } else {
                                    screenWidth - floatingArea.width - limitOffset
                                }

                            buttonParams.y =
                                buttonParams.y.coerceIn(0, screenHeight - floatingArea.height)

                            windowManager.updateViewLayout(floatingArea, buttonParams)
                        }

                        savePosition(buttonParams.x, buttonParams.y)
                        return true
                    }
                }
                return false
            }
        })

        floatingArea.setOnClickListener {
            if (grid.isVisible) {
                grid.visibility = View.INVISIBLE
                dragEnabled = true
            } else {
                dragEnabled = false
                gridParams.x = buttonParams.x + floatingArea.width + 25
                gridParams.y = buttonParams.y + floatingArea.height / 2 - grid.height / 2
                windowManager.updateViewLayout(grid, gridParams)

                grid.visibility = View.VISIBLE
            }
//            openMainActivity()
        }

//        floatingArea.addView(grid)
        windowManager.addView(grid, gridParams)
        windowManager.addView(floatingArea, buttonParams)
    }

    fun destroy() {
        windowManager.removeView(floatingArea)
        windowManager.removeView(grid)
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

    private fun openMainActivity() {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        context.startActivity(intent)
    }


}