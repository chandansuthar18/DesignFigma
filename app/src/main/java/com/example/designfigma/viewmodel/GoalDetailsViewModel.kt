package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.designfigma.model.GoalData
import com.example.designfigma.model.Milestone
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoalDetailsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val _goal = MutableStateFlow<GoalData?>(null)
    val goal = _goal.asStateFlow()

    private val _milestones = MutableStateFlow<List<Milestone>>(emptyList())
    val milestones = _milestones.asStateFlow()

    private var isAddingDay = false

    fun fetchGoalDetails(creatorId: String, goalId: String) {
        val goalRef = database.child(creatorId).child("goals").child(goalId)

        // Goal Info Listener
        goalRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _goal.value = GoalData(
                    id = snapshot.key ?: "",
                    title = snapshot.child("title").value?.toString() ?: snapshot.child("name").value?.toString() ?: "Goal",
                    deadline = snapshot.child("deadline").value?.toString() ?: ""
                )
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Real-time Milestones Listener
        goalRef.child("milestones").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Milestone>()
                snapshot.children.forEach { snap ->
                    val m = snap.getValue(Milestone::class.java)
                    if (m != null) list.add(m.copy(id = snap.key ?: ""))
                }

                val sortedList = list.sortedBy { it.dayNumber }
                _milestones.value = sortedList

                // Only check for new day if we aren't already in the middle of adding one
                if (!isAddingDay) {
                    if (sortedList.isNotEmpty()) {
                        val last = sortedList.last()
                        // Only add Day X+1 if the current last day is 100% finished
                        if (last.userAChecked && last.userBChecked) {
                            addNewDay(creatorId, goalId, last.dayNumber + 1)
                        }
                    } else {
                        addNewDay(creatorId, goalId, 1)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addNewDay(creatorId: String, goalId: String, nextDay: Int) {
        isAddingDay = true // Lock creation
        val ref = database.child(creatorId).child("goals").child(goalId).child("milestones")

        val currentDays = _milestones.value.map { it.dayNumber }
        if (nextDay in currentDays) {
            isAddingDay = false
            return
        }

        val newId = ref.push().key ?: return
        ref.child(newId).setValue(Milestone(id = newId, dayNumber = nextDay))
            .addOnCompleteListener {
                isAddingDay = false // Unlock after Firebase confirms save
            }
    }

    fun toggleCheck(creatorId: String, goalId: String, milestone: Milestone) {
        val currentUid = auth.currentUser?.uid ?: return
        val ref = database.child(creatorId).child("goals").child(goalId)
            .child("milestones").child(milestone.id)

        if (currentUid == creatorId) {
            ref.child("userAChecked").setValue(!milestone.userAChecked)
        } else {
            ref.child("userBChecked").setValue(!milestone.userBChecked)
        }
    }
}