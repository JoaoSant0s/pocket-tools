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
    private lateinit var rootLayout: ViewGroup
    private lateinit var context: MainActivity

    private var buttonVolume: ButtonVolume? = null
    private var buttonLockScreen: ButtonLockScreen? = null

    fun init(view: MainActivity): ButtonBuilder {
        context = view
        rootLayout = context.findViewById<LinearLayout>(R.id.button_list)

        return this
    }

    fun addLockScreen(): ButtonBuilder {
        buttonLockScreen = ButtonLockScreen(context)
        rootLayout.addView(buttonLockScreen!!.create())

        return this
    }

    fun addVolume(): ButtonBuilder {
        buttonVolume = ButtonVolume(context)
        rootLayout.addView(buttonVolume!!.create())

        return this
    }
}