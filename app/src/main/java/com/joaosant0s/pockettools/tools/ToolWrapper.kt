package com.joaosant0s.pockettools.tools

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.core.graphics.toColorInt

import com.joaosant0s.pockettools.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class ToolWrapper {
    companion object {
        var baseLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        var horizontalElementLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        var wrapContentLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        fun createTextButton(
            context: MainActivity,
            layout: ViewGroup.LayoutParams,
            resId: Int,
            action: View.OnClickListener
        ): MaterialButton {
            val button = MaterialButton(context).apply {
                text = context.getString(resId)
                layoutParams = layout
            }

            button.setOnClickListener(action)

            return button
        }

        fun createToggleSwitch(
            context: MainActivity,
            layout: ViewGroup.LayoutParams,
            resId: Int,
            wasChecked: Boolean,
            listener: CompoundButton.OnCheckedChangeListener
        ): SwitchMaterial {
            val toggleSwitch = SwitchMaterial(context).apply {
                text = context.getString(resId)
                isChecked = wasChecked
                layoutParams = layout
                setTextColor("#361E6B".toColorInt())
            }

            toggleSwitch.setOnCheckedChangeListener(listener)

            return toggleSwitch
        }
    }

}