package com.example.encuentra_uca.data.repository

import com.example.encuentra_uca.data.local.TokenManager
import com.example.encuentra_uca.data.remote.AuthApiService
import com.example.encuentra_uca.data.remote.dto.AuthResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AuthRepository(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) {
    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.register(name, email, password)
            tokenManager.saveSession(response.token, response.user.email, response.user.name)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.login(email, password)
            tokenManager.saveSession(response.token, response.user.email, response.user.name)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clearSession()
    }

    fun getTokenFlow() = tokenManager.tokenFlow

    fun getUserInfo(): Flow<Pair<String, String>> {
        return combine(tokenManager.userNameFlow, tokenManager.userEmailFlow) { name, email ->
            Pair(name ?: "", email ?: "")
        }
    }
}