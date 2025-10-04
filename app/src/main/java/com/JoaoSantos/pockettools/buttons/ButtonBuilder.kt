package com.JoaoSantos.pockettools.buttons

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.JoaoSantos.pockettools.DeviceAdminReceiver
import com.JoaoSantos.pockettools.MainActivity
import com.JoaoSantos.pockettools.R
import com.JoaoSantos.pockettools.utils.ButtonWrapper
import com.JoaoSantos.pockettools.utils.Message
import com.google.android.material.button.MaterialButton

class ButtonBuilder {
    private lateinit var horizontalElementLayoutParams: LinearLayout.LayoutParams
    private lateinit var baseLayoutParams: LinearLayout.LayoutParams

    private lateinit var rootLayout: ViewGroup
    private lateinit var context: MainActivity

    private lateinit var deviceAdminLauncher: ActivityResultLauncher<Intent>
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var audioManager: AudioManager

    fun init(view: MainActivity): ButtonBuilder {
        context = view
        rootLayout = context.findViewById<LinearLayout>(R.id.button_list)
        devicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        deviceAdminLauncher = context.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {}

        createLayoutParams()

        return this
    }

    fun addLockScreen(): ButtonBuilder {
        createTextButton(R.string.lock_screen)
        {
            if (!tryRequestAdminAccess()) {
                devicePolicyManager.lockNow()
            }
        }

        return this
    }

    fun addVolume(): ButtonBuilder {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        val horizontalLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = baseLayoutParams
            gravity = Gravity.CENTER  // optional, to center the buttons
        }

        createTextButton(R.string.decrease_volume, horizontalLayout, horizontalElementLayoutParams){
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)

            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

            println("Volume: $currentVolume / $maxVolume")
            Message.showToast(context, "Volume: $currentVolume / $maxVolume", Toast.LENGTH_SHORT)
        }

        createTextButton(R.string.increase_volume, horizontalLayout, horizontalElementLayoutParams) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)

            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            println("Volume: $currentVolume / $maxVolume")
            Message.showToast(context, "Volume: $currentVolume / $maxVolume", Toast.LENGTH_SHORT)
        }

        rootLayout.addView(horizontalLayout)

        return this
    }

    private fun createTextButton(resId: Int, action: View.OnClickListener): MaterialButton {
        val button = ButtonWrapper.createTextButton(context, baseLayoutParams, resId, action)

        rootLayout.addView(button)

        return button
    }

    private fun createTextButton(resId: Int, parentLayout: ViewGroup, layout : ViewGroup.LayoutParams, action: View.OnClickListener): MaterialButton {
        val button = ButtonWrapper.createTextButton(context, layout, resId, action)
        parentLayout.addView(button)
        return button
    }

    private fun tryRequestAdminAccess(): Boolean {
        val compName = ComponentName(context, DeviceAdminReceiver::class.java)

        val isAdmin = devicePolicyManager.isAdminActive(compName)
        if (!isAdmin) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Permission needed to lock the screen"
                )
            }

            deviceAdminLauncher.launch(intent)
            return true
        }

        return false
    }

    private fun createLayoutParams()
    {
         baseLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        horizontalElementLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
    }
}