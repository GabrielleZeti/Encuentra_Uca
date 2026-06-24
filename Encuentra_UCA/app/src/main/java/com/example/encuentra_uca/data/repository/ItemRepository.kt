package com.example.encuentra_uca.data.repository

import com.example.encuentra_uca.data.remote.ItemsApiService
import com.example.encuentra_uca.data.remote.dto.CreateItemRequest
import com.example.encuentra_uca.data.remote.dto.ItemDto

class ItemRepository(
    private val apiService: ItemsApiService
) {
    suspend fun getItems(category: String? = null): Result<List<ItemDto>> {
        return try {
            Result.success(apiService.getItems(category))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemById(id: Int): Result<ItemDto> {
        return try {
            Result.success(apiService.getItemById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createItem(token: String, request: CreateItemRequest): Result<ItemDto> {
        return try {
            Result.success(apiService.createItem(token, request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}