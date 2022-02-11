package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.glbgod.knowyourbudget.ui.pages.budgetlist.BudgetPageVM
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState

@Composable
fun ExpensesPageView(viewModel: BudgetPageVM, state: BudgetPageState) {
    val scrollState = rememberScrollState()
    Column(Modifier.verticalScroll(scrollState)) {
        BudgetTopBar(state.totalBudgetData, onEvent = {
            viewModel.handleEvent(it)
        })

        var isDailyOpen by remember { mutableStateOf(false) }
        var isWeeklyOpen by remember { mutableStateOf(false) }
        var isMonthlyOpen by remember { mutableStateOf(false) }

        //daily
        ExpenseInOneRegularity(
            expenseCategoriesData = state.expensesData.daily,
            isOpen = isDailyOpen,
            onReceivedEvent = {
                viewModel.handleEvent(it)
            },
            onRegularityClick = { isDailyOpen = !isDailyOpen },
        )
        //weekly
        ExpenseInOneRegularity(
            expenseCategoriesData = state.expensesData.weekly,
            isOpen = isWeeklyOpen,
            onReceivedEvent = {
                viewModel.handleEvent(it)
            },
            onRegularityClick = { isWeeklyOpen = !isWeeklyOpen })
        //monthly
        ExpenseInOneRegularity(
            expenseCategoriesData = state.expensesData.monthly,
            isOpen = isMonthlyOpen,
            onReceivedEvent = {
                viewModel.handleEvent(it)
            },
            onRegularityClick = { isMonthlyOpen = !isMonthlyOpen })
    }
}
