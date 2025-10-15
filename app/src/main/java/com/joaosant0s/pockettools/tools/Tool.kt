package com.joaosant0s.pockettools.tools

import android.view.ViewGroup
import com.joaosant0s.pockettools.MainActivity

interface Tool {
    var context: MainActivity
    fun create(): ViewGroup
}