package com.example.encuentra_uca.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
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