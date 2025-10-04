package com.JoaoSantos.pockettools.utils

import android.view.View
import android.view.ViewGroup

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
    }
}