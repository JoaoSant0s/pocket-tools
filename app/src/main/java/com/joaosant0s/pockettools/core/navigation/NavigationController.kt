package com.joaosant0s.pockettools.core.navigation

import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.core.Permission
import com.joaosant0s.pockettools.utils.Message

class NavigationController(view: MainActivity) {

    private var context: MainActivity = view

    private var drawerLayout: DrawerLayout = context.findViewById(R.id.drawerLayout)
    private var navigationView: NavigationView = context.findViewById(R.id.navigationView)
    private var topAppBar: MaterialToolbar = context.findViewById(R.id.topAppBar)

    private val navigationPermissionActions: MutableMap<Int, () -> Unit> = mutableMapOf()

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
            navigationPermissionActions[menuItem.itemId]?.invoke()
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

    fun populatePermissionOptions(availableTools: MutableList<Permission>) {
        for (permission in availableTools) {
            val resNameId = permission.permissionNameId()

            val dynamicItem = navigationView.menu.add(
                0, resNameId, 0, context.getString(resNameId)
            )

            dynamicItem.setIcon(ContextCompat.getDrawable(context, permission.permissionIconId()))
            dynamicItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            navigationPermissionActions[resNameId] = {
                permission.requestPermission()
            }
        }
    }
}