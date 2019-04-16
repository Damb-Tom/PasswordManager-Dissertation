package com.tombleroneee.passwordmanager

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var recyclerListList = ArrayList<RecyclerData>()
    private var dialogControls = ArrayList<EditText>()

    private fun updateRecyclerView() {
        recyclerList.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayout.VERTICAL, false)
        recyclerList.adapter = RecyclerClass(recyclerListList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        val userID = user!!.uid

        // Listen for auth change
        FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                val intent = Intent(this, PreLoginActivity::class.java)
                startActivity(intent)
            }
        }

        database = FirebaseDatabase.getInstance().reference

        database.child("stored_passwords").child(userID).orderByKey()
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Clear Arrays because they'll have kept data from the previous 'onDataChange'
                    recyclerListList.clear()

                    // Grab Data unsorted
                    for (ds in dataSnapshot.children) {
                        recyclerListList.add(
                            RecyclerData(
                                ds.child("title").value.toString(),
                                ds.child("username").value.toString(),
                                ds.child("password").value.toString()
                            )
                        )
                    }
                    updateRecyclerView()
                }
            })



        btnAddNew2.setOnClickListener {
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            dialogControls.clear()


            EditText(this).apply {
                hint = "Website"
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                layout.addView(this)
                dialogControls.add(this)
            }
            EditText(this).apply {
                hint = "Username/Email"
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                layout.addView(this)
                dialogControls.add(this)
            }
            EditText(this).apply {
                hint = "Password"
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                layout.addView(this)
                dialogControls.add(this)
            }

            AlertDialog.Builder(this).apply {
                setTitle("Add Password")
                setView(layout)

                setPositiveButton("Add") { dialog, _ ->
                    dialog.dismiss()

                    val data = RecyclerData(
                        dialogControls[0].text.toString(),
                        dialogControls[1].text.toString(),
                        dialogControls[2].text.toString()
                    )
                    if (data.title.isNotEmpty() && data.username.isNotEmpty() && data.Password.isNotEmpty()) {
                        database.child("stored_passwords").child(userID).push().setValue(data)
                    }
                }

                setNeutralButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

                show()
            }
        }
    }

    private var backPressedTwice = false
    override fun onBackPressed() {
        if (backPressedTwice) {
            AlertDialog.Builder(this).apply {
                setTitle("Sign Out?")
                setMessage("Would you like to sign out?")

                setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()

                    Toast.makeText(baseContext, "Signed out successfully!", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@MainActivity, PreLoginActivity::class.java))
                }

                setNeutralButton("No") { dialog, _ ->
                    dialog.cancel()
                }

                show()
            }
            return
        }
        this.backPressedTwice = true
        Handler().postDelayed({ this.backPressedTwice = false }, 2000)
    }
}
