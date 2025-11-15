package com.joaosant0s.pockettools.tools.collection

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.joaosant0s.pockettools.MainActivity
import com.joaosant0s.pockettools.R
import com.joaosant0s.pockettools.tools.Tool
import com.joaosant0s.pockettools.tools.ToolWrapper
import com.joaosant0s.pockettools.utils.Message

class ToolVolume(view: Context) : Tool {

    val context: Context = view
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var maxVolume: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    override fun create(): ViewGroup {
        val horizontalLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ToolWrapper.baseLayoutParams
            gravity = Gravity.CENTER
        }

        val lowerButton = ToolWrapper.createTextButton(
            context,
            ToolWrapper.horizontalElementLayoutParams,
            if(context is MainActivity) R.string.decrease_volume_button else R.string.decrease_volume_abbreviation_button
        ) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_PLAY_SOUND
            )

            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            Message.showToast(context, "Volume: $currentVolume / $maxVolume", Toast.LENGTH_SHORT)
        }

        val raiseButton = ToolWrapper.createTextButton(
            context,
            ToolWrapper.horizontalElementLayoutParams,
            if(context is MainActivity) R.string.increase_volume_button else R.string.increase_volume_abbreviation_button
        ) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND
            )

            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            Message.showToast(context, "Volume: $currentVolume / $maxVolume", Toast.LENGTH_SHORT)
        }

        horizontalLayout.addView(lowerButton)
        horizontalLayout.addView(raiseButton)

        return horizontalLayout
    }
}