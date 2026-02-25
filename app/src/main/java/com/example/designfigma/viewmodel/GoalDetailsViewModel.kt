package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import com.example.designfigma.model.GoalData
import com.example.designfigma.model.Milestone
import com.example.designfigma.model.Message
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

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun fetchGoalDetails(creatorId: String, goalId: String) {
        val goalRef = database.child(creatorId).child("goals").child(goalId)

        goalRef.child("name").get().addOnSuccessListener { snapshot ->
            _goal.value = GoalData(title = snapshot.value?.toString() ?: "Goal")
        }

        goalRef.child("milestones").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Milestone>()
                snapshot.children.forEach { snap ->
                    snap.getValue(Milestone::class.java)?.let { list.add(it.copy(id = snap.key!!)) }
                }
                val sortedList = list.sortedBy { it.dayNumber }
                _milestones.value = sortedList

                if (sortedList.isEmpty()) {
                    addNewDay(creatorId, goalId, 1)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        goalRef.child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val msgs = mutableListOf<Message>()
                snapshot.children.forEach { snap ->
                    snap.getValue(Message::class.java)?.let { msgs.add(it) }
                }
                _messages.value = msgs.sortedBy { it.timestamp }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun toggleCheck(creatorId: String, goalId: String, milestone: Milestone) {
        val currentUid = auth.currentUser?.uid ?: return
        val milestoneRef = database.child(creatorId).child("goals").child(goalId)
            .child("milestones").child(milestone.id)

        val isCreator = currentUid == creatorId
        val updateField = if (isCreator) "userAChecked" else "userBChecked"
        val newValue = if (isCreator) !milestone.userAChecked else !milestone.userBChecked

        // Update the checkmark
        milestoneRef.child(updateField).setValue(newValue).addOnSuccessListener {
            checkForNextDayProgression(creatorId, goalId, milestone.id)
        }
    }

    private fun checkForNextDayProgression(creatorId: String, goalId: String, milestoneId: String) {
        val ref = database.child(creatorId).child("goals").child(goalId).child("milestones")

        ref.child(milestoneId).get().addOnSuccessListener { snapshot ->
            val m = snapshot.getValue(Milestone::class.java) ?: return@addOnSuccessListener

            // LOGIC: Only proceed if BOTH friends have checked the current day
            if (m.userAChecked && m.userBChecked) {
                val nextDayNumber = m.dayNumber + 1

                // Query to see if the next day already exists to prevent duplicates
                ref.orderByChild("dayNumber").equalTo(nextDayNumber.toDouble()).get()
                    .addOnSuccessListener { nextDaySnapshot ->
                        if (!nextDaySnapshot.exists()) {
                            addNewDay(creatorId, goalId, nextDayNumber)
                        }
                    }
            }
        }
    }

    private fun addNewDay(creatorId: String, goalId: String, dayNumber: Int) {
        val ref = database.child(creatorId).child("goals").child(goalId).child("milestones")
        val newId = ref.push().key ?: return
        val newMilestone = Milestone(id = newId, dayNumber = dayNumber)
        ref.child(newId).setValue(newMilestone)
    }

    fun sendMessage(creatorId: String, goalId: String, text: String) {
        if (text.isBlank()) return
        val currentUid = auth.currentUser?.uid ?: return
        val ref = database.child(creatorId).child("goals").child(goalId).child("messages")
        ref.push().setValue(Message(senderId = currentUid, text = text, timestamp = System.currentTimeMillis()))
    }
}