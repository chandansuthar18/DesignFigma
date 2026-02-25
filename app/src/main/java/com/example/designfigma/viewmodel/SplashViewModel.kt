package com.example.designfigma.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashViewModel : ViewModel() {
    private val _currentScreen = MutableStateFlow(0)
    val currentScreen: StateFlow<Int> = _currentScreen.asStateFlow()

    fun nextScreen() {
        if (_currentScreen.value < 3) {
            _currentScreen.value += 1
        }
    }

    fun previousScreen() {
        if (_currentScreen.value > 0) {
            _currentScreen.value -= 1
        }
    }

    fun isLastScreen(): Boolean = _currentScreen.value == 3
}