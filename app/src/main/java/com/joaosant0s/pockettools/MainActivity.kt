package com.joaosant0s.pockettools

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.provider.Settings
import android.widget.Toast
import androidx.core.net.toUri
import androidx.activity.result.contract.ActivityResultContracts

import com.joaosant0s.pockettools.services.FloatingService
import com.joaosant0s.pockettools.tools.ToolsBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var toolsBuilder: ToolsBuilder

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Permission denied. Cannot draw overlays.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolsBuilder = ToolsBuilder(this)
        toolsBuilder.addLockScreen().addVolume().build()

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val serviceIntent = Intent(this, FloatingService::class.java)
        stopService(serviceIntent)
    }

    override fun onStop() {
        super.onStop()

        if (Settings.canDrawOverlays(this)) startFloatingService()
    }

    private fun startFloatingService() {
        val serviceIntent = Intent(this, FloatingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}
