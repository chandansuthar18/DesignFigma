package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designfigma.Repository.SocialRepository
import com.example.designfigma.model.FriendRequest
import com.example.designfigma.model.UiState
import com.example.designfigma.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val repository: SocialRepository
) : ViewModel() {

    private val _uiActionState = MutableStateFlow<UiState<Unit>?>(null)
    val uiActionState = _uiActionState.asStateFlow()

    val incomingRequests: StateFlow<UiState<List<FriendRequest>>> = repository.getIncomingRequests()
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), UiState.Loading)

    val friends: StateFlow<UiState<List<User>>> = repository.getFriends()
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), UiState.Loading)

    fun sendRequest(receiverId: String) {
        viewModelScope.launch {
            _uiActionState.value = UiState.Loading
            repository.sendFriendRequest(receiverId)
                .onSuccess { _uiActionState.value = UiState.Success(Unit) }
                .onFailure { _uiActionState.value = UiState.Error(it.message ?: "Failed") }
        }
    }

    fun acceptRequest(request: FriendRequest) {
        viewModelScope.launch { repository.acceptFriendRequest(request) }
    }
}