package com.glbgod.knowyourbudget.ui.pages.transactionsList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.glbgod.knowyourbudget.ui.pages.budgetlist.views.ConfirmationDialog
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageEvent
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageState
import com.glbgod.knowyourbudget.ui.pages.transactionsList.views.EditingTransactionDialog
import com.glbgod.knowyourbudget.ui.pages.transactionsList.views.TransactionPageListView

@Composable
fun TransactionPage(
    outterNavController: NavController,
    transactionVM: TransactionsVM = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val state by transactionVM.state.observeAsState()
    if (state == null) return
    if (state is TransactionsPageState.EditDialog) {
        val state = state as TransactionsPageState.EditDialog
        EditingTransactionDialog(state.selectedItem) {
            transactionVM.handleEvent(it)
        }
    }
    if (state is TransactionsPageState.DeleteConfirmationDialog) {
        val state = state as TransactionsPageState.DeleteConfirmationDialog
        ConfirmationDialog(
            "Удалить транзакцию",
            "Вы точно хотите удалить транзакцию '${state.selectedItem.expenseName}'?",
            onAccept = {
                transactionVM.handleEvent(
                    TransactionsPageEvent.OnTransactionDeleteSuccess(
                        transactionItem = state.selectedItem,

                        )
                )
            },
            onCancel = {
                transactionVM.handleEvent(
                    TransactionsPageEvent.DismissDialog
                )
            })
    }
    TransactionPageListView(state = state!!) {
        transactionVM.handleEvent(
            it
        )
    }

}