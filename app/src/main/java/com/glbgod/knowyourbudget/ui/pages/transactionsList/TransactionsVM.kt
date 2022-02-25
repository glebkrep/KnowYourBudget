package com.glbgod.knowyourbudget.ui.pages.transactionsList

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.glbgod.knowyourbudget.core.utils.PreferencesProvider
import com.glbgod.knowyourbudget.feature.db.BudgetRepository
import com.glbgod.knowyourbudget.feature.db.BudgetRoomDB
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageEvent
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransactionsVM(application: Application) : TransactionsPageVMAbs(application) {
    private var budgetRepository: BudgetRepository = BudgetRepository(
        BudgetRoomDB.getDatabase(application, viewModelScope).expensesDao(),
        BudgetRoomDB.getDatabase(application, viewModelScope).transactionsDao()
    )

    init {
        PreferencesProvider.init(application)
        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.getAllExpensesFlow()
                .collect { expenses ->
                    workWithData(expenses)
                }
        }
    }

    override fun handleEvent(event: TransactionsPageEvent) {
        when (event) {
            is TransactionsPageEvent.DismissDialog -> {
                val state = getCurrentStateNotNull()
                postState(
                    TransactionsPageState.DefaultState(
                        state.transactionItems
                    )
                )
            }
            is TransactionsPageEvent.OnTransactionClicked -> {
                val selectedItem = event.transactionItem
                val state = getCurrentStateNotNull()
                postState(
                    TransactionsPageState.EditDialog(
                        selectedItem = selectedItem,
                        _transactionItems = state.transactionItems
                    )
                )
            }
            is TransactionsPageEvent.OnTransactionDeleteClicked -> {
                val selectedItem = event.transactionItem
                val state = getCurrentStateNotNull()
                postState(
                    TransactionsPageState.DeleteConfirmationDialog(
                        selectedItem = selectedItem,
                        _transactionItems = state.transactionItems
                    )
                )
            }
            is TransactionsPageEvent.OnTransactionDeleteSuccess -> {
                val selectedItem = event.transactionItem
                viewModelScope.launch(Dispatchers.IO) {
                    budgetRepository.deleteTransaction(selectedItem)
                }
            }
            is TransactionsPageEvent.OnTransactionEditSuccess -> {
                val selectedItem = event.transactionItem
                val newValue = event.newValue
                viewModelScope.launch(Dispatchers.IO) {
                    budgetRepository.updateTransaction(selectedItem, -newValue)
                }
            }
        }
    }

}
