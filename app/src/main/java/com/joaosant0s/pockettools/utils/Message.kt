package com.joaosant0s.pockettools.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast

class Message {
    companion object {

        private var toast: Toast? = null
        private var toastVisible = false

        fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
            toast?.cancel()
            toast = Toast.makeText(context, message, duration).apply {
                show()
            }

            toastVisible = true
            val delay = if (duration == Toast.LENGTH_SHORT) 2000L else 3500L
            Handler(Looper.getMainLooper()).postDelayed({
                toastVisible = false
            }, delay)
        }
    }
}