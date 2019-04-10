package com.tombleroneee.passwordmanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    private fun createToast(text: String, stayLength: Int) {
        Toast.makeText(baseContext, text, stayLength).show()
    }

    private fun validateEmail(): Boolean {
        user = auth.currentUser!!
        var emailVerified = user.isEmailVerified
        if (emailVerified) {
            return true
        }

        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    createToast("Verification Email sent to ${user.email}", Toast.LENGTH_SHORT)
                } else {
                    createToast("Verification failed!", Toast.LENGTH_SHORT)
                }
            }
        return emailVerified
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        forgotPasswordText.setOnClickListener {
            var email = emailField.text.toString()
            if (email.isEmpty()) {
                createToast("Please enter an Email!", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Password reset email has been sent!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "An account doesn't exist with that email!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }

        loginButton.setOnClickListener {
            var email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                createToast("Please enter an Email and Password!", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                createToast("Invalid Email Address!", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if (validateEmail()) {
                            createToast("Successfully Logged In!", Toast.LENGTH_SHORT)
                        }
                    } else {
                        createToast("Invalid Username or Password, try again!", Toast.LENGTH_SHORT)
                        passwordField.setText("")
                    }
                }
        }
    }
}
