package com.joaosant0s.pockettools

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.joaosant0s.pockettools.core.Permission
import com.joaosant0s.pockettools.core.floating.FloatingController
import com.joaosant0s.pockettools.core.navigation.NavigationController

import com.joaosant0s.pockettools.tools.ToolsBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var toolsBuilder: ToolsBuilder
    private lateinit var floatingController: FloatingController
    private lateinit var navigationController: NavigationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val hasFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        val rootLayout: ViewGroup = this.findViewById<LinearLayout>(R.id.tool_list)

        toolsBuilder = ToolsBuilder(this, rootLayout)
        floatingController = FloatingController(this)
        navigationController = NavigationController(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolsBuilder = toolsBuilder.addLockScreen().addVolume()
        if (hasFlash) toolsBuilder = toolsBuilder.addLantern()
        toolsBuilder.create()

        val permissions: MutableList<Permission> = mutableListOf<Permission>().apply {
            add(floatingController)
            addAll(toolsBuilder.getPermissions())
        }

        navigationController.populatePermissionOptions(permissions)

        floatingController.tryRequestFloatingPermission()
    }

    override fun onStart() {
        super.onStart()
        toolsBuilder.refreshTools()

        floatingController.tryStop();
    }

    override fun onStop() {
        super.onStop()

        floatingController.tryStart();
    }
}
