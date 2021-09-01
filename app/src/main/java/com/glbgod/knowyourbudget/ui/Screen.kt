package com.glbgod.knowyourbudget.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.glbgod.knowyourbudget.R

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.home, Icons.Filled.Favorite)
    object Change : Screen("change", R.string.change, Icons.Filled.Favorite)
    object ChangeBudget : Screen("change_budget", R.string.change, Icons.Filled.Favorite)


    object Budget : Screen("budget", R.string.budget, Icons.Filled.Favorite)
    object Transactions : Screen("transactions", R.string.transactions, Icons.Filled.ShoppingCart)

}