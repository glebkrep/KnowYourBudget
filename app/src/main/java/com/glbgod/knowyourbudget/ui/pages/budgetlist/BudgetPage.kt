package com.glbgod.knowyourbudget.ui.pages.budgetlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState
import com.glbgod.knowyourbudget.ui.pages.budgetlist.views.*

@Composable
fun BudgetPage(
    outterNavController: NavController,
    viewModel: BudgetPageVM = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.observeAsState()
    if (state == null) return
    if (state is BudgetPageState.EditTotalBalanceDialog){
        MoneyIncomeDialog(state as BudgetPageState.EditTotalBalanceDialog){
            viewModel.handleEvent(it)
        }
    }
    if (state is BudgetPageState.AddTransactionDialog){
        AddingTransactionDialogView(
            state = (state as BudgetPageState.AddTransactionDialog),
            onEvent = {
                viewModel.handleEvent(it)
            }
        )
    }
    ExpensesPageView(viewModel = viewModel, state = state!!)

}

