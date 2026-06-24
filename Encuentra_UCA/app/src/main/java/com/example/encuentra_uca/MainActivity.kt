package com.example.encuentra_uca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.encuentra_uca.ui.AppViewModelFactory
import com.example.encuentra_uca.ui.navigation.AppNavGraph
import com.example.encuentra_uca.ui.theme.Encuentra_UCATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Encuentra_UCATheme {
                AppNavGraph(
                    viewModelFactory = AppViewModelFactory(applicationContext)
                )
            }
        }
    }
}