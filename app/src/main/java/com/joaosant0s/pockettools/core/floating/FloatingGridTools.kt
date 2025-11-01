package com.joaosant0s.pockettools.core.floating

import android.annotation.SuppressLint
import android.app.Service.LAYOUT_INFLATER_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.tools.ToolWrapper
import com.joaosant0s.pockettools.tools.ToolsBuilder

class FloatingGridTools(service: FloatingService) {


    private val context: FloatingService = service
    private val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    private val themedContext = ContextThemeWrapper(context, R.style.Theme_PocketTools)

    private val toolsList: LinearLayout
    private var toolsBuilder: ToolsBuilder

    private val gridParams: WindowManager.LayoutParams
    private val gridToolsArea: FrameLayout

    init {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("InflateParams")
        gridToolsArea = inflater.inflate(R.layout.floating_panel, null) as FrameLayout
        gridToolsArea.visibility = View.INVISIBLE
        toolsList = gridToolsArea.findViewById(R.id.floating_tool_list)

        toolsBuilder = ToolsBuilder(themedContext, toolsList)

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

        setupTools()
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

    private fun setupTools() {
        val hasFlash = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        val backButton = ToolWrapper.createTextButton(
            themedContext,
            ToolWrapper.baseLayoutParams,
            R.string.back_application_button
        )
        {
            openMainActivity()
        }

        toolsList.addView(backButton)

        toolsBuilder = toolsBuilder.addLockScreen().addVolume()
        if (hasFlash) toolsBuilder = toolsBuilder.addLantern()
        toolsBuilder.create()
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