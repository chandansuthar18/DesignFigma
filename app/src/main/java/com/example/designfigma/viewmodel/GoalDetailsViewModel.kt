package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.designfigma.model.GoalData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoalDetailsViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val _goal = MutableStateFlow<GoalData?>(null)
    val goal = _goal.asStateFlow()

    fun fetchGoalDetails(creatorId: String, goalId: String) {
        database.child(creatorId).child("goals").child(goalId).get().addOnSuccessListener { snapshot ->
            val data = GoalData(
                id = snapshot.key ?: "",
                title = snapshot.child("name").value?.toString() ?: "",
                deadline = snapshot.child("deadline").value?.toString() ?: "",
                category = snapshot.child("category").value?.toString() ?: "",
                isAchieved = snapshot.child("achieved").value as? Boolean ?: false
            )
            _goal.value = data
        }
    }
}