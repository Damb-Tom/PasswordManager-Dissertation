package com.tombleroneee.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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

    private var backPressedTwice = false
    override fun onBackPressed() {
        if (backPressedTwice) {
            val signOutDialogBox = AlertDialog.Builder(this)
            signOutDialogBox.setTitle("Sign Out?")
            signOutDialogBox.setMessage("Would you like to sign out?")

            signOutDialogBox.setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()

                Toast.makeText(baseContext, "Signed out successfully!", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@SelectedPasswordActivity, PreLoginActivity::class.java))
            }

            signOutDialogBox.setNeutralButton("No") { dialog, _ ->
                dialog.cancel()
            }
            signOutDialogBox.show()
            return
        }
        this.backPressedTwice = true
        Handler().postDelayed({ this.backPressedTwice = false }, 2000)
    }
}
