package com.example.encuentra_uca.data.remote

import com.example.encuentra_uca.data.remote.dto.AuthResponse
import com.example.encuentra_uca.data.remote.dto.LoginRequest
import com.example.encuentra_uca.data.remote.dto.RegisterRequest
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApiService {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.BASE_URL

    suspend fun register(name: String, email: String, password: String): AuthResponse {
        return client.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(name, email, password))
        }.body()
    }

    suspend fun login(email: String, password: String): AuthResponse {
        return client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }.body()
    }
}