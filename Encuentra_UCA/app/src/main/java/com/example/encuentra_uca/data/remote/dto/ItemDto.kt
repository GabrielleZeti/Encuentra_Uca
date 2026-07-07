package com.example.encuentra_uca.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val imageUrl: String,
    val location: String,
    val foundById: Int,
    val foundByEmail: String,
    val status: String,
    val type: String = "found",
    val timestamp: Long
)

@Serializable
data class CreateItemRequest(
    val title: String,
    val description: String,
    val category: String,
    val location: String,
    val imageUrl: String = "",
    val type: String = "found"
)