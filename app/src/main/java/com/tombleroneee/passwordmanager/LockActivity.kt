package com.tombleroneee.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_lock.*


class LockActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        var firebaseObj = FirebaseHelper()

        FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                val intent = Intent(this, PreLoginActivity::class.java)
                startActivity(intent)
            }
        }

        if (!firebaseObj.isUserLoggedIn()) {
            val intent = Intent(this, PreLoginActivity::class.java)
            startActivity(intent)
        }

        val pinLock = PinLockHelper(applicationContext)
        val pinAlreadyCreated = pinLock.isPinCreated()

        if (!pinAlreadyCreated) {
            fullscreen_content.text = "Welcome"
            btnEnter.text = "Set Pin!"
            txtPin2.visibility = View.VISIBLE
        } else {
            fullscreen_content.text = "Welcome Back"
            btnEnter.text = "Enter!"
            txtPin2.visibility = View.INVISIBLE
        }

        btnEnter.setOnClickListener {
            if (!pinAlreadyCreated) {
                if (txtPin1.text.isEmpty() || txtPin2.text.isEmpty()) {
                    Toast.makeText(applicationContext, "Please enter a pin code!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (txtPin1.text.toString() != txtPin2.text.toString()) {
                    Toast.makeText(applicationContext, "Pin Codes do not match!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                pinLock.setPin(txtPin1.text.toString().toInt())

                Toast.makeText(applicationContext, "Pin Saved!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, PreLoginActivity::class.java)
                startActivity(intent)
            } else {
                val pin = pinLock.getPin()
                if (txtPin1.text.toString().toInt() == pin) {
                    val intent = Intent(this, PreLoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Invalid Pin!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

// TODO: Reset data from device (will log out the user and delete any saved pins)