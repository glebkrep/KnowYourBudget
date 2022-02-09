package com.glbgod.knowyourbudget.ui.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.glbgod.knowyourbudget.ui.Screen
import com.glbgod.knowyourbudget.ui.pages.budgetlist.BudgetPage
import com.glbgod.knowyourbudget.ui.theme.MyColors

@Composable
fun HomePage(
    outterNavController: NavController,
) {

    val navController = rememberNavController()
    val items = listOf(
        Screen.Budget,
        Screen.History,
    )
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                painterResource(id = screen.iconResId!!),
                                contentDescription = null
                            )
                        },
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
                        },
                        selectedContentColor = MyColors.SelectedColor,
                        unselectedContentColor = MyColors.UnSelectedColor,
                        modifier = Modifier
                            .background(Color.White)
                            .border(width = 1.dp, MyColors.GrayColor)
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Budget.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Budget.route) {
                BudgetPage(navController)
            }
//            composable(Screen.History.route) { TransactionsPage(viewModel) }
        }
    }
}
