package com.example.designfigma.Repository

import com.example.designfigma.model.FriendRequest
import com.example.designfigma.model.UiState
import com.example.designfigma.model.User
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    suspend fun sendFriendRequest(receiverId: String): Result<Unit>
    suspend fun acceptFriendRequest(request: FriendRequest): Result<Unit>
    suspend fun rejectFriendRequest(request: FriendRequest): Result<Unit>
    suspend fun cancelSentRequest(request: FriendRequest): Result<Unit>
    suspend fun removeFriend(friendId: String): Result<Unit>

    fun getFriends(): Flow<UiState<List<User>>>
    fun getIncomingRequests(): Flow<UiState<List<FriendRequest>>>
    fun getSentRequests(): Flow<UiState<List<FriendRequest>>>
}