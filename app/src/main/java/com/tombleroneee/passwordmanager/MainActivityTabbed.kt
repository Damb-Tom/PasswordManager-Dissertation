package com.tombleroneee.passwordmanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_tabbed.*
import kotlinx.android.synthetic.main.fragment_main_activity_tabbed.*
import kotlinx.android.synthetic.main.fragment_main_activity_tabbed.view.*
import kotlinx.android.synthetic.main.fragment_main_activity_tabbed_2.view.*
import kotlinx.android.synthetic.main.fragment_main_activity_tabbed_3.view.*


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
            return 3
        }
    }

    class PlaceholderFragment : Fragment() {
        private lateinit var database: DatabaseReference
        lateinit var rootView: View
        private var dialogControls = ArrayList<EditText>()

        private fun updateRecyclerView(list: ArrayList<RecyclerData>) {
            if (arguments?.getInt(ARG_SECTION_NUMBER) == 1) {
                rootView.recyclerList.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
                rootView.recyclerList.adapter = RecyclerClass(list, context)
                loadingBar.visibility = View.INVISIBLE
            }
        }

        private fun generatePassword(length: Int, letters: Boolean, symbols: Boolean, numbers: Boolean): String {
            var generatedPasswordChars = ""
            val usableLetters = "abcdefghijklmnopqrstuvwxyz"
            val usableSymbols = "!@£$%^&*(){}[]:<>,.?/`§"
            val usableNumbers = "1234567890"

            if (letters)
                generatedPasswordChars += usableLetters
            if (letters)
                generatedPasswordChars += usableLetters.toUpperCase()
            if (symbols)
                generatedPasswordChars += usableSymbols
            if (numbers)
                generatedPasswordChars += usableNumbers

            var generatedPassword = ""
            for (i in 0..length) {
                val randomChar = (0 until generatedPasswordChars.length).random()
                generatedPassword += generatedPasswordChars[randomChar]
            }
            return generatedPassword
        }

        fun getFromDB(userID: String, list: ArrayList<RecyclerData>, withUpdate: Boolean = true) {
            database.child("stored_passwords").child(userID).orderByKey()
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        list.clear()

                        for (ds in dataSnapshot.children) {
                            list.add(
                                RecyclerData(
                                    ds.child("title").value.toString(),
                                    ds.child("username").value.toString(),
                                    ds.child("password").value.toString(),
                                    ds.child("title").ref,
                                    ds.child("username").ref,
                                    ds.child("password").ref
                                )
                            )
                        }
                        if (withUpdate) {
                            updateRecyclerView(list)
                        }
                    }
                })
        }

        fun sortData() {
            txtSearchResults.visibility = View.VISIBLE
            tempRecyclerListList.clear()
            for (i in 0 until recyclerListList.size) {
                if (recyclerListList[i].title.toLowerCase().contains(searchField.text.toString().toLowerCase())) {
                    tempRecyclerListList.add(recyclerListList[i])
                }
            }
            rootView.txtSearchResults.text = if (tempRecyclerListList.size == 0) {
                "No results found!"
            } else {
                "${tempRecyclerListList.size} results found!"
            }

            updateRecyclerView(tempRecyclerListList)
        }

        var firstRun: Boolean = false
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            when {
                arguments?.getInt(ARG_SECTION_NUMBER) == 1 -> {
                    rootView = inflater.inflate(R.layout.fragment_main_activity_tabbed, container, false)

                    if (!firstRun) {
                        Snackbar.make(rootView, "Swipe left and right to navigate pages!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Hide Tip") {}.show()
                        firstRun = true
                    }

                    val spinnerOptions = arrayOf("None", "Ascending", "Descending")
                    rootView.spinnerSort.adapter =
                        ArrayAdapter<String>(context!!, R.layout.spinner_layout, spinnerOptions)

                    val user = FirebaseAuth.getInstance().currentUser
                    val userID = user!!.uid

                    database = FirebaseDatabase.getInstance().reference

                    getFromDB(userID, recyclerListList)


                    rootView.btnCreateSavedPassword.setOnClickListener {
                        val layout = LinearLayout(context)
                        layout.orientation = LinearLayout.VERTICAL
                        dialogControls.clear()


                        EditText(context).apply {
                            hint = "Website"
                            setTextColor(ContextCompat.getColor(context, R.color.White))
                            setHintTextColor(ContextCompat.getColor(context, R.color.lightWhite))
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                            textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                            layout.addView(this)
                            dialogControls.add(this)
                        }
                        EditText(context).apply {
                            hint = "Username/Email"
                            setTextColor(ContextCompat.getColor(context, R.color.White))
                            setHintTextColor(ContextCompat.getColor(context, R.color.lightWhite))
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                            textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                            layout.addView(this)
                            dialogControls.add(this)
                        }
                        EditText(context).apply {
                            hint = "Password"
                            setTextColor(ContextCompat.getColor(context, R.color.White))
                            setHintTextColor(ContextCompat.getColor(context, R.color.lightWhite))
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                            layout.addView(this)
                            dialogControls.add(this)
                        }

                        AlertDialog.Builder(it.context, R.style.MyAlertDialogStyle).apply {
                            setTitle("Store Password")
                            setView(layout)

                            setPositiveButton("Add") { dialog, _ ->
                                dialog.dismiss()

                                val data = RecyclerDataWithoutRef(
                                    dialogControls[0].text.toString(),
                                    dialogControls[1].text.toString(),
                                    dialogControls[2].text.toString()
                                )
                                if (data.title.isNotEmpty() && data.username.isNotEmpty() && data.password.isNotEmpty()) {
                                    database.child("stored_passwords").child(userID).push().setValue(data)
                                }
                            }

                            setNeutralButton("Cancel") { dialog, _ ->
                                dialog.cancel()
                            }

                            show()
                        }
                    }

                    rootView.searchField.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(p0: Editable?) {
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            sortData()
                        }
                    })

                    rootView.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }

                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            when (p2) {
                                0 -> { // none
                                    getFromDB(userID, recyclerListList)
                                }
                                1 -> { // ascending
                                    getFromDB(userID, recyclerListList, false)
                                    tempRecyclerListList.clear()
                                    var newData = recyclerListList.sortedWith(compareBy { it.title.toLowerCase() })
                                    recyclerListList.clear()
                                    for (obj in newData) {
                                        tempRecyclerListList.add(obj)
                                    }
                                    updateRecyclerView(tempRecyclerListList)
                                }
                                2 -> { // descending
                                    getFromDB(userID, recyclerListList, false)
                                    tempRecyclerListList.clear()
                                    var newData = recyclerListList.sortedWith(compareBy { it.title.toLowerCase() })
                                    recyclerListList.clear()
                                    for (obj in newData) {
                                        tempRecyclerListList.add(obj)
                                    }
                                    tempRecyclerListList.reverse()
                                    updateRecyclerView(tempRecyclerListList)
                                }
                            }
                        }
                    }
                }
                arguments?.getInt(ARG_SECTION_NUMBER) == 2 -> {
                    rootView = inflater.inflate(R.layout.fragment_main_activity_tabbed_2, container, false)
                    rootView.btnGenerate.setOnClickListener {
                        if (rootView.chkLetters.isChecked || rootView.chkSymbols.isChecked || rootView.chkNumbers.isChecked) {
                            val pass = generatePassword(
                                rootView.strengthSeekBar.progress,
                                rootView.chkLetters.isChecked,
                                rootView.chkSymbols.isChecked,
                                rootView.chkNumbers.isChecked
                            )
                            rootView.txtGeneratedPass.setText(pass)
                        } else {
                            Toast.makeText(context, "Please select password properties!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    rootView.btnCopy.setOnClickListener {
                        val clipboardManager =
                            this.context!!.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                        val textToCopy = ClipData.newPlainText("text", rootView.txtGeneratedPass.text)
                        clipboardManager?.primaryClip = textToCopy
                        Toast.makeText(context, "Copied password to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                    rootView.btnSave.setOnClickListener {
                        val user = FirebaseAuth.getInstance().currentUser
                        val userID = user!!.uid
                        val pass = "${rootView.txtGeneratedPass.text}"

                        val layout = LinearLayout(context)
                        layout.orientation = LinearLayout.VERTICAL
                        dialogControls.clear()

                        EditText(context).apply {
                            hint = "Website"
                            setTextColor(ContextCompat.getColor(context, R.color.White))
                            setHintTextColor(ContextCompat.getColor(context, R.color.lightWhite))
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                            textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                            layout.addView(this)
                            dialogControls.add(this)
                        }
                        EditText(context).apply {
                            hint = "Username/Email"
                            setTextColor(ContextCompat.getColor(context, R.color.White))
                            setHintTextColor(ContextCompat.getColor(context, R.color.lightWhite))
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                            textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                            layout.addView(this)
                            dialogControls.add(this)
                        }
                        EditText(context).apply {
                            hint = "Password"
                            setText(pass)
                            setTextColor(ContextCompat.getColor(context, R.color.White))
                            setHintTextColor(ContextCompat.getColor(context, R.color.lightWhite))
                            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            textAlignment = EditText.TEXT_ALIGNMENT_CENTER
                            layout.addView(this)
                            dialogControls.add(this)
                        }

                        AlertDialog.Builder(it.context, R.style.MyAlertDialogStyle).apply {
                            setTitle("Store Password")
                            setView(layout)

                            setPositiveButton("Add") { dialog, _ ->
                                dialog.dismiss()

                                val data = RecyclerDataWithoutRef(
                                    dialogControls[0].text.toString(),
                                    dialogControls[1].text.toString(),
                                    dialogControls[2].text.toString()
                                )
                                if (data.title.isNotEmpty() && data.username.isNotEmpty() && data.password.isNotEmpty()) {
                                    database = FirebaseDatabase.getInstance().reference
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
                arguments?.getInt(ARG_SECTION_NUMBER) == 3 -> {
                    rootView = inflater.inflate(R.layout.fragment_main_activity_tabbed_3, container, false)
                    rootView.passwordStrengthBar.progressDrawable =
                        ContextCompat.getDrawable(this.context!!, R.drawable.progress_style)


                    rootView.txtPassInput.addTextChangedListener(object : TextWatcher {
                        var passwordStrengthObj = PasswordStrengthHelper()
                        override fun afterTextChanged(p0: Editable?) {
                            val result = passwordStrengthObj.testPassword(rootView.txtPassInput.text.toString()).toInt()
                            rootView.passwordStrengthBar.progress = result
                        }

                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            val result = passwordStrengthObj.testPassword(rootView.txtPassInput.text.toString()).toInt()
                            rootView.passwordStrengthBar.progress = result
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            val result = passwordStrengthObj.testPassword(rootView.txtPassInput.text.toString()).toInt()
                            rootView.passwordStrengthBar.progress = result
                        }
                    })
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

    private var backPressedTwice = false
    override fun onBackPressed() {
        if (backPressedTwice) {
            val signOutDialogBox = AlertDialog.Builder(this)
            signOutDialogBox.setTitle("Sign out?")
            signOutDialogBox.setMessage("Would you like to sign out?")

            signOutDialogBox.setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()

                Toast.makeText(baseContext, "Signed out successfully!", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivityTabbed, PreLoginActivity::class.java))
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
