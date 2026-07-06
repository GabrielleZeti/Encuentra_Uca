package com.example.encuentra_uca.data.remote

import com.example.encuentra_uca.data.remote.dto.CreateItemRequest
import com.example.encuentra_uca.data.remote.dto.ItemDto
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ItemsApiService {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.BASE_URL

    suspend fun getItems(category: String? = null): List<ItemDto> {
        val url = if (category != null) "$baseUrl/items?category=$category"
        else "$baseUrl/items"
        return client.get(url).body()
    }

    suspend fun getItemById(id: Int): ItemDto {
        return client.get("$baseUrl/items/$id").body()
    }

    suspend fun createItem(token: String, request: CreateItemRequest): ItemDto {
        return client.post("$baseUrl/items") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(request)
        }.body()
    }

    suspend fun deleteItem(token: String, id: Int) {
        client.delete("$baseUrl/items/$id") {
            header("Authorization", "Bearer $token")
        }
    }
}