package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.designfigma.model.GoalData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewGoalViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val _userName = MutableStateFlow("User")
    val userName = _userName.asStateFlow()

    private val _userGender = MutableStateFlow("Girl")
    val userGender = _userGender.asStateFlow()

    private val _ongoingGoals = MutableStateFlow<List<GoalData>>(emptyList())
    val ongoingGoals = _ongoingGoals.asStateFlow()

    private val _achievedGoals = MutableStateFlow<List<GoalData>>(emptyList())
    val achievedGoals = _achievedGoals.asStateFlow()

    init {
        fetchProfileAndCommunityGoals()
    }

    private fun fetchProfileAndCommunityGoals() {
        val currentUid = auth.currentUser?.uid ?: return

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allCommunityGoals = mutableListOf<GoalData>()

                val currentUserSnap = snapshot.child(currentUid)
                _userName.value = currentUserSnap.child("name").value?.toString()
                    ?: currentUserSnap.child("informationscreen").child("nickname").value?.toString() ?: "User"
                _userGender.value = currentUserSnap.child("gender").value?.toString()
                    ?: currentUserSnap.child("informationscreen").child("gender").value?.toString() ?: "Girl"

                val myFriends = currentUserSnap.child("friends").children.mapNotNull { it.key }.toSet()

                for (userSnap in snapshot.children) {
                    val userId = userSnap.key ?: continue

                    // Only show goals for self and friends
                    if (userId == currentUid || myFriends.contains(userId)) {
                        val creatorName = userSnap.child("name").value?.toString()
                            ?: userSnap.child("informationscreen").child("nickname").value?.toString() ?: "User"

                        val goalsSnap = userSnap.child("goals")
                        for (goalSnap in goalsSnap.children) {
                            val goalTitle = goalSnap.child("title").value?.toString()
                                ?: goalSnap.child("name").value?.toString()
                                ?: "Untitled Goal"

                            val goal = GoalData(
                                id = goalSnap.key ?: "",
                                title = goalTitle,
                                creator = creatorName,
                                creatorId = userId,
                                deadline = goalSnap.child("deadline").value?.toString() ?: "No Deadline",
                                category = goalSnap.child("category").value?.toString() ?: "General",
                                isAchieved = goalSnap.child("achieved").value as? Boolean ?: false
                            )
                            allCommunityGoals.add(goal)
                        }
                    }
                }

                _ongoingGoals.value = allCommunityGoals.filter { !it.isAchieved }
                _achievedGoals.value = allCommunityGoals.filter { it.isAchieved }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun markAsAchieved(goalId: String) {
        val uid = auth.currentUser?.uid ?: return
        database.child(uid).child("goals").child(goalId).child("achieved").setValue(true)
    }
}