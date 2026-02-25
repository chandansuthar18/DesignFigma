package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.designfigma.model.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateGoalViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _totalGoals = MutableStateFlow(0)
    val totalGoals = _totalGoals.asStateFlow()

    init {
        listenToGoalCount()
    }

    private fun listenToGoalCount() {
        val uid = auth.currentUser?.uid ?: return
        database.child(uid).child("goals").addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                _totalGoals.value = snapshot.childrenCount.toInt()
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }

    fun createGoal(name: String, deadline: String, category: String, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val userName = auth.currentUser?.displayName ?: "User"
        _isLoading.value = true

        val goalsRef = database.child(uid).child("goals")
        val goalId = goalsRef.push().key ?: ""

        val newGoal = mapOf(
            "id" to goalId,
            "title" to name,
            "deadline" to deadline,
            "category" to category,
            "creator" to userName,
            "creatorId" to uid,
            "isAchieved" to false
        )

        goalsRef.child(goalId).setValue(newGoal)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                onComplete(task.isSuccessful)
            }
    }
}