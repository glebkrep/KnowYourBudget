package com.glbgod.knowyourbudget.ui.pages.budgetlist

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.glbgod.knowyourbudget.core.utils.Debug
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
    if (state is BudgetPageState.NewExpenseDialog){
        AddingExpenseDialog(state = (state as BudgetPageState.NewExpenseDialog)){
            Debug.log("adding invoked")
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

