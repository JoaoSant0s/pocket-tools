package com.joaosant0s.pockettools.tools

import android.view.ViewGroup

interface Tool {
    fun create(): ViewGroup
    fun refresh() {}
}