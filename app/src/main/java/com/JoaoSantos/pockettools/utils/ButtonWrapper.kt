package com.JoaoSantos.pockettools.utils

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.JoaoSantos.pockettools.MainActivity
import com.google.android.material.button.MaterialButton

class ButtonWrapper {
    companion object {
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

        var baseLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        var horizontalElementLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
    }

}