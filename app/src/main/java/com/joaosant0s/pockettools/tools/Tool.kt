package com.joaosant0s.pockettools.tools

import android.content.Context
import android.view.ViewGroup
import com.joaosant0s.pockettools.MainActivity

interface Tool {
    fun create(): ViewGroup
}