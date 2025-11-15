package com.joaosant0s.pockettools.core.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.joaosant0s.pockettools.core.EventNames
import com.joaosant0s.pockettools.utils.events.EventEmitter

class DeviceAdminReceiver : DeviceAdminReceiver(), EventEmitter {

    override fun onEnabled(context: Context, intent: Intent) {
        Toast.makeText(context, "Device Admin enabled", Toast.LENGTH_SHORT).show()
        emit(EventNames.LOCK_SCREEN_PERMISSION_UPDATED)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(context, "Device Admin disabled", Toast.LENGTH_SHORT).show()
        emit(EventNames.LOCK_SCREEN_PERMISSION_UPDATED)
    }
}