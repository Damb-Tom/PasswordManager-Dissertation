package com.tombleroneee.passwordmanager

import android.content.Intent
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
    private var stayLoggedIn: Boolean = false

    private fun attemptLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            NotificationHelper.createToast(applicationContext, "Please enter an Email and Password!", Toast.LENGTH_SHORT)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            NotificationHelper.createToast(applicationContext, "Invalid Email Address!", Toast.LENGTH_SHORT)
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (validateEmail()) {
                        NotificationHelper.createToast(applicationContext, "Successfully Logged In!", Toast.LENGTH_SHORT)

                        val pinLock = PinLockHelper(applicationContext)
                        val pinAlreadyCreated = pinLock.isPinCreated()

                        if (pinAlreadyCreated) {
                            val intent = Intent(this, MainActivityTabbed::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, LockActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    NotificationHelper.createToast(applicationContext, "Invalid Username or Password, try again!", Toast.LENGTH_SHORT)
                    passwordField.setText("")
                }
            }
    }

    private fun isEmailVerified(): Boolean {
        val user = auth.currentUser
        return user!!.isEmailVerified
    }

    private fun validateEmail(): Boolean {
        var emailVerified = isEmailVerified()
        if (emailVerified) {
            return true
        }

        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    NotificationHelper.createToast(applicationContext, "Verification Email sent to ${user.email}", Toast.LENGTH_SHORT)
                } else {
                    NotificationHelper.createToast(applicationContext, "Verification failed!", Toast.LENGTH_SHORT)
                }
            }
        return emailVerified
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        var settings = applicationContext.getSharedPreferences("USER_DATA", 0)
        stayLoggedIn = settings.getBoolean("stayLoggedIn", false)
        stay_logged_in_check.isChecked = stayLoggedIn

        stay_logged_in_check.setOnCheckedChangeListener { _, isChecked ->
            stayLoggedIn = isChecked

            val editor = settings.edit()
            editor.putBoolean("stayLoggedIn", stayLoggedIn)
            editor.apply()
        }

        var registrationData = intent
        var registrationEmail = "${registrationData.getStringExtra("email")}"
        var registrationPassword = "${registrationData.getStringExtra("password")}"

        if (registrationEmail != "null" && registrationPassword != "null") {
            emailField.setText(registrationEmail)
            passwordField.setText(registrationPassword)
            attemptLogin(registrationEmail, registrationPassword)
        }

        forgotPasswordText.setOnClickListener {
            var email = emailField.text.toString()
            if (email.isEmpty()) {
                NotificationHelper.createToast(applicationContext, "Please enter an Email!", Toast.LENGTH_SHORT)
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
            attemptLogin(email, password)
        }
        // TODO: PRESSING ENTER WHILE LOGGING IN WILL RESET YOUR PASSWORD..
    }

    override fun onBackPressed() {
        val intent = Intent(this, PreLoginActivity::class.java)
        startActivity(intent)
    }
}
