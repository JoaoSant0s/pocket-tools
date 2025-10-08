package com.joaosant0s.pockettools.tools

import android.view.ViewGroup
import android.widget.LinearLayout
import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R

class ToolsBuilder(view: MainActivity) {
    private var context: MainActivity = view
    private var rootLayout: ViewGroup = context.findViewById<LinearLayout>(R.id.tool_list)

    private var tools: MutableList<Tool> = mutableListOf()

    fun addLockScreen(): ToolsBuilder {
        tools.add(ToolLockScreen(context))

        return this
    }

    fun addVolume(): ToolsBuilder {
        tools.add(ToolVolume(context))

        return this
    }

    fun create() {
        for (tool in tools) {
            rootLayout.addView(tool.create())
        }
    }
}