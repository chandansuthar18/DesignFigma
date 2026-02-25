package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val _userData = MutableStateFlow(UserProfile())
    val userData = _userData.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        val uid = auth.currentUser?.uid ?: return

        database.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value?.toString() ?:
                snapshot.child("informationscreen").child("nickname").value?.toString() ?: ""

                val email = snapshot.child("email").value?.toString() ?: auth.currentUser?.email ?: ""

                val contact = snapshot.child("contactNumber").value?.toString() ?:
                snapshot.child("informationscreen").child("contact").value?.toString() ?: ""

                val gender = snapshot.child("gender").value?.toString() ?:
                snapshot.child("informationscreen").child("gender").value?.toString() ?: "Girl"

                _userData.value = UserProfile(
                    name = name,
                    email = email,
                    contactNumber = contact,
                    gender = gender
                )
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}