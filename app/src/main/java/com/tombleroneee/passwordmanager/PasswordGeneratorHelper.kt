package com.tombleroneee.passwordmanager

class PasswordGeneratorHelper {
    fun generatePassword(length: Int, letters: Boolean, symbols: Boolean, numbers: Boolean): String {
        var generatedPasswordChars = ""
        val usableLetters = "abcdefghijklmnopqrstuvwxyz"
        val usableSymbols = "!@£$%^&*(){}[]:<>,.?/`§"
        val usableNumbers = "1234567890"

        if (letters)
            generatedPasswordChars += usableLetters
        if (letters)
            generatedPasswordChars += usableLetters.toUpperCase()
        if (symbols)
            generatedPasswordChars += usableSymbols
        if (numbers)
            generatedPasswordChars += usableNumbers

        var generatedPassword = ""
        for (i in 0..length) {
            val randomChar = (0 until generatedPasswordChars.length).random()
            generatedPassword += generatedPasswordChars[randomChar]
        }
        return generatedPassword
    }
}