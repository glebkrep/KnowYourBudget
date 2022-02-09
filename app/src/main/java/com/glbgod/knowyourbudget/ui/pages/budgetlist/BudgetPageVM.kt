package com.glbgod.knowyourbudget.ui.pages.budgetlist

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.glbgod.knowyourbudget.R
import com.glbgod.knowyourbudget.core.utils.Debug
import com.glbgod.knowyourbudget.core.utils.PreferencesProvider
import com.glbgod.knowyourbudget.core.utils.SensetiveData
import com.glbgod.knowyourbudget.feature.db.BudgetRepository
import com.glbgod.knowyourbudget.feature.db.BudgetRoomDB
import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.TransactionCategory
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BudgetPageVM(application: Application) : BudgetPageVMAbs(application) {

    private var budgetRepository: BudgetRepository = BudgetRepository(
        BudgetRoomDB.getDatabase(application, viewModelScope).expensesDao(),
        BudgetRoomDB.getDatabase(application, viewModelScope).transactionsDao()
    )

    //todo when starting new month copy leftover money as transactions this month
    init {
        PreferencesProvider.init(application)
        viewModelScope.launch(Dispatchers.IO) {
            firstStartMock()
            budgetRepository.getAllExpensesFlow()
                .collect { expenseModels ->
                    recalculateData(expenseModels)
                }
        }
    }

    private suspend fun firstStartMock() {
        if (PreferencesProvider.isFirstStart()) {
            Debug.log("expense models empty -> mocking data")
            val mockData = SensetiveData.firstInitData()
            for (item in mockData) {
                budgetRepository.insertExpense(item)
            }
            restartBudget(100000)
            PreferencesProvider.saveNotFirstStart()
        }
    }

    private fun restartBudget(additionalRestartMoney: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            //todo currentBalance
            val transactions =
                budgetRepository.getAllTransactionsFromDate(PreferencesProvider.getCycleStartTime())
                    .map { it.change }
            val currentBalance = transactions.sum()
            PreferencesProvider.saveCycleStartTime(System.currentTimeMillis())
            PreferencesProvider.saveRestartMoney(additionalRestartMoney)
            val monthStartBalance = currentBalance + additionalRestartMoney
            PreferencesProvider.saveMonthStartBalance(monthStartBalance)
            viewModelScope.launch(Dispatchers.IO) {
                budgetRepository.insertTransaction(
                    TransactionModel(
                        parentExpenseId = 1,
                        expenseName = "Заработок",
                        change = additionalRestartMoney,
                        time = System.currentTimeMillis(),
                        transactionCategory = 1
                    )
                )
                budgetRepository.insertTransaction(
                    TransactionModel(
                        parentExpenseId = 1,
                        expenseName = "Переход с прошлого месяца",
                        change = currentBalance,
                        time = System.currentTimeMillis(),
                        transactionCategory = 1
                    )
                )
            }
        }
    }


    override fun handleEvent(event: BudgetPageEvent) {
        when (event) {
            is BudgetPageEvent.EditTotalBalanceClicked -> {
                postState(
                    BudgetPageState.EditTotalBalanceDialog(
                        getCurrentStateNotNull().totalBudgetData,
                        getCurrentStateNotNull().expensesData
                    )
                )
            }
            is BudgetPageEvent.AddTransactionToExpenseClicked -> {
                Debug.log("adding transaction")
                val addTransactionEvent: BudgetPageEvent.AddTransactionToExpenseClicked = event
                viewModelScope.launch(Dispatchers.IO) {
                    budgetRepository.insertTransaction(
                        TransactionModel(
                            parentExpenseId = addTransactionEvent.expenseItem.id,
                            expenseName = addTransactionEvent.expenseItem.name,
                            change = -125,
                            time = System.currentTimeMillis(),
                            transactionCategory = TransactionCategory.SPENT.category
                        )
                    )
                }
            }
            is BudgetPageEvent.AddExpenseClicked -> {
                Debug.log("adding expense")
                val addExpenseEvent: BudgetPageEvent.AddExpenseClicked = event
                viewModelScope.launch(Dispatchers.IO) {
                    budgetRepository.insertExpense(
                        ExpenseModel(
                            name = "test",
                            iconResId = R.drawable.ic_car,
                            regularity = addExpenseEvent.regularity.regularity,
                            category = "testovaya",
                            budgetPerRegularity = 1000
                        )
                    )
                }
            }
            is BudgetPageEvent.AddTransactionToExpenseFinished -> {
                TODO()
            }
            is BudgetPageEvent.DeleteExpenseClicked -> {
                TODO()
            }
            is BudgetPageEvent.DeleteExpenseSuccess -> {
                TODO()
            }
            BudgetPageEvent.DialogDismissed -> {
                postState(
                    BudgetPageState.NoDialogs(
                        getCurrentStateNotNull().totalBudgetData,
                        getCurrentStateNotNull().expensesData
                    )
                )
            }
            is BudgetPageEvent.EditExpenseClicked -> {
                TODO()
            }
            is BudgetPageEvent.EditExpenseFinished -> {
                TODO()
            }
            is BudgetPageEvent.EditTotalBalanceFinished -> {
                if (event.isRestart){
                    restartBudget(event.balanceAdded)
                }
                else {
                    viewModelScope.launch(Dispatchers.IO) {
                        budgetRepository.insertTransaction(
                            TransactionModel(
                                parentExpenseId = 1,
                                expenseName = "Дополнительный заработок",
                                change = event.balanceAdded,
                                time = System.currentTimeMillis(),
                                transactionCategory = 1
                            )
                        )
                    }
                }
            }
            is BudgetPageEvent.OnExpenseOpenCloseClicked -> {
                TODO()
            }
            is BudgetPageEvent.SettingsClicked -> {
                TODO()
            }
        }
    }

}
