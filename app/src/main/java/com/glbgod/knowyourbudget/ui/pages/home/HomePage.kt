package com.glbgod.knowyourbudget.ui.pages.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.ui.Screen
import com.glbgod.knowyourbudget.ui.pages.transactions.TransactionsPage
import com.glbgod.knowyourbudget.ui.pages.budgetlist.BudgetPage
import com.glbgod.knowyourbudget.utils.Debug
import com.glbgod.knowyourbudget.viewmodel.ExpensesViewModel

@Composable
fun HomePage(outterNavController: NavController, viewModel: ExpensesViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Budget,
        Screen.Transactions,
    )
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }, floatingActionButton = {
            FloatingActionButton(onClick = {
                onAddTransactionFabClick(
                    null,
                    outterNavController,
                    viewModel
                )
            }, content = {
                Icon(Icons.Default.Add, "")
            })
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Budget.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Budget.route) {
                BudgetPage(viewModel, onExpenseClick = {
                    onAddTransactionFabClick(it, outterNavController, viewModel)
                }, onLongPress = {
                    onLongPressTransaction(it, outterNavController, viewModel)
                })
            }
            composable(Screen.Transactions.route) { TransactionsPage(viewModel) }
        }
    }
}

private fun onLongPressTransaction(
    expense: Expense?,
    navController: NavController,
    viewModel: ExpensesViewModel
) {
    Debug.log("expense: ${expense?.name ?: "null"}")
    viewModel.currentExpense.postValue(expense)
    navController.navigate(Screen.ChangeBudget.route) {
        launchSingleTop = true
    }
}

private fun onAddTransactionFabClick(
    expense: Expense?,
    navController: NavController,
    viewModel: ExpensesViewModel
) {
    Debug.log("expense: ${expense?.name ?: "null"}")
    viewModel.currentExpense.postValue(expense)
    navController.navigate(Screen.Change.route) {
        launchSingleTop = true
    }
}