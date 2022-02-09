package com.glbgod.knowyourbudget.ui

import androidx.annotation.StringRes
import com.glbgod.knowyourbudget.R

sealed class Screen(val route: String, @StringRes val resourceId: Int, val iconResId: Int?) {
    object Home : Screen("home", R.string.home, null)
    object Change : Screen("change", R.string.change, null)
    object ChangeBudget : Screen("change_budget", R.string.change, null)


    object Budget : Screen("budget", R.string.budget, R.drawable.ic_budget)
    object History : Screen("history", R.string.history, R.drawable.ic_history)

}