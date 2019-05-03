package com.tombleroneee.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private fun createToast(text: String, stayTime: Int) {
        Toast.makeText(baseContext, text, stayTime).show()
    }

    private fun updateDisplayName(name: String){
        val user = auth.currentUser
        if(user != null) {
            user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
        }else{
            Toast.makeText(baseContext, "Error adding a Display Name!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            var name = displayNameInput.text.toString()
            var email = emailInputField.text.toString()
            val password = passwordInputField.text.toString()
            val passwordConfirm = passwordInputFieldConfirmation.text.toString()

            if(password != passwordConfirm){
                createToast("Passwords don't match!", Toast.LENGTH_SHORT)
            }
            if(name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()){
                createToast("Please enter a Display Name, Email and Password!", Toast.LENGTH_SHORT)
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                createToast("Invalid Email Address!", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        updateDisplayName(name)
                       //createToast("Account has been created, please Login!", Toast.LENGTH_SHORT)

                        val intent = Intent(this, LoginActivity::class.java).apply {
                            putExtra("email", email)
                            putExtra("password", password)
                        }
                        startActivity(intent)
                    } else {
                        var e = task.exception
                        createToast("Error: ${e!!.message}", Toast.LENGTH_SHORT)
                    }
                }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, PreLoginActivity::class.java)
        startActivity(intent)
    }
}
