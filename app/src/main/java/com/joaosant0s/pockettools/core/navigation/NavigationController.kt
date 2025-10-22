package com.joaosant0s.pockettools.core.navigation

import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.utils.Message

class NavigationController(view: MainActivity) {

    private var context: MainActivity = view

    private var drawerLayout: DrawerLayout = context.findViewById(R.id.drawerLayout)
    private var navigationView: NavigationView = context.findViewById(R.id.navigationView)
    private var topAppBar: MaterialToolbar = context.findViewById(R.id.topAppBar)

    init {
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
            true
        }

        context.onBackPressedDispatcher.addCallback(context, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    context.finish()
                }
            }
        })
    }

    private fun showToast(message: String) {
        Message.showToast(context, message)
    }
}