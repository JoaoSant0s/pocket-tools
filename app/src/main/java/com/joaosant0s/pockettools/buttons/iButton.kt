package com.joaosant0s.pockettools.buttons

import android.view.ViewGroup
import com.joaosant0s.pockettools.MainActivity

interface Button {
    fun create(): ViewGroup
    var context: MainActivity

}