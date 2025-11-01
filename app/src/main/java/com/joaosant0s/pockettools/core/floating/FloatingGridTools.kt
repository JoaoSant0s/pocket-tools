package com.joaosant0s.pockettools.core.floating

import android.annotation.SuppressLint
import android.app.Service.LAYOUT_INFLATER_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R

class FloatingGridTools(service: FloatingService) {

    private var context: FloatingService = service
    private var gridToolsArea: FrameLayout
    private var gridParams: WindowManager.LayoutParams

    private var windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager


    init {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("InflateParams")
        gridToolsArea = inflater.inflate(R.layout.floating_panel, null) as FrameLayout
        gridToolsArea.visibility = View.INVISIBLE

        gridToolsArea.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        gridParams = WindowManager.LayoutParams(
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

        windowManager.addView(gridToolsArea, gridParams)
    }

    fun isVisible(): Boolean {
        return gridToolsArea.isVisible
    }

    fun setVisibility(visibility: Int) {
        gridToolsArea.visibility = visibility
    }

    fun setPosition(xPosition: Int, yPosition: Int) {
        gridParams.x = xPosition
        gridParams.y = yPosition - gridToolsArea.height / 2
        windowManager.updateViewLayout(gridToolsArea, gridParams)
    }

    fun destroy() {
        windowManager.removeView(gridToolsArea)
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