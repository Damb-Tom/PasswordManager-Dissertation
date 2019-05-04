package com.tombleroneee.passwordmanager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseHelper {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser

    init {
        user = auth.currentUser!!
    }

    fun isUserLoggedIn(): Boolean {
        val user = auth.currentUser
        return user != null
    }

    fun isEmailVerified(): Boolean {
        return user.isEmailVerified
    }
}