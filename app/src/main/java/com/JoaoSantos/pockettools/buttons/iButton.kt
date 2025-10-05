package com.JoaoSantos.pockettools.buttons

import android.view.ViewGroup
import com.JoaoSantos.pockettools.MainActivity

interface Button {
    fun create(): ViewGroup
    var context: MainActivity

}