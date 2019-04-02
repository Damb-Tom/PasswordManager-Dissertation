package com.tombleroneee.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pre_login.*

class PreLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pre_login)

        login_button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        register_button.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }
}
