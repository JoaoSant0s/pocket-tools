package com.joaosant0s.pockettools

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.joaosant0s.pockettools.core.floating.FloatingController

import com.joaosant0s.pockettools.tools.ToolsBuilder
import com.joaosant0s.pockettools.tools.collection.ToolLantern

class MainActivity : AppCompatActivity() {

    private lateinit var toolsBuilder: ToolsBuilder
    private lateinit var floatingController : FloatingController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val hasFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        toolsBuilder = ToolsBuilder(this)
        floatingController = FloatingController(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolsBuilder = toolsBuilder.addLockScreen().addVolume()
        if(hasFlash) toolsBuilder = toolsBuilder.addLantern()
        toolsBuilder.create()

        floatingController.tryRequestFloatingPermission()
    }

    override fun onStart() {
        super.onStart()

        floatingController.tryStop();
    }

    override fun onStop() {
        super.onStop()

        floatingController.tryStart();
    }
}
