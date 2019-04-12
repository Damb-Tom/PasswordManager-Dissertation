package com.tombleroneee.passwordmanager

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class RecyclerClass(private val recyclerList: ArrayList<RecyclerData>): RecyclerView.Adapter<RecyclerClass.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerClass.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return recyclerList.size
    }

    override fun onBindViewHolder(holder: RecyclerClass.ViewHolder, position: Int) {
        holder.mainTitle.text = recyclerList[position].title
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mainTitle = itemView.findViewById<TextView>(R.id.text)!!
    }
}