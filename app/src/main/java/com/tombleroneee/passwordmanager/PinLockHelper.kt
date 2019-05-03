package com.tombleroneee.passwordmanager

import android.content.Context
import android.content.SharedPreferences

class PinLockHelper(context: Context) {
    private var settings: SharedPreferences = context.getSharedPreferences("USER_DATA", 0)

    fun isPinCreated(): Boolean{
        return settings.getBoolean("pinAlreadyCreated", false)
    }

    fun setPin(pin: Int){
        val editor = settings.edit()
        editor.putBoolean("pinAlreadyCreated", true)
        editor.putInt("storedPin", pin)
        editor.apply()
    }

    fun getPin(): Int{
        return settings.getInt("storedPin", 0)
    }
}