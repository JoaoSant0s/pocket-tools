package com.JoaoSantos.pockettools.buttons

import android.view.ViewGroup
import android.widget.LinearLayout
import com.JoaoSantos.pockettools.MainActivity
import com.JoaoSantos.pockettools.R

class ButtonBuilder(view: MainActivity) {
    private var context: MainActivity = view
    private var rootLayout: ViewGroup = context.findViewById<LinearLayout>(R.id.button_list)

    private var buttons: MutableList<Button> = mutableListOf()

    fun addLockScreen(): ButtonBuilder {
        buttons.add(ButtonLockScreen(context))

        return this
    }

    fun addVolume(): ButtonBuilder {
        buttons.add(ButtonVolume(context))

        return this
    }

    fun build() {
        for (button in buttons) {
            rootLayout.addView(button.create())
        }
    }
}