package com.joaosant0s.pockettools.utils

import androidx.appcompat.app.AlertDialog
import com.joaosant0s.pockettools.MainActivity

class AlertDialogWrapper {

    companion object {

        fun showConfirmDialog(
            context: MainActivity,
            titleText: String,
            messageText: String,
            okButtonText: String? = "OK",
            cancelButtonText: String? = "Cancel",
            okAction: (() -> Unit)? = null,
            cancelAction: (() -> Unit)? = null
        ): AlertDialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(titleText)
            builder.setMessage(messageText)
            builder.setPositiveButton(okButtonText) { dialog, _ ->
                okAction?.invoke()
                dialog.dismiss()
            }
            builder.setNegativeButton(cancelButtonText) { dialog, _ ->
                cancelAction?.invoke()
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
            return dialog
        }
    }


}