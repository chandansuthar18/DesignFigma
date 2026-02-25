package com.example.designfigma.model
data class GoalData(
    val id: String = "",
    val title: String = "",
    val creator: String = "",
    val creatorId: String = "",
    val deadline: String = "",
    val category: String = "",
    val isAchieved: Boolean = false
)