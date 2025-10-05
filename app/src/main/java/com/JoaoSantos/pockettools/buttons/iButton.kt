package com.JoaoSantos.pockettools.buttons

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.JoaoSantos.pockettools.MainActivity
import com.JoaoSantos.pockettools.R
import com.JoaoSantos.pockettools.utils.ButtonWrapper

interface Button {
    fun create() : ViewGroup
    var context: MainActivity

}