package com.joaosant0s.pockettools

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.joaosant0s.pockettools.core.floating.FloatingController

import com.joaosant0s.pockettools.tools.ToolsBuilder
import com.joaosant0s.pockettools.utils.Message

class MainActivity : AppCompatActivity() {

    private lateinit var toolsBuilder: ToolsBuilder
    private lateinit var floatingController : FloatingController

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var topAppBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val hasFlash = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

        toolsBuilder = ToolsBuilder(this)
        floatingController = FloatingController(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        topAppBar = findViewById(R.id.topAppBar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
            GravityCompat.START
        )

        // Hamburger icon opens drawer
        topAppBar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        // Handle menu item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> showToast("Home selected")
                R.id.nav_settings -> showToast("Settings selected")
                R.id.nav_help -> showToast("Help selected")
            }
//            drawerLayout.closeDrawer(GravityCompat.END)
            true
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

    private fun showToast(message: String) {
        Message.showToast(this, message)
    }

    override fun onBackPressed() {
        // Close drawer first if open
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}
