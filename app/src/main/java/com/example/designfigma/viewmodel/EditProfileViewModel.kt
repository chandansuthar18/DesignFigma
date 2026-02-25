package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val name: String = "",
    val gender: String = "",
    val age: String = "",
    val contactNumber: String = "",
    val email: String = ""
)

class EditProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("users")

    private val _uiState = MutableStateFlow(UserProfile())
    val uiState = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.child(uid).get().await()
                val profile = snapshot.getValue(UserProfile::class.java)
                if (profile != null) {
                    _uiState.value = profile
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateName(newName: String) { _uiState.value = _uiState.value.copy(name = newName) }
    fun updateGender(newGender: String) { _uiState.value = _uiState.value.copy(gender = newGender) }
    fun updateAge(newAge: String) { _uiState.value = _uiState.value.copy(age = newAge) }
    fun updateContact(newContact: String) { _uiState.value = _uiState.value.copy(contactNumber = newContact) }
    fun updateEmail(newEmail: String) { _uiState.value = _uiState.value.copy(email = newEmail) }

    fun saveProfile(onComplete: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                db.child(uid).updateChildren(
                    mapOf(
                        "name" to _uiState.value.name,
                        "gender" to _uiState.value.gender,
                        "age" to _uiState.value.age,
                        "contactNumber" to _uiState.value.contactNumber,
                        "email" to _uiState.value.email
                    )
                ).await()
                onComplete()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}