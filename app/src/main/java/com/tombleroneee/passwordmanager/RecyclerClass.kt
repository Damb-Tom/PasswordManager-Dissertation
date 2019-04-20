package com.tombleroneee.passwordmanager

import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_layout.view.*

class RecyclerClass(private val recyclerList: ArrayList<RecyclerData>) :
    RecyclerView.Adapter<RecyclerClass.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerClass.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return recyclerList.size
    }

    override fun onBindViewHolder(holder: RecyclerClass.ViewHolder, position: Int) {
        holder.mainTitle.text = recyclerList[position].title
        holder.itemView.tag = position
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {
        val mainTitle = itemView.text!!
        private lateinit var database: DatabaseReference
        private lateinit var userId: String

        init {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                userId = user.uid
                database = FirebaseDatabase.getInstance().reference.child("stored_passwords").child(userId)
            }

            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {

            AlertDialog.Builder(itemView.context).apply {
                setTitle("What would you like to do?")

                setPositiveButton("Delete") { dialog, _ ->
                    dialog.dismiss()
                    recyclerListList[v!!.tag.toString().toInt()].urlRef.setValue(null)
                    recyclerListList[v.tag.toString().toInt()].usernameRef.setValue(null)
                    recyclerListList[v.tag.toString().toInt()].passwordRef.setValue(null)
                }

                setNeutralButton("Nothing") { dialog, _ ->
                    dialog.cancel()
                }
                show()
            }


            return true
        }
    }

}