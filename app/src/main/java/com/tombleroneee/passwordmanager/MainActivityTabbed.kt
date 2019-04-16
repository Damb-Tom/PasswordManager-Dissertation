package com.tombleroneee.passwordmanager

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_tabbed.*
import kotlinx.android.synthetic.main.fragment_main_activity_tabbed.view.*
import kotlinx.android.synthetic.main.fragment_main_activity_tabbed_2.view.*
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText


class MainActivityTabbed : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tabbed)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                val intent = Intent(this, PreLoginActivity::class.java)
                startActivity(intent)
            }
        }
    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            return 2
        }
    }

    class PlaceholderFragment : Fragment() {
        private lateinit var database: DatabaseReference
        private var recyclerListList = ArrayList<RecyclerData>()
        lateinit var rootView: View
        private var dialogControls = ArrayList<EditText>()

        private fun updateRecyclerView() {
            if (arguments?.getInt(ARG_SECTION_NUMBER) == 1) {
                rootView.recyclerList.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
                rootView.recyclerList.adapter = RecyclerClass(recyclerListList)
            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            if (arguments?.getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.fragment_main_activity_tabbed, container, false)

                val user = FirebaseAuth.getInstance().currentUser
                val userID = user!!.uid

                database = FirebaseDatabase.getInstance().reference

                database.child("stored_passwords").child(userID).orderByKey()
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            recyclerListList.clear()

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


                rootView.btnAddNew1.setOnClickListener {
                    Toast.makeText(context, "Test 1", Toast.LENGTH_SHORT).show()


                    val layout = LinearLayout(context)
                    layout.orientation = LinearLayout.VERTICAL
                    dialogControls.clear()


                    EditText(context).apply {
                        hint = "Website"
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                        layout.addView(this)
                        dialogControls.add(this)
                    }
                    EditText(context).apply {
                        hint = "Username/Email"
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                        layout.addView(this)
                        dialogControls.add(this)
                    }
                    EditText(context).apply {
                        hint = "Password"
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                        layout.addView(this)
                        dialogControls.add(this)
                    }

                    AlertDialog.Builder(it.context).apply {
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
            } else {
                rootView = inflater.inflate(R.layout.fragment_main_activity_tabbed_2, container, false)
                rootView.btnAddNew2.setOnClickListener {
                    Toast.makeText(context, "Test 2", Toast.LENGTH_SHORT).show()
                }
            }
            return rootView
        }

        companion object {
            private const val ARG_SECTION_NUMBER = "section_number"

            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
