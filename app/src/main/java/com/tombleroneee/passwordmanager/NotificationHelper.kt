package com.tombleroneee.passwordmanager

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast

class NotificationHelper {
    companion object {
        fun createToast(context: Context, text: String, stayTime: Int) {
            Toast.makeText(context, text, stayTime).show()
        }

        fun createSnack(view: View, message: String, action: String, len: Int){
            Snackbar.make(view, message, len).setAction(action) {}.show()
        }
    }
}