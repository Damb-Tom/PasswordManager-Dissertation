package com.tombleroneee.passwordmanager

import com.google.firebase.database.DatabaseReference

data class RecyclerDataWithoutRef(val title: String, val username: String, val password: String)
data class RecyclerData(
    val title: String,
    val username: String,
    val password: String,
    val titleRef: DatabaseReference,
    val usernameRef: DatabaseReference,
    val passwordRef: DatabaseReference
)
var recyclerListList = ArrayList<RecyclerData>()
var tempRecyclerListList = ArrayList<RecyclerData>()
var tempRecyclerListList2 = ArrayList<RecyclerData>()