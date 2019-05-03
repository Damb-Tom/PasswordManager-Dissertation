package com.tombleroneee.passwordmanager


class PasswordStrengthHelper {
    val passwordMedStrength = 6
    val passwordHighStrength = 10

    fun testPassword(password: String): Float {
        val capsReg = "[A-Z]".toRegex()
        val numsReg = "[0-9]".toRegex()
        val symsReg = "[^a-z^A-Z^0-9]".toRegex()
        val caps = capsReg.find(password)
        val nums = numsReg.find(password)
        val syms = symsReg.find(password)

        var len = false
        if (password.length >= passwordMedStrength) {
            len = true
        }
        var len2 = false
        if (password.length >= passwordHighStrength) {
            len2 = true
        }

        var total = 0.0F
        if (caps != null) {
            total += 1
        }
        if (nums != null) {
            total += 1
        }
        if (syms != null) {
            total += 1
        }
        if (len) {
            total += 1
        }
        if (len2) {
            total += 1
        }
        return (total / 5) * 100
    }
}