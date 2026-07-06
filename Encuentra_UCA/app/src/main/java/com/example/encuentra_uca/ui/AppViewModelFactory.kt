package com.example.encuentra_uca.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.encuentra_uca.data.local.AppDatabase
import com.example.encuentra_uca.data.local.TokenManager
import com.example.encuentra_uca.data.remote.AuthApiService
import com.example.encuentra_uca.data.remote.ItemsApiService
import com.example.encuentra_uca.data.repository.AuthRepository
import com.example.encuentra_uca.data.repository.ItemRepository
import com.example.encuentra_uca.ui.screens.detail.DetailViewModel
import com.example.encuentra_uca.ui.screens.home.HomeViewModel
import com.example.encuentra_uca.ui.screens.login.LoginViewModel
import com.example.encuentra_uca.ui.screens.profile.ProfileViewModel
import com.example.encuentra_uca.ui.screens.publish.PublishViewModel
import com.example.encuentra_uca.ui.screens.register.RegisterViewModel

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val authApiService = AuthApiService()
    private val itemsApiService = ItemsApiService()
    private val tokenManager = TokenManager(context)
    private val database = AppDatabase.getInstance(context)
    private val itemDao = database.itemDao()
    private val authRepository = AuthRepository(authApiService, tokenManager)
    private val itemRepository = ItemRepository(itemsApiService, itemDao)

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(authRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(authRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(itemRepository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) ->
                DetailViewModel(itemRepository, tokenManager) as T
            modelClass.isAssignableFrom(PublishViewModel::class.java) ->
                PublishViewModel(itemRepository, tokenManager) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(authRepository) as T
            else ->
                throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}