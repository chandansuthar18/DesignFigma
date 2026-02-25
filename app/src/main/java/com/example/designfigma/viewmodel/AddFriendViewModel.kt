package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.designfigma.model.UserCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddFriendViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val _usersList = MutableStateFlow<List<UserCard>>(emptyList())
    val usersList = _usersList.asStateFlow()

    private val _sentRequests = MutableStateFlow<Set<String>>(emptySet())
    val sentRequests = _sentRequests.asStateFlow()

    // New state to track who is already a friend
    private val _friendsSet = MutableStateFlow<Set<String>>(emptySet())
    val friendsSet = _friendsSet.asStateFlow()

    init {
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        val currentUid = auth.currentUser?.uid ?: return

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<UserCard>()
                val alreadySentSet = mutableSetOf<String>()
                val currentFriends = mutableSetOf<String>()

                for (userSnap in snapshot.children) {
                    val targetUid = userSnap.key ?: ""

                    if (targetUid != currentUid) {
                        // 1. Check if already friends
                        val isFriend = userSnap.child("friends").hasChild(currentUid)

                        if (isFriend) {
                            currentFriends.add(targetUid)
                        } else {
                            // 2. Only check for "sent requests" if they aren't friends yet
                            if (userSnap.child("friendRequests").hasChild(currentUid)) {
                                alreadySentSet.add(targetUid)
                            }
                        }

                        // 3. Data Parsing
                        val name = userSnap.child("name").value?.toString()
                            ?: userSnap.child("informationscreen").child("nickname").value?.toString() ?: "User"
                        val gender = userSnap.child("gender").value?.toString()
                            ?: userSnap.child("informationscreen").child("gender").value?.toString() ?: "Girl"

                        list.add(UserCard(targetUid, name, gender))
                    }
                }
                _friendsSet.value = currentFriends
                _sentRequests.value = alreadySentSet
                _usersList.value = list
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendFriendRequest(targetUid: String) {
        // Double check: don't send if already friends
        if (_friendsSet.value.contains(targetUid)) return

        val currentUid = auth.currentUser?.uid ?: return
        val requestData = mapOf(
            "fromUid" to currentUid,
            "status" to "pending",
            "timestamp" to ServerValue.TIMESTAMP
        )
        database.child(targetUid).child("friendRequests").child(currentUid).setValue(requestData)
    }

    fun cancelFriendRequest(targetUid: String) {
        val currentUid = auth.currentUser?.uid ?: return
        database.child(targetUid).child("friendRequests").child(currentUid).removeValue()
    }
}