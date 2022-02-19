package com.glbgod.knowyourbudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.glbgod.knowyourbudget.core.utils.Debug
import com.glbgod.knowyourbudget.ui.Screen
import com.glbgod.knowyourbudget.ui.pages.home.HomePage
import com.glbgod.knowyourbudget.ui.theme.KnowYourBudgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KnowYourBudgetTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val mainNavController = rememberNavController()
                    NavHost(
                        navController = mainNavController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            Debug.log("Test")
                            HomePage(mainNavController)
                        }
                    }

                }
            }
        }
    }
}




