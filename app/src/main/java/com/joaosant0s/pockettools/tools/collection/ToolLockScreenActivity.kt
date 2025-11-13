package com.joaosant0s.pockettools.tools.collection

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.provider.Settings
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import com.joaosant0s.pockettools.core.admin.DeviceAdminReceiver
import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.core.Permission
import com.joaosant0s.pockettools.tools.Tool
import com.joaosant0s.pockettools.tools.ToolWrapper
import com.joaosant0s.pockettools.utils.Message

class ToolLockScreenActivity(view: MainActivity) : Tool, Permission {

    val context: MainActivity = view
    private var deviceAdminLauncher: ActivityResultLauncher<Intent> =
        context.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {}

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
            if (!tryRequestAdminAccess()) {
                context.moveTaskToBack(true)
                devicePolicyManager.lockNow()
            }
        }

        horizontalLayout.addView(lockScreenButton)

        return horizontalLayout
    }

    override fun requestPermission() {
        if (!tryRequestAdminAccess()) {
            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
            context.startActivity(intent)
            Message.showToast(context, "Search for 'Device admin apps'", Toast.LENGTH_LONG)
        }
    }

    override fun permissionNameId(): Int {
        return R.string.lock_screen_request
    }

    override fun permissionIconId(): Int {
        return android.R.drawable.ic_lock_lock
    }

    private fun tryRequestAdminAccess(): Boolean {
        val compName = ComponentName(context, DeviceAdminReceiver::class.java)

        val isAdmin = devicePolicyManager.isAdminActive(compName)
        if (!isAdmin) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    R.string.lock_screen_permission_message
                )
            }

            deviceAdminLauncher.launch(intent)
            return true
        }

        return false
    }
}