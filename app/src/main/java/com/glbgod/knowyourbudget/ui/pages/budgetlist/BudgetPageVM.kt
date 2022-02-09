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
import com.glbgod.knowyourbudget.feature.db.data.ExpenseWithTransactionsModel
import com.glbgod.knowyourbudget.feature.db.data.TransactionCategory
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BudgetPageVM(application: Application) : BudgetPageVMAbs(application) {

    private var expensesRepository: BudgetRepository = BudgetRepository(
        BudgetRoomDB.getDatabase(application, viewModelScope).expensesDao(),
        BudgetRoomDB.getDatabase(application, viewModelScope).transactionsDao()
    )

    //todo when starting new month copy leftover money as transactions this month
    init {
        PreferencesProvider.init(application)
        viewModelScope.launch(Dispatchers.IO) {
            firstStartMock()
            expensesRepository.getAllExpensesFlow()
                .collect { expenseModels ->
                    recalculateData(expenseModels)
                }
        }
    }

    private fun recalculateData(
        expenseModels: List<ExpenseWithTransactionsModel>,
    ) {
        val transactions = expenseModels.map { it.transactions }.flatten()
        val totalBudgetData = getTotalBudgetData(transactions)
        val expensesData = getExpensesData(expenseModels)

        if ((expensesData.daily + expensesData.monthly + expensesData.weekly).isNotEmpty()) {
            postState(BudgetPageState.NoDialogs(totalBudgetData, expensesData))
        }
    }

    private fun mockFirstStart(restartMoney: Int, isMock: Boolean = false) {
        PreferencesProvider.saveCycleStartTime(System.currentTimeMillis())
        PreferencesProvider.saveRestartMoney(restartMoney)
        val monthStartBalance = if (!isMock) {
            val currentBalance: Int = TODO()
            currentBalance + restartMoney
        } else restartMoney
        PreferencesProvider.saveMonthStartBalance(monthStartBalance)
        viewModelScope.launch(Dispatchers.IO) {
            expensesRepository.insertTransaction(
                TransactionModel(
                    parentExpenseId = 1,
                    expenseName = "Заработок",
                    change = restartMoney,
                    time = System.currentTimeMillis(),
                    transactionCategory = 1
                )
            )
            expensesRepository.insertTransaction(
                TransactionModel(
                    parentExpenseId = 1,
                    expenseName = "Переход с прошлого месяца",
                    change = monthStartBalance-restartMoney,
                    time = System.currentTimeMillis(),
                    transactionCategory = 1
                )
            )
        }

    }

    private suspend fun firstStartMock() {
        if (PreferencesProvider.isFirstStart()) {
            Debug.log("expense models empty -> mocking data")
            val mockData = SensetiveData.firstInitData()
            for (item in mockData) {
                expensesRepository.insertExpense(item)
            }
            mockFirstStart(160000, isMock = true)
            PreferencesProvider.saveNotFirstStart()
        }
    }


    override fun handleEvent(event: BudgetPageEvent) {
        when (event) {
            is BudgetPageEvent.AddTransactionToExpenseClicked -> {
                Debug.log("adding transaction")
                val addTransactionEvent: BudgetPageEvent.AddTransactionToExpenseClicked = event
                viewModelScope.launch(Dispatchers.IO) {
                    expensesRepository.insertTransaction(
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
                    expensesRepository.insertExpense(
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
                TODO()
            }
            is BudgetPageEvent.EditExpenseClicked -> {
                TODO()
            }
            is BudgetPageEvent.EditExpenseFinished -> {
                TODO()
            }
            BudgetPageEvent.EditTotalBalanceClicked -> {
                TODO()
            }
            is BudgetPageEvent.EditTotalBalanceFinished -> {
                TODO()
            }
            is BudgetPageEvent.OnExpenseOpenCloseClicked -> {
                TODO()
            }
            BudgetPageEvent.SettingsClicked -> {
                TODO()
            }
        }
    }

}
