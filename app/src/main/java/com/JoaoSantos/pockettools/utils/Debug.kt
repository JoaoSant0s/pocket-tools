package com.JoaoSantos.pockettools.utils

import android.util.Log

class Debug {
    companion object {

        private const val TAG = "[PT]"

        fun log(message : String)
        {
            Log.d(TAG, message)
        }

        fun error(message : String)
        {
            Log.e(TAG, message)
        }

        fun warning(message : String)
        {
            Log.w(TAG, message)
        }
    }
}