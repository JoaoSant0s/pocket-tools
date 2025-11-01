package com.joaosant0s.pockettools.tools

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.core.Permission
import com.joaosant0s.pockettools.tools.collection.ToolLantern
import com.joaosant0s.pockettools.tools.collection.ToolLockScreen
import com.joaosant0s.pockettools.tools.collection.ToolLockScreenAlternative
import com.joaosant0s.pockettools.tools.collection.ToolVolume

class ToolsBuilder(view: Context, layout : ViewGroup) {
    private val context: Context = view
    private val rootLayout: ViewGroup = layout

    private val tools: MutableList<Tool> = mutableListOf()
    private var created = false

    fun addLockScreen(): ToolsBuilder {

        if(context is MainActivity)
        {
            tools.add(ToolLockScreen(context))

        }else{
            tools.add(ToolLockScreenAlternative(context))
        }

        return this
    }

    fun addVolume(): ToolsBuilder {
        tools.add(ToolVolume(context))

        return this
    }

    fun addLantern(): ToolsBuilder {
        tools.add(ToolLantern(context))

        return this
    }

    fun create() {
        for (tool in tools) {
            rootLayout.addView(tool.create())
        }
        created = true
    }

    fun getPermissions(): Collection<Permission> {
        if (!created) throw Exception("ToolsBuild where not Created yet")

        return tools.filterIsInstance<Permission>()
    }
}