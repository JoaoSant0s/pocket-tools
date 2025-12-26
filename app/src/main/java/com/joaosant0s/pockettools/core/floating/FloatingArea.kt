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
import android.content.res.Configuration
import android.widget.FrameLayout
import androidx.core.content.edit

import kotlin.math.absoluteValue

import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.core.EventNames
import com.joaosant0s.pockettools.utils.events.EventListener

@SuppressLint("ClickableViewAccessibility")
class FloatingArea(service: FloatingService) : EventListener {

    private enum class FloatingOrientation {
        Left,
        Right
    }

    private val limitOffset = 78
    private val clickThreshold = 10
    private val floatingPrefs = "floating_prefs"

    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    private val context: FloatingService = service

    private var floatingArea: FrameLayout
    private var floatingGridTools: FloatingGridTools

    private var dragEnabled: Boolean = true
    private var floatingOrientation = FloatingOrientation.Left

    private val deviceOrientation: Int
        get() = context.resources.configuration.orientation

    private val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

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

    init {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("InflateParams")
        floatingArea = inflater.inflate(R.layout.floating_action_area, null) as FrameLayout
        floatingGridTools = FloatingGridTools(context)

        floatingArea.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        floatingAreaParams.gravity = Gravity.TOP or Gravity.START
        val position = loadPosition()
        floatingAreaParams.x = position.first
        floatingAreaParams.y = position.second
        updateOrientation()

        floatingArea.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            private var moveX = 0
            private var moveY = 0

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
                        moveX = (initialX + (event.rawX - initialTouchX)).toInt()
                        moveY = (initialY + (event.rawY - initialTouchY)).toInt()

                        val width : Int
                        val height : Int
                        
                        if(deviceOrientation == Configuration.ORIENTATION_PORTRAIT){
                            width = screenWidth - floatingArea.width
                            height = screenHeight - floatingArea.height
                        }else{
                            width = screenHeight - floatingArea.height
                            height = screenWidth - floatingArea.width
                        }

                        // Clamp within screen limits (subtract button size)
                        updateFloatingAreaPosition(
                            moveX.coerceIn(0, width),
                            moveY.coerceIn(0, height)
                        )

                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val dx = event.rawX - initialTouchX
                        val dy = event.rawY - initialTouchY

                        if (dx.absoluteValue < clickThreshold && dy.absoluteValue < clickThreshold) {
                            v.performClick()
                        } else {
                            val xPosition : Int
                            val yPosition : Int

                            if(deviceOrientation == Configuration.ORIENTATION_PORTRAIT){
                                xPosition =
                                    if (floatingAreaParams.x + floatingArea.width / 2 < screenWidth / 2) {
                                        limitOffset
                                    } else {
                                        screenWidth - floatingArea.width - limitOffset
                                    }
                                yPosition = floatingAreaParams.y.coerceIn(0, screenHeight - floatingArea.height)
                            }else{
                                yPosition =
                                    if (floatingAreaParams.y + floatingArea.height / 2 < screenWidth / 2) {
                                        limitOffset
                                    } else {
                                        screenWidth - floatingArea.width - limitOffset
                                    }
                                xPosition = floatingAreaParams.x.coerceIn(limitOffset, screenHeight - floatingArea.height - limitOffset)
                            }

                            updateOrientation()
                            updateFloatingAreaPosition(
                                xPosition,
                                yPosition
                            )
                            savePosition(floatingAreaParams.x, floatingAreaParams.y)
                        }

                        return true
                    }
                }
                return false
            }
        })

        floatingArea.setOnClickListener {
            if (floatingGridTools.isVisible()) {
                hideFloatingGridTools()
            } else {
                dragEnabled = false

                val xPos =
                    floatingAreaParams.x + if (floatingOrientation == FloatingOrientation.Left) {
                        floatingArea.width + 25
                    } else {
                        -(floatingArea.width + 420)
                    }

                val yPos = floatingAreaParams.y + floatingArea.height / 2

                floatingGridTools.setPosition(xPos, yPos)
                floatingGridTools.setVisibility(View.VISIBLE)
            }
        }

        windowManager.addView(floatingArea, floatingAreaParams)
        addListener()
    }

    private fun updateOrientation() {
        floatingOrientation = if (floatingAreaParams.x + floatingArea.width / 2 < screenWidth / 2) {
            FloatingOrientation.Left
        } else {
            FloatingOrientation.Right
        }
    }

    private fun updateFloatingAreaPosition(xPosition: Int, yPosition: Int) {
        floatingAreaParams.x = xPosition
        floatingAreaParams.y = yPosition

        windowManager.updateViewLayout(floatingArea, floatingAreaParams)
    }

    private fun hideFloatingGridTools() {
        floatingGridTools.setVisibility(View.INVISIBLE)
        dragEnabled = true
    }

    fun destroy() {
        removeListener()
        floatingGridTools.destroy()
        windowManager.removeView(floatingArea)
    }

    private fun savePosition(x: Int, y: Int) {
        val prefs = context.getSharedPreferences(floatingPrefs, Context.MODE_PRIVATE)
        prefs.edit { putInt("x_pos", x).putInt("y_pos", y) }
    }

    private fun loadPosition(): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(floatingPrefs, Context.MODE_PRIVATE)
        val x = prefs.getInt("x_pos", limitOffset) // default X
        val y = prefs.getInt("y_pos", 100) // default Y
        return Pair(x, y)
    }

    override fun onEventTriggered(eventName: String) {
        when (eventName) {
            EventNames.LOCK_SCREEN_ACTIVATED -> hideFloatingGridTools()
            EventNames.LOCK_SCREEN_PERMISSION_UPDATED -> floatingGridTools.refreshTools()
        }
    }
}