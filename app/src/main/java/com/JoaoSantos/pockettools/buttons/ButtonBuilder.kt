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

class ButtonBuilder(view: MainActivity) {
    private var context: MainActivity = view
    private var rootLayout: ViewGroup = context.findViewById<LinearLayout>(R.id.button_list)

    private var buttons : MutableList<Button> = mutableListOf<Button>()

    fun addLockScreen(): ButtonBuilder {
        buttons.add(ButtonLockScreen(context))

        return this
    }

    fun addVolume(): ButtonBuilder {
        buttons.add(ButtonVolume(context))

        return this
    }

    fun build()
    {
        for (button in buttons)
        {
            rootLayout.addView(button.create())
        }
    }
}