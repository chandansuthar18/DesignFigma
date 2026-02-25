package com.example.designfigma.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Friend(
    val userId: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

data class FriendRequest(
    val requestId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val status: String = "pending", // pending, accepted, rejected
    val createdAt: Long = System.currentTimeMillis()
)

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}