package com.example.designfigma.model

data class Milestone(
    val id: String = "",
    val dayNumber: Int = 1,
    val userAChecked: Boolean = false,
    val userBChecked: Boolean = false
)
