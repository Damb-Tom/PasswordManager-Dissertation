package com.tombleroneee.passwordmanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
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

        private fun updateRecyclerView() {
            if (arguments?.getInt(ARG_SECTION_NUMBER) == 1) {
                rootView.recyclerList.layoutManager = LinearLayoutManager(this.context, LinearLayout.VERTICAL, false)
                rootView.recyclerList.adapter = RecyclerClass(recyclerListList, context)
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

        fun Int.length() = when (this) {
            0 -> 1
            else -> Math.log10(Math.abs(toDouble())).toInt() + 1
        }

        private fun testPassword(password: String): Int {
            val passwordLength = password.length
            Log.d("TEST", "Pass: $password")
            Log.d("TEST", "Len: $passwordLength")


            // TODO: ADDITIONS - Done

            // Characters
            val testChars = +(passwordLength * 4)
            Log.d("TEST", "Chars: $testChars")

            var lengthChars = 0
            var lowerCaseNumber = 0
            var upperCaseNumber = 0
            var numbers = 0
            var symbols = 0
            var middleNumbersOrSymbols = 0

            for (i in 0 until passwordLength) {
                if (password[i].isLowerCase()) {
                    lowerCaseNumber++
                }
                if (password[i].isUpperCase()) {
                    upperCaseNumber++
                }
                if (password[i].isLetter()) {
                    lengthChars++
                }
                if (password[i].isDigit()) {
                    numbers++
                }
                if (!password[i].isLetter() && !password[i].isDigit()) {
                    symbols++
                }
                if (!password[i].isLetter()) {
                    if (i != 0 && i != passwordLength - 1)
                        middleNumbersOrSymbols++
                }
            }

            // Upper Case
            if (upperCaseNumber != 0) {
                val testUpper = ((passwordLength - upperCaseNumber) * 2)
                Log.d("TEST", "Upper: $testUpper")
            }

            // Lower Case
            if (lowerCaseNumber != 0) {
                val testLower = ((passwordLength - lowerCaseNumber) * 2)
                Log.d("TEST", "Lower: $testLower")
            }

            // Numbers
            val testNumbers = (numbers * 4)
            Log.d("TEST", "Numbers: $testNumbers")

            // Symbols
            val testSymbols = (symbols * 6)
            Log.d("TEST", "Symbols: $testSymbols")

            // Middle Numbers or Symbols
            val testNumbersOrSymbols = (middleNumbersOrSymbols * 2)
            Log.d("TEST", "Middle Numbers or Symbols: $testNumbersOrSymbols")

            // Minimum Requirements
            var lengthReached = false
            var minRequirements = 0

            if (passwordLength >= 8)
                lengthReached = true

            if (upperCaseNumber > 0)
                minRequirements++
            if (lowerCaseNumber > 0)
                minRequirements++
            if (numbers > 0)
                minRequirements++
            if (symbols > 0)
                minRequirements++

            if (lengthReached && minRequirements >= 3) {
                val testRequirements = ((minRequirements + 1) * 2) // 1 = lengthReached
                Log.d("TEST", "Requirements: $testRequirements")
            }

            // TODO: DEDUCTIONS
            // Letters
            val testDeductionLetters = (lengthChars)
            Log.d("TEST", "Letters: $testDeductionLetters")

            // Numbers
            val testDeductionNumbers = (numbers)
            Log.d("TEST", "Letters: $testDeductionNumbers")

            return 0
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            when {
                arguments?.getInt(ARG_SECTION_NUMBER) == 1 -> {
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
                                            ds.child("password").value.toString(),
                                            ds.child("title").ref,
                                            ds.child("username").ref,
                                            ds.child("password").ref
                                        )
                                    )
                                }
                                updateRecyclerView()
                            }
                        })


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
                                    Toast.makeText(context, "Stored Password!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            setNeutralButton("Cancel") { dialog, _ ->
                                dialog.cancel()
                            }

                            show()
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
                        val clipboardManager = this.context!!.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
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
                                    Toast.makeText(context, "Stored Password!", Toast.LENGTH_SHORT).show()
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

                    rootView.btnCheck.setOnClickListener {
                        val result = testPassword(rootView.txtPassInput.text.toString())
                        Toast.makeText(context, "$result%", Toast.LENGTH_SHORT).show()
                    }
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
            signOutDialogBox.setTitle("Sign Out?")
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
