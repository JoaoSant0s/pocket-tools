package com.joaosant0s.pockettools.tools.collection

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.hardware.camera2.CameraManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout

import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.tools.Tool
import com.joaosant0s.pockettools.tools.ToolWrapper
import androidx.core.graphics.toColorInt

class ToolLantern(view: Context) : Tool {

    val context: Context = view
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var isFlashOn = false

    private var cameraId: String? = cameraManager.cameraIdList.firstOrNull { id ->
        val hasFlash = cameraManager.getCameraCharacteristics(id)
            .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE)
        hasFlash == true
    }

    override fun create(): ViewGroup {

        cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                isFlashOn = enabled
            }
        }, null)

        val horizontalLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ToolWrapper.baseLayoutParams
            gravity = Gravity.CENTER

            val backgroundDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 200f // adjust as needed
                setColor("#CFBAFF".toColorInt()) // background
            }

            background = backgroundDrawable
        }

        val lanternButton = ToolWrapper.createToggleSwitch(
            context,
            ToolWrapper.wrapContentLayoutParams,
            R.string.lantern_toggle,
            isFlashOn
        )
        { _, isChecked ->
            if (isChecked) {
                turnOn()
            } else {
                turnOff()
            }
        }

        horizontalLayout.addView(lanternButton)

        return horizontalLayout
    }

    private fun turnOn() {
        cameraId?.let { id -> cameraManager.setTorchMode(id, true) }
    }

    private fun turnOff() {
        cameraId?.let { id -> cameraManager.setTorchMode(id, false) }
    }

}