package com.glbgod.knowyourbudget.ui.pages.budgetlist

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.glbgod.knowyourbudget.core.utils.Debug
import com.glbgod.knowyourbudget.core.utils.PreferencesProvider
import com.glbgod.knowyourbudget.core.utils.SensetiveData
import com.glbgod.knowyourbudget.data.AddingExpenseEditData
import com.glbgod.knowyourbudget.feature.db.BudgetRepository
import com.glbgod.knowyourbudget.feature.db.BudgetRoomDB
import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity
import com.glbgod.knowyourbudget.feature.db.data.TransactionCategory
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel
import com.glbgod.knowyourbudget.feature.db.data.expenseRegularityByRegularityInt
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState
import com.glbgod.knowyourbudget.ui.theme.UiConsts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
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
                postState(
                    BudgetPageState.AddTransactionDialog(
                        expenseItem = event.expenseItem,
                        _totalBudgetData = getCurrentStateNotNull().totalBudgetData,
                        _expensesData = getCurrentStateNotNull().expensesData
                    )
                )
            }
            is BudgetPageEvent.AddExpenseClicked -> {
                Debug.log("adding expense")
                val addExpenseEvent: BudgetPageEvent.AddExpenseClicked = event
                val regularity = addExpenseEvent.regularity.regularity
                viewModelScope.launch(Dispatchers.IO) {
                    val currentState = getCurrentStateNotNull()
                    val allExpensesInThisRegularity = when (event.regularity.regularity) {
                        ExpenseRegularity.DAILY.regularity -> currentState.expensesData.daily
                        ExpenseRegularity.WEEKLY.regularity -> currentState.expensesData.weekly
                        else -> currentState.expensesData.monthly
                    }
                    val allCategoriesInRegularity =
                        allExpensesInThisRegularity.map { it.categoryName }

                    val allExpenses =
                        (currentState.expensesData.daily + currentState.expensesData.weekly + currentState.expensesData.monthly)
                    val moneyOccupiedByAllExpenses = allExpenses.map { it.items }.flatten().map {
                        if (it.id == 1) return@map 0
                        it.totalBalanceForPeriod * expenseRegularityByRegularityInt(
                            it.regularity.regularity
                        ).refillsInMonth
                    }.sum()
                    val freeFunds =
                        PreferencesProvider.getRestartMoney() - moneyOccupiedByAllExpenses
                    postState(
                        BudgetPageState.NewExpenseDialog(
                            newExpenseData = AddingExpenseEditData(
                                regularity = expenseRegularityByRegularityInt(regularity),
                                iconsIdList = UiConsts.iconsMap.map { it.key },
                                categoryList = allCategoriesInRegularity,
                                freeToUseFunds = freeFunds
                            ),
                            _totalBudgetData = currentState.totalBudgetData,
                            _expensesData = currentState.expensesData
                        )
                    )
                }
            }
            is BudgetPageEvent.EditExpenseClicked -> {
                Debug.log("editing expense")
                val regularity = event.expenseItem.regularity.regularity
                viewModelScope.launch(Dispatchers.IO) {
                    val currentState = getCurrentStateNotNull()
                    val allExpensesInThisRegularity =
                        when (event.expenseItem.regularity.regularity) {
                            ExpenseRegularity.DAILY.regularity -> currentState.expensesData.daily
                            ExpenseRegularity.WEEKLY.regularity -> currentState.expensesData.weekly
                            else -> currentState.expensesData.monthly
                        }
                    val allCategoriesInRegularity =
                        allExpensesInThisRegularity.map { it.categoryName }

                    val allExpenses =
                        (currentState.expensesData.daily + currentState.expensesData.weekly + currentState.expensesData.monthly)
                    val moneyOccupiedByAllExpenses = allExpenses.map { it.items }.flatten().map {
                        if (it.id == 1) return@map 0
                        if (it.id == event.expenseItem.id) return@map 0
                        it.totalBalanceForPeriod * expenseRegularityByRegularityInt(
                            it.regularity.regularity
                        ).refillsInMonth
                    }.sum()
                    val freeFunds =
                        PreferencesProvider.getRestartMoney() - moneyOccupiedByAllExpenses
                    postState(
                        BudgetPageState.ExpenseEditDialog(
                            selectedExpense = event.expenseItem,
                            newExpenseData = AddingExpenseEditData(
                                regularity = expenseRegularityByRegularityInt(regularity),
                                iconsIdList = UiConsts.iconsMap.map { it.key },
                                categoryList = allCategoriesInRegularity,
                                freeToUseFunds = freeFunds
                            ),
                            _totalBudgetData = currentState.totalBudgetData,
                            _expensesData = currentState.expensesData
                        )
                    )
                }
            }
            is BudgetPageEvent.EditExpenseFinished -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (getCurrentStateNotNull() is BudgetPageState.NewExpenseDialog) {
                        budgetRepository.insertExpense(
                            expenseModel = event.newExpenseModel
                        )
                    } else if (getCurrentStateNotNull() is BudgetPageState.ExpenseEditDialog) {
                        val state = getCurrentStateNotNull() as BudgetPageState.ExpenseEditDialog
                        budgetRepository.updateExpense(
                            id = state.selectedExpense.id,
                            newExpenseModel = event.newExpenseModel
                        )
                    }
                }
            }
            is BudgetPageEvent.AddTransactionToExpenseFinished -> {
                Debug.log("adding transaction")
                viewModelScope.launch(Dispatchers.IO) {
                    budgetRepository.insertTransaction(
                        TransactionModel(
                            parentExpenseId = event.expenseItem.id,
                            expenseName = event.expenseItem.name,
                            change = -event.sum,
                            comment = event.comment,
                            time = System.currentTimeMillis(),
                            transactionCategory = TransactionCategory.SPENT.category
                        )
                    )
                }
            }
            is BudgetPageEvent.EditTotalBalanceFinished -> {
                if (event.isRestart) {
                    restartBudget(event.balanceAdded)
                } else {
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
            is BudgetPageEvent.DialogDismissed -> {
                postState(
                    BudgetPageState.NoDialogs(
                        getCurrentStateNotNull().totalBudgetData,
                        getCurrentStateNotNull().expensesData
                    )
                )
            }
            is BudgetPageEvent.DeleteExpenseClicked -> {
                val state = getCurrentStateNotNull() as BudgetPageState.ExpenseEditDialog
                postState(
                    BudgetPageState.DeleteExpenseConfirmationDialog(
                        selectedExpense = state.selectedExpense,
                        newExpenseData = state.newExpenseData,
                        _totalBudgetData = state.totalBudgetData,
                        _expensesData = state.expensesData
                    )
                )
            }
            is BudgetPageEvent.DeleteExpenseFinished -> {
                val state =
                    getCurrentStateNotNull() as BudgetPageState.DeleteExpenseConfirmationDialog
                if (event.isSuccess) {
                    TODO()
                } else {
                    postState(
                        BudgetPageState.ExpenseEditDialog(
                            selectedExpense = state.selectedExpense,
                            newExpenseData = state.newExpenseData,
                            _expensesData = state.expensesData,
                            _totalBudgetData = state.totalBudgetData
                        )
                    )
                }
            }
            is BudgetPageEvent.SettingsClicked -> {
                TODO()
            }
        }
    }

}
