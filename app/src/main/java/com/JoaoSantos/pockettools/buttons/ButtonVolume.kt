package com.JoaoSantos.pockettools.buttons

import android.content.Context
import android.media.AudioManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.JoaoSantos.pockettools.MainActivity
import com.JoaoSantos.pockettools.R
import com.JoaoSantos.pockettools.utils.ButtonWrapper
import com.JoaoSantos.pockettools.utils.Debug
import com.JoaoSantos.pockettools.utils.Message

class ButtonVolume(view: MainActivity) : Button {

    override var context: MainActivity = view
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var maxVolume: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    override fun create(): ViewGroup {
        val horizontalLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ButtonWrapper.baseLayoutParams
            gravity = Gravity.CENTER  // optional, to center the buttons
        }

        val lowerButton = ButtonWrapper.createTextButton(
            context,
            ButtonWrapper.horizontalElementLayoutParams,
            R.string.decrease_volume_button
        ) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_PLAY_SOUND
            )

            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            Debug.log("Volume: $currentVolume / $maxVolume")
            Message.showToast(context, "Volume: $currentVolume / $maxVolume", Toast.LENGTH_SHORT)
        }

        val raiseButton = ButtonWrapper.createTextButton(
            context,
            ButtonWrapper.horizontalElementLayoutParams,
            R.string.increase_volume_button
        ) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND
            )

            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            Debug.log("Volume: $currentVolume / $maxVolume")
            Message.showToast(context, "Volume: $currentVolume / $maxVolume", Toast.LENGTH_SHORT)
        }

        horizontalLayout.addView(lowerButton)
        horizontalLayout.addView(raiseButton)

        return horizontalLayout
    }
}