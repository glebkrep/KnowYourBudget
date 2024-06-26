package com.glbgod.knowyourbudget.ui.pages.budgetlist

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.glbgod.knowyourbudget.R
import com.glbgod.knowyourbudget.core.utils.Debug
import com.glbgod.knowyourbudget.core.utils.PreferencesProvider
import com.glbgod.knowyourbudget.data.AddingExpenseEditData
import com.glbgod.knowyourbudget.feature.db.BudgetRepository
import com.glbgod.knowyourbudget.feature.db.BudgetRoomDB
import com.glbgod.knowyourbudget.feature.db.data.*
import com.glbgod.knowyourbudget.ui.BaseAction
import com.glbgod.knowyourbudget.ui.Screen
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

    init {
        PreferencesProvider.init(application)
        viewModelScope.launch(Dispatchers.IO) {
            if (PreferencesProvider.isFirstStart()) {
                firstStart()
            }
            budgetRepository.getAllExpensesFlow()
                .collect { expenseModels ->
                    recalculateData(expenseModels)
                }
        }
    }

    private fun firstStart() {
        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.insertExpense(
                ExpenseModel(
                    expenseId = 0,
                    name = "Left over money",
                    regularity = ExpenseRegularity.MONTHLY.regularity,
                    category = "Other",
                    budgetPerRegularity = 0,
                    iconResId = R.drawable.ic_other
                )
            )
        }
        PreferencesProvider.saveNotFirstStart()
    }

    private fun restartBudget(additionalRestartMoney: Int, incomeTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions =
                budgetRepository.getAllTransactionsFromDate(PreferencesProvider.getCycleStartTime())
                    .map { it.change }
            val currentBalance = transactions.sum()
            PreferencesProvider.saveCycleStartTime(incomeTime)
            val monthStartBalance = currentBalance + additionalRestartMoney
            PreferencesProvider.saveMonthStartBalance(monthStartBalance)
            viewModelScope.launch(Dispatchers.IO) {
                budgetRepository.insertTransaction(
                    TransactionModel(
                        parentExpenseId = 1,
                        comment = "Заработок",
                        change = additionalRestartMoney,
                        time = incomeTime,
                        transactionCategory = 1
                    )
                )
                budgetRepository.insertTransaction(
                    TransactionModel(
                        parentExpenseId = 1,
                        comment = "Переход с прошлого месяца",
                        change = currentBalance,
                        time = incomeTime,
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
                        it.balancePlannedForPeriod * expenseRegularityByRegularityInt(
                            it.regularity.regularity
                        ).refillsInMonth
                    }.sum()
                    val freeFunds =
                        PreferencesProvider.getPlannedTotalBudget() - moneyOccupiedByAllExpenses
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
                if (event.expenseItem.id == 1) return
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
                        it.balancePlannedForPeriod * expenseRegularityByRegularityInt(
                            it.regularity.regularity
                        ).refillsInMonth
                    }.sum()
                    val freeFunds =
                        PreferencesProvider.getPlannedTotalBudget() - moneyOccupiedByAllExpenses
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
//                intVal - if (state.expenseItem.currentBalanceForPeriod > 0) state.expenseItem.currentBalanceForPeriod else 0
                Debug.log("adding transaction")
                viewModelScope.launch(Dispatchers.IO) {
                    val transactionSum = event.sum
                    val ableToSpendInCategory = event.expenseItem.currentBalanceForPeriod
                    if (transactionSum > ableToSpendInCategory && event.expenseItem.id != 1) {
                        val putToLOM =
                            transactionSum - if (ableToSpendInCategory > 0) ableToSpendInCategory else 0
                        budgetRepository.insertTransaction(
                            TransactionModel(
                                parentExpenseId = event.expenseItem.id,
                                change = -ableToSpendInCategory,
                                comment = event.comment,
                                time = System.currentTimeMillis(),
                                transactionCategory = TransactionCategory.SPENT.category
                            )
                        )
                        budgetRepository.insertTransaction(
                            TransactionModel(
                                parentExpenseId = 1,
                                change = -putToLOM,
                                comment = "[${event.expenseItem.name}]${" " + event.comment}",
                                time = System.currentTimeMillis(),
                                transactionCategory = TransactionCategory.SPENT.category
                            )
                        )
                    } else {
                        budgetRepository.insertTransaction(
                            TransactionModel(
                                parentExpenseId = event.expenseItem.id,
                                change = -event.sum,
                                comment = event.comment,
                                time = System.currentTimeMillis(),
                                transactionCategory = TransactionCategory.SPENT.category
                            )
                        )
                    }
                }
            }
            is BudgetPageEvent.EditTotalBalanceFinished -> {
                if (event.isRestart) {
                    restartBudget(event.balanceAdded, event.incomeTime)
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        budgetRepository.insertTransaction(
                            TransactionModel(
                                parentExpenseId = 1,
                                comment = "Дополнительный заработок",
                                change = event.balanceAdded,
                                time = event.incomeTime,
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
                    viewModelScope.launch(Dispatchers.IO) {
                        val allTransactions =
                            budgetRepository.getAllTransactionsForExpense(event.expenseItem)
                        Debug.log("transactions for ${event.expenseItem.name}: ${allTransactions.map { it.change.toString() }}")
                        for (transaction in allTransactions) {
                            budgetRepository.insertTransaction(
                                transactionModel = transaction.copy(
                                    id = 0,
                                    parentExpenseId = 1
                                )
                            )
                        }
                        budgetRepository.deleteExpense(event.expenseItem)
                    }
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
                postAction(BaseAction.GoAway(Screen.Settings.route))
            }
            is BudgetPageEvent.EditBudgetPlannedClicked -> {
                val currentState = getCurrentStateNotNull()
                postState(
                    BudgetPageState.EditBudgetPlannedDialog(
                        _totalBudgetData = currentState.totalBudgetData,
                        _expensesData = currentState.expensesData
                    )
                )
            }
            is BudgetPageEvent.EditBudgetPlannedFinished -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val curEvent = event
                    val currentState = getCurrentStateNotNull()
                    PreferencesProvider.savePlannedTotalBudget(curEvent.newBalance)
                    budgetRepository.fakeUpdateExpense(currentState.expensesData.monthly.first().items.first())
                }
            }
            is BudgetPageEvent.TransferToLOMFinished -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val fromItem = event.fromExpenseItem
                    budgetRepository.insertTransaction(
                        TransactionModel(
                            parentExpenseId = fromItem.id,
                            change = -fromItem.currentBalanceForPeriod,
                            comment = "Перенос в остаток",
                            time = System.currentTimeMillis(),
                            transactionCategory = TransactionCategory.SPENT.category
                        )
                    )
                    budgetRepository.insertTransaction(
                        TransactionModel(
                            parentExpenseId = 1,
                            change = fromItem.currentBalanceForPeriod,
                            comment = "Перенос из [${fromItem.name}]",
                            time = System.currentTimeMillis(),
                            transactionCategory = TransactionCategory.SPENT.category
                        )
                    )
                }
            }

        }
    }

}
