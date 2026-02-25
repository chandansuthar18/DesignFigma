package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Represents the various states of the Authentication process.
 */
sealed class AuthResult {
    object NewUser : AuthResult()           // Authenticated but no profile yet
    object ReturningUser : AuthResult()     // Authenticated with existing profile
    object NotLoggedIn : AuthResult()        // No active session
    object SignUpSuccess : AuthResult()     // Account created successfully
    data class Error(val message: String) : AuthResult()
}

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    // State for showing progress bars in the UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // SharedFlow for navigation events (ensures events aren't re-fired on rotation)
    private val _authResult = MutableSharedFlow<AuthResult>()
    val authResult = _authResult.asSharedFlow()

    /**
     * SPLASH LOGIC: Check if user session exists and where to direct them.
     */
    fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                checkUserProfile(currentUser.uid)
            } else {
                _authResult.emit(AuthResult.NotLoggedIn)
            }
        }
    }

    /**
     * LOGIN LOGIC: Authenticate existing user.
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
                result.user?.uid?.let { uid ->
                    checkUserProfile(uid)
                }
            } catch (e: Exception) {
                _authResult.emit(AuthResult.Error(e.message ?: "Login failed"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * REGISTRATION LOGIC: Create account and save initial user data.
     */
    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
                val uid = result.user?.uid

                if (uid != null) {
                    val userMap = mapOf(
                        "name" to name,
                        "email" to email,
                        "uid" to uid
                    )
                    // Save base user info to RTDB
                    database.child(uid).setValue(userMap).await()
                    _authResult.emit(AuthResult.SignUpSuccess)
                }
            } catch (e: Exception) {
                _authResult.emit(AuthResult.Error(e.message ?: "Registration failed"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * LOGOUT LOGIC
     */
    fun signOut() {
        auth.signOut()
        viewModelScope.launch {
            _authResult.emit(AuthResult.NotLoggedIn)
        }
    }

    /**
     * HELPER: Decision maker based on database "informationscreen" node.
     */
    private suspend fun checkUserProfile(uid: String) {
        try {
            val snapshot = database.child(uid).child("informationscreen").get().await()
            if (snapshot.exists()) {
                _authResult.emit(AuthResult.ReturningUser)
            } else {
                _authResult.emit(AuthResult.NewUser)
            }
        } catch (e: Exception) {
            _authResult.emit(AuthResult.Error("Database error: ${e.message}"))
        }
    }
}