package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.designfigma.model.UserCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FriendsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val _incomingRequests = MutableStateFlow<List<UserCard>>(emptyList())
    val incomingRequests = _incomingRequests.asStateFlow()

    private val _friendsList = MutableStateFlow<List<UserCard>>(emptyList())
    val friendsList = _friendsList.asStateFlow()

    init {
        observeIncomingRequests()
        observeFriendsList()
    }

    private fun observeIncomingRequests() {
        val currentUid = auth.currentUser?.uid ?: return
        database.child(currentUid).child("friendRequests").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fetchUsersFromKeys(snapshot) { _incomingRequests.value = it }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun observeFriendsList() {
        val currentUid = auth.currentUser?.uid ?: return
        database.child(currentUid).child("friends").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fetchUsersFromKeys(snapshot) { _friendsList.value = it }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Helper to fetch full user details from a list of UIDs
    private fun fetchUsersFromKeys(snapshot: DataSnapshot, onComplete: (List<UserCard>) -> Unit) {
        val uids = snapshot.children.mapNotNull { it.key }
        if (uids.isEmpty()) {
            onComplete(emptyList())
            return
        }

        val users = mutableListOf<UserCard>()
        var count = 0
        uids.forEach { uid ->
            database.child(uid).get().addOnSuccessListener { userSnap ->
                val name = userSnap.child("name").value?.toString()
                    ?: userSnap.child("informationscreen").child("nickname").value?.toString() ?: "User"
                val gender = userSnap.child("gender").value?.toString()
                    ?: userSnap.child("informationscreen").child("gender").value?.toString() ?: "Girl"

                users.add(UserCard(uid, name, gender))
                count++
                if (count == uids.size) onComplete(users)
            }
        }
    }

    fun acceptFriendRequest(senderUid: String) {
        val currentUid = auth.currentUser?.uid ?: return
        database.child(currentUid).child("friends").child(senderUid).setValue(true)
        database.child(senderUid).child("friends").child(currentUid).setValue(true)
        database.child(currentUid).child("friendRequests").child(senderUid).removeValue()
    }

    fun declineFriendRequest(senderUid: String) {
        val currentUid = auth.currentUser?.uid ?: return
        database.child(currentUid).child("friendRequests").child(senderUid).removeValue()
    }
}