package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// State class to hold the live data
data class HomeProfileState(
    val userName: String = "User",
    val gender: String = "Girl"
)

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val _userData = MutableStateFlow(HomeProfileState())
    val userData = _userData.asStateFlow()

    private var databaseListener: ValueEventListener? = null

    init {
        observeUserData()
    }

    private fun observeUserData() {
        val uid = auth.currentUser?.uid ?: return

        databaseListener = database.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val name = snapshot.child("name").value?.toString()
                        ?: snapshot.child("informationscreen").child("nickname").value?.toString()
                        ?: "User"


                    val gender = snapshot.child("gender").value?.toString()
                        ?: snapshot.child("informationscreen").child("gender").value?.toString()
                        ?: "Girl"

                    _userData.value = HomeProfileState(userName = name, gender = gender)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        val uid = auth.currentUser?.uid
        if (uid != null && databaseListener != null) {
            database.child(uid).removeEventListener(databaseListener!!)
        }
    }
}