package com.joaosant0s.pockettools.core.floating

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri

import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.core.Permission
import com.joaosant0s.pockettools.utils.AlertDialogWrapper

class FloatingController(view: MainActivity) : Permission {

    private var context: MainActivity = view

    private val canOverlay: Boolean
        get() = Settings.canDrawOverlays(context)

    private val overlayPermissionLauncher = context.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    override fun requestPermission() {
        requestFloatingPermission()
    }

    override fun permissionNameId(): Int {
        return R.string.floating_button_request
    }

    override fun permissionIconId(): Int {
        return R.drawable.ic_floating
    }

    fun tryRequestFloatingPermission() {
        if (canOverlay) return

        val appName = context.applicationInfo.loadLabel(context.packageManager).toString()

        AlertDialogWrapper.showConfirmDialog(
            context = context,
            titleText = "Permission Required",
            messageText = "Please,  allow 'Display over other apps' for '$appName' to use the floating button.",
            okButtonText = "Open Settings",
            okAction = ::requestFloatingPermission
        )
    }

    fun tryStop() {
        if (!canOverlay) return

        val serviceIntent = Intent(context, FloatingService::class.java)
        context.stopService(serviceIntent)
    }

    fun tryStart() {
        if (!canOverlay) return

        startFloatingService()
    }

    private fun startFloatingService() {
        val serviceIntent = Intent(context, FloatingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    private fun requestFloatingPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:$context.packageName".toUri()
        )
        overlayPermissionLauncher.launch(intent)
    }


}