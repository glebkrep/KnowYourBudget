package com.glbgod.knowyourbudget.ui.pages.budgetlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.glbgod.knowyourbudget.core.utils.Debug
import com.glbgod.knowyourbudget.ui.BaseAction
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState
import com.glbgod.knowyourbudget.ui.pages.budgetlist.views.*

@Composable
fun BudgetPage(
    outterNavController: NavController,
    viewModel: BudgetPageVM = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.observeAsState()
    val action by viewModel.action.observeAsState()

    LaunchedEffect(key1 = action, block = {
        if (action is BaseAction.GoAway) {
            outterNavController.navigate((action as BaseAction.GoAway).route)
        }
    })
    if (state == null) return
    if (state is BudgetPageState.EditTotalBalanceDialog) {
        MoneyIncomeDialog(state as BudgetPageState.EditTotalBalanceDialog) {
            viewModel.handleEvent(it)
        }
    }
    if (state is BudgetPageState.EditBudgetPlannedDialog) {
        EditBudgetPlannedDialog(state as BudgetPageState.EditBudgetPlannedDialog) {
            viewModel.handleEvent(it)
        }
    }
    if (state is BudgetPageState.NewExpenseDialog) {
        EditingExpenseDialog(state = (state as BudgetPageState.NewExpenseDialog)) {
            Debug.log("adding invoked")
            viewModel.handleEvent(it)
        }
    }
    if (state is BudgetPageState.ExpenseEditDialog) {
        val editState = state as BudgetPageState.ExpenseEditDialog

        EditingExpenseDialog(
            state = (BudgetPageState.NewExpenseDialog(
                editState.newExpenseData,
                editState.totalBudgetData,
                editState.expensesData
            )), selectedExpense = editState.selectedExpense
        ) {
            Debug.log("editing invoked")
            viewModel.handleEvent(it)
        }
    }
    if (state is BudgetPageState.AddTransactionDialog) {
        AddingTransactionDialogView(
            state = (state as BudgetPageState.AddTransactionDialog),
            onEvent = {
                viewModel.handleEvent(it)
            }
        )
    }
    if (state is BudgetPageState.DeleteExpenseConfirmationDialog) {
        val state = state as BudgetPageState.DeleteExpenseConfirmationDialog
        ConfirmationDialog(
            "Удалить трату",
            "Вы точно хотите удалить трату '${state.selectedExpense.name}'?",
            onAccept = {
                viewModel.handleEvent(
                    BudgetPageEvent.DeleteExpenseFinished(
                        expenseItem = state.selectedExpense, isSuccess = true
                    )
                )
            },
            onCancel = {
                viewModel.handleEvent(
                    BudgetPageEvent.DeleteExpenseFinished(
                        expenseItem = state.selectedExpense, isSuccess = false
                    )
                )
            })
    }
    ExpensesPageView(viewModel = viewModel, state = state!!)

}

