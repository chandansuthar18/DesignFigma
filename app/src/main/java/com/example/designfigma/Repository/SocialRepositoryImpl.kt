package com.example.designfigma.Repository

import com.example.designfigma.model.Friend
import com.example.designfigma.model.FriendRequest
import com.example.designfigma.model.UiState
import com.example.designfigma.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SocialRepositoryImpl @Inject constructor(
    private val db: FirebaseDatabase,
    private val auth: FirebaseAuth
) : SocialRepository {

    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    override suspend fun sendFriendRequest(receiverId: String): Result<Unit> = runCatching {
        if (currentUserId == receiverId) throw Exception("Cannot send to self")

        val senderRef = db.getReference("friend_requests/$currentUserId/$receiverId")
        val receiverRef = db.getReference("friend_requests/$receiverId/$currentUserId")

        // Check if already exists
        val existing = senderRef.get().await()
        if (existing.exists()) throw Exception("Request already pending")

        val requestId = senderRef.push().key ?: ""
        val request = FriendRequest(requestId, currentUserId, receiverId)

        val updates = hashMapOf<String, Any>(
            "friend_requests/$currentUserId/$receiverId" to request,
            "friend_requests/$receiverId/$currentUserId" to request
        )
        db.reference.updateChildren(updates).await()
    }

    override suspend fun acceptFriendRequest(request: FriendRequest): Result<Unit> = runCatching {
        val updates = hashMapOf<String, Any?>(
            // Add to both friends lists
            "friends/$currentUserId/${request.senderId}" to Friend(request.senderId),
            "friends/${request.senderId}/$currentUserId" to Friend(currentUserId),
            // Remove requests from both sides
            "friend_requests/$currentUserId/${request.senderId}" to null,
            "friend_requests/${request.senderId}/$currentUserId" to null
        )
        db.reference.updateChildren(updates).await()
    }

    override suspend fun rejectFriendRequest(request: FriendRequest): Result<Unit> = runCatching {
        val updates = hashMapOf<String, Any?>(
            "friend_requests/$currentUserId/${request.senderId}" to null,
            "friend_requests/${request.senderId}/$currentUserId" to null
        )
        db.reference.updateChildren(updates).await()
    }

    override suspend fun removeFriend(friendId: String): Result<Unit> = runCatching {
        val updates = hashMapOf<String, Any?>(
            "friends/$currentUserId/$friendId" to null,
            "friends/$friendId/$currentUserId" to null
        )
        db.reference.updateChildren(updates).await()
    }

    override fun getFriends(): Flow<UiState<List<User>>> = callbackFlow {
        val ref = db.getReference("friends/$currentUserId")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendIds = snapshot.children.mapNotNull { it.key }
                // In production, you'd fetch user details for these IDs
                trySend(UiState.Success(emptyList()))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(UiState.Error(error.message))
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getIncomingRequests(): Flow<UiState<List<FriendRequest>>> = callbackFlow {
        val ref = db.getReference("friend_requests/$currentUserId")
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(FriendRequest::class.java) }
                    .filter { it.receiverId == currentUserId }
                trySend(UiState.Success(list))
            }
            override fun onCancelled(error: DatabaseError) { trySend(UiState.Error(error.message)) }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun cancelSentRequest(request: FriendRequest): Result<Unit> = rejectFriendRequest(request)
    override fun getSentRequests(): Flow<UiState<List<FriendRequest>>> = emptyFlow()
}