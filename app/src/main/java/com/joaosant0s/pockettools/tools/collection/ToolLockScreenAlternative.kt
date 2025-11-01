package com.joaosant0s.pockettools.tools.collection

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout

import com.joaosant0s.pockettools.core.admin.DeviceAdminReceiver
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.tools.Tool
import com.joaosant0s.pockettools.tools.ToolWrapper
import com.joaosant0s.pockettools.utils.AppEvents
import com.joaosant0s.pockettools.utils.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToolLockScreenAlternative(view: Context) : Tool {

    val context: Context = view

    private var devicePolicyManager: DevicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    override fun create(): ViewGroup {
        val horizontalLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ToolWrapper.baseLayoutParams
            gravity = Gravity.CENTER
        }

        val lockScreenButton = ToolWrapper.createTextButton(
            context,
            ToolWrapper.baseLayoutParams,
            R.string.lock_screen_button
        )
        {
            if (isAdmin()) {
                CoroutineScope(Dispatchers.Default).launch {
                    AppEvents.emit("LOCK_SCREEN_TAPPED")
                }
                devicePolicyManager.lockNow()
            }else{
                Message.showToast(context, "Open the Pocket Tool App and Request Admin Permission")
            }
        }

        horizontalLayout.addView(lockScreenButton)

        return horizontalLayout
    }

    private fun isAdmin(): Boolean {
        val compName = ComponentName(context, DeviceAdminReceiver::class.java)

        return devicePolicyManager.isAdminActive(compName)
    }
}