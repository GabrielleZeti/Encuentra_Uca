package com.example.encuentra_uca.data.local

import com.example.encuentra_uca.data.remote.dto.ItemDto

fun ItemDto.toEntity(): ItemEntity = ItemEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    imageUrl = imageUrl,
    location = location,
    foundById = foundById,
    foundByEmail = foundByEmail,
    status = status,
    type = type,
    timestamp = timestamp
)

fun ItemEntity.toDto(): ItemDto = ItemDto(
    id = id,
    title = title,
    description = description,
    category = category,
    imageUrl = imageUrl,
    location = location,
    foundById = foundById,
    foundByEmail = foundByEmail,
    status = status,
    type = type,
    timestamp = timestamp
)