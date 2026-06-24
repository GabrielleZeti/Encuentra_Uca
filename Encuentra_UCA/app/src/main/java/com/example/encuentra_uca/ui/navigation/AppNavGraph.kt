package com.example.encuentra_uca.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.encuentra_uca.ui.AppViewModelFactory
import com.example.encuentra_uca.ui.screens.detail.DetailScreen
import com.example.encuentra_uca.ui.screens.home.HomeScreen
import com.example.encuentra_uca.ui.screens.login.LoginScreen
import com.example.encuentra_uca.ui.screens.profile.ProfileScreen
import com.example.encuentra_uca.ui.screens.publish.PublishScreen
import com.example.encuentra_uca.ui.screens.register.RegisterScreen
import com.example.encuentra_uca.ui.screens.splash.SplashScreen
import com.example.encuentra_uca.data.local.TokenManager

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Detail : Screen("detail/{itemId}") {
        fun createRoute(itemId: Int) = "detail/$itemId"
    }
    object Publish : Screen("publish")
    object Profile : Screen("profile")
}

@Composable
fun AppNavGraph(viewModelFactory: AppViewModelFactory) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                tokenManager = TokenManager(androidx.compose.ui.platform.LocalContext.current),
                onHasSession = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNoSession = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                viewModelFactory = viewModelFactory,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModelFactory = viewModelFactory,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModelFactory = viewModelFactory,
                onItemClick = { itemId ->
                    navController.navigate(Screen.Detail.createRoute(itemId))
                },
                onPublishClick = {
                    navController.navigate(Screen.Publish.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Detail.route) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: return@composable
            DetailScreen(
                itemId = itemId,
                viewModelFactory = viewModelFactory,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Publish.route) {
            PublishScreen(
                viewModelFactory = viewModelFactory,
                onPublishSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModelFactory = viewModelFactory,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}