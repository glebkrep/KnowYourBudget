package com.glbgod.knowyourbudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.glbgod.knowyourbudget.ui.ChangeBudgetPage
import com.glbgod.knowyourbudget.ui.ChangePage
import com.glbgod.knowyourbudget.ui.HomePage
import com.glbgod.knowyourbudget.ui.Screen
import com.glbgod.knowyourbudget.ui.theme.KnowYourBudgetTheme
import com.glbgod.knowyourbudget.viewmodel.ExpensesViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ExpensesViewModel by viewModels()

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
                        composable(Screen.Home.route) { HomePage(mainNavController, viewModel) }
                        composable(Screen.Change.route) { ChangePage(mainNavController, viewModel) }
                        composable(Screen.ChangeBudget.route) {
                            ChangeBudgetPage(
                                mainNavController,
                                viewModel
                            )
                        }

                    }
                }
            }
        }
    }
}



