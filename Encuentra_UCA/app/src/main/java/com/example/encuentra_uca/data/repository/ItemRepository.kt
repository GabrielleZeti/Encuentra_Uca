package com.example.encuentra_uca.data.repository

import com.example.encuentra_uca.data.local.ItemDao
import com.example.encuentra_uca.data.local.toDto
import com.example.encuentra_uca.data.local.toEntity
import com.example.encuentra_uca.data.remote.ItemsApiService
import com.example.encuentra_uca.data.remote.dto.CreateItemRequest
import com.example.encuentra_uca.data.remote.dto.ItemDto
import kotlinx.coroutines.flow.first

class ItemRepository(
    private val apiService: ItemsApiService,
    private val itemDao: ItemDao
) {
    suspend fun getItems(category: String? = null): Result<List<ItemDto>> {
        return try {
            val remoteItems = apiService.getItems(category)
            if (category == null) {
                itemDao.deleteAll()
                itemDao.insertAll(remoteItems.map { it.toEntity() })
            }
            Result.success(remoteItems)
        } catch (e: Exception) {
            try {
                val cachedItems = if (category != null) {
                    itemDao.getItemsByCategory(category).first()
                } else {
                    itemDao.getAllItems().first()
                }
                Result.success(cachedItems.map { it.toDto() })
            } catch (cacheError: Exception) {
                Result.failure(e)
            }
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
            val newItem = apiService.createItem(token, request)
            itemDao.insertAll(listOf(newItem.toEntity()))
            Result.success(newItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteItem(token: String, id: Int): Result<Unit> {
        return try {
            apiService.deleteItem(token, id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
