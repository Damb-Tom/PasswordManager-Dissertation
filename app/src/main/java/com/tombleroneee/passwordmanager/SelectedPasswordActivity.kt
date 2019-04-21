package com.tombleroneee.passwordmanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import kotlinx.android.synthetic.main.activity_selected_password.*

class SelectedPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_password)

        val intent = intent
        if (intent != null) {
            fullscreen_content.text = intent.getStringExtra("TITLE")
            txtUsername.setText(intent.getStringExtra("USERNAME"))
            txtPassword.setText(intent.getStringExtra("PASSWORD"))

        }

        chkShowPass.setOnCheckedChangeListener { _, _ ->
            if (chkShowPass.isChecked) {
                txtPassword.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                txtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }
}
