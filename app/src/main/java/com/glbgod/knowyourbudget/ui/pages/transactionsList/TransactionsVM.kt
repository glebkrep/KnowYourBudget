package com.glbgod.knowyourbudget.ui.pages.transactionsList

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.glbgod.knowyourbudget.core.utils.PreferencesProvider
import com.glbgod.knowyourbudget.feature.db.BudgetRepository
import com.glbgod.knowyourbudget.feature.db.BudgetRoomDB
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageEvent
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
        TODO("Not yet implemented")
    }

}
