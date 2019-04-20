package com.tombleroneee.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_pre_login.*

class PreLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var stayLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_login)

        auth = FirebaseAuth.getInstance()

        var settings = applicationContext.getSharedPreferences("USER_DATA", 0)
        stayLoggedIn = settings.getBoolean("stayLoggedIn", false)

        if(auth.currentUser != null && stayLoggedIn){
            val intent = Intent(this, MainActivityTabbed::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        register_button.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }
}
