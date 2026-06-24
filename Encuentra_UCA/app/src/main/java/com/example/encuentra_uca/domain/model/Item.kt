package com.example.encuentra_uca.domain.model

data class Item(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val foundBy: String = "",
    val foundByEmail: String = "",
    val status: String = "available",
    val timestamp: Long = 0L
)