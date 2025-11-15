package com.joaosant0s.pockettools.tools.collection

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

import com.joaosant0s.pockettools.core.admin.DeviceAdminReceiver
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.core.EventNames
import com.joaosant0s.pockettools.tools.Tool
import com.joaosant0s.pockettools.tools.ToolWrapper
import com.joaosant0s.pockettools.utils.Message
import com.joaosant0s.pockettools.utils.events.EventEmitter

class ToolLockScreenFloating(view: Context) : Tool, EventEmitter {
    val context: Context = view

    private var devicePolicyManager: DevicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    private lateinit var horizontalLayout : LinearLayout

    override fun create(): ViewGroup {
        horizontalLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ToolWrapper.baseLayoutParams
            gravity = Gravity.CENTER
        }

        refresh()

        val lockScreenButton = ToolWrapper.createTextButton(
            context,
            ToolWrapper.baseLayoutParams,
            R.string.lock_screen_button
        )
        {
            if (isAdmin()) {
                emit(EventNames.LOCK_SCREEN_ACTIVATED)
                devicePolicyManager.lockNow()
            }else{
                Message.showToast(context, "Open the Pocket Tool App and Request Admin Permission")
            }
        }

        horizontalLayout.addView(lockScreenButton)

        return horizontalLayout
    }

    override fun refresh()
    {
        if(!isAdmin() && horizontalLayout.background == null)
        {
            horizontalLayout.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 100f
                setColor(ContextCompat.getColor(context, R.color.orange))
            }
            horizontalLayout.setPadding(15, 4, 15, 5)
        }else if(isAdmin() && horizontalLayout.background != null){
            horizontalLayout.background = null
        }

        horizontalLayout.requestLayout()
    }

    private fun isAdmin(): Boolean {
        val compName = ComponentName(context, DeviceAdminReceiver::class.java)

        return devicePolicyManager.isAdminActive(compName)
    }
}