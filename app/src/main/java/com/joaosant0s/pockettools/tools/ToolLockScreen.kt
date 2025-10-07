package com.joaosant0s.pockettools.tools

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.joaosant0s.pockettools.DeviceAdminReceiver
import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.utils.ToolWrapper

class ToolLockScreen(view: MainActivity) : Tool {

    override var context: MainActivity = view
    private var deviceAdminLauncher: ActivityResultLauncher<Intent> = context.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    private var devicePolicyManager: DevicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    override fun create() : ViewGroup {
        val horizontalLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ToolWrapper.baseLayoutParams
            gravity = Gravity.CENTER
        }

        val lockScreenButton = ToolWrapper.createTextButton(context, ToolWrapper.baseLayoutParams, R.string.lock_screen_button)
        {
            if (!tryRequestAdminAccess()) {
                devicePolicyManager.lockNow()
            }
        }

        horizontalLayout.addView(lockScreenButton)

        return horizontalLayout
    }

    private fun tryRequestAdminAccess(): Boolean {
        val compName = ComponentName(context, DeviceAdminReceiver::class.java)

        val isAdmin = devicePolicyManager.isAdminActive(compName)
        if (!isAdmin) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    R.string.lock_screen_permission
                )
            }

            deviceAdminLauncher.launch(intent)
            return true
        }

        return false
    }
}