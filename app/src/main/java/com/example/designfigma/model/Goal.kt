package com.example.designfigma.model

data class Goal(
    val id: String = "",
    val name: String = "",
    val deadline: String = "",
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis()
)