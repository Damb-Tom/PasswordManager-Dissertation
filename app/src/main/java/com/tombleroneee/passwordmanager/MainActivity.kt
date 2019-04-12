package com.tombleroneee.passwordmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var recyclerListList = ArrayList<RecyclerData>()
    private var dialogControls = ArrayList<EditText>()

    private fun updateRecyclerView(){
        recyclerList.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayout.VERTICAL, false)
        recyclerList.adapter = RecyclerClass(recyclerListList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        val userID = user!!.uid
        // TODO: Go to login page if not logged in

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
                        recyclerListList.add(RecyclerData(ds.child("title").value.toString(), ds.child("username").value.toString(), ds.child("password").value.toString()))
                    }
                    updateRecyclerView()
                }
            })



        btnAddNew.setOnClickListener {
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL

            val addNewPassDialog = AlertDialog.Builder(this)
            addNewPassDialog.setTitle("Create Memo")

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


            addNewPassDialog.setView(layout)
            addNewPassDialog.setPositiveButton("Add") { dialog, _ ->
                dialog.dismiss()

                val data = RecyclerData(dialogControls[0].text.toString(), dialogControls[1].text.toString(), dialogControls[2].text.toString())
                if(data.title.isNotEmpty() && data.username.isNotEmpty() && data.Password.isNotEmpty()) {
                    database.child("stored_passwords").child(userID).push().setValue(data)
                }
            }

            addNewPassDialog.setNeutralButton("Cancel") {
                    dialog, _ -> dialog.cancel()
            }

            addNewPassDialog.show()
        }

    }
}