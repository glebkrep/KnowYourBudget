package com.glbgod.knowyourbudget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.glbgod.knowyourbudget.expenses.ExpenseRepository
import com.glbgod.knowyourbudget.expenses.ExpenseRoomDatabase
import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.expenses.data.ExpenseRegularity
import com.glbgod.knowyourbudget.transactions.TransactionsRepository
import com.glbgod.knowyourbudget.transactions.data.Transaction
import com.glbgod.knowyourbudget.transactions.data.TransactionCategory
import com.glbgod.knowyourbudget.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpensesViewModel(application: Application) : AndroidViewModel(application) {
    var currentExpense: MutableLiveData<Expense> = MutableLiveData()
    private var expensesRepository: ExpenseRepository = ExpenseRepository(
        ExpenseRoomDatabase.getDatabase(application, viewModelScope).expensesDao()
    )
    private var transactionsRepository: TransactionsRepository = TransactionsRepository(
        ExpenseRoomDatabase.getDatabase(application, viewModelScope).transactionsDao()

    )

    private val _allExpenses: MutableLiveData<List<Expense>> = MutableLiveData()
    val allExpenses: LiveData<List<Expense>> = _allExpenses

    private val _allTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val allTransactions: LiveData<List<Transaction>> = _allTransactions

    private val _stableIncome: MutableLiveData<Int> = MutableLiveData()
    val stableIncome: LiveData<Int> = _stableIncome

    private val _currentMoney: MutableLiveData<Int> = MutableLiveData()
    val currentMoney: LiveData<Int> = _currentMoney

    init {
        PreferencesProvider.init(application)
        viewModelScope.launch(Dispatchers.IO) {
            val data = getAndPostAll()
            val lastUpdateTime = PreferencesProvider.getLastUpdateTime()
            if (lastUpdateTime == 0L) {
                firstAppStart()
            } else if (!lastUpdateTime.isToday()) {
                // data needs updating since it was last calculated yesterday+
                doRecalculation(lastUpdateTime, data.expenses)
            }
        }
    }

    private suspend fun getAndPostAll(): AllBudgetData {
        val outdatedExpenses = expensesRepository.getAllExpenses()
        _allExpenses.postValue(outdatedExpenses)
        val outdatedTransactions = transactionsRepository.getAllTransactions()
        _allTransactions.postValue(outdatedTransactions)
        _stableIncome.postValue(PreferencesProvider.getStableIncome())
        val transactionsAfterCycleStart =
            transactionsRepository.getTransactionsAfter(PreferencesProvider.getCycleStartTime())
        var sumSpent = 0
        for (trans in transactionsAfterCycleStart) {
            if (trans.transactionCategory == TransactionCategory.SPENT.category) {
                sumSpent += trans.change
            } else if (trans.transactionCategory == TransactionCategory.INCREASE.category) {
                sumSpent += trans.change
            }
        }
        _currentMoney.postValue(PreferencesProvider.getStableIncome() + sumSpent)

        return AllBudgetData(expenses = outdatedExpenses, transactions = outdatedTransactions)
    }

    private suspend fun doRecalculation(lastUpdateTime: Long, outdatedExpenses: List<Expense>) {
        val now = System.currentTimeMillis()
        val daysPassed = lastUpdateTime.daysPassed(now)

        val cycleStart = PreferencesProvider.getCycleStartTime()
        val weeksPassed = lastUpdateTime.weeksPassed(cycleStart, now)

        for (expense in outdatedExpenses) {
            when (expense.regularity) {
                ExpenseRegularity.DAILY.regularity -> {
                    for (day in 1..daysPassed) {
                        val newIncrease = expense.budget * day
                        val newDate = lastUpdateTime.plusDays(day.toInt())
                        updateExpenseBalance(
                            expense,
                            (expense.currentBalance + (newIncrease.toInt())),
                            (expense.budget),
                            newDate,
                            TransactionCategory.REGULAR.category
                        )
                    }
                }
                ExpenseRegularity.WEEKLY.regularity -> {
                    for (week in 1..weeksPassed) {
                        val newIncrease = expense.budget * week
                        val newDate = lastUpdateTime.plusDays(week * 7)
                        updateExpenseBalance(
                            expense,
                            (expense.currentBalance + (newIncrease)),
                            (expense.budget),
                            newDate,
                            TransactionCategory.REGULAR.category
                        )
                    }
                }
                else -> {
                }
            }
        }
        getAndPostAll()
        PreferencesProvider.saveLastUpdateTime(now)
    }

    private suspend fun updateExpenseBalance(
        expense: Expense,
        newBalance: Int,
        change: Int,
        date: Long,
        transactionCategory: Int
    ) {
        expensesRepository.updateBalance(expense.id, newBalance)
        if (transactionCategory != -1) {
            transactionsRepository.insert(
                Transaction(
                    expenseId = expense.id,
                    name = expense.name,
                    change = change,
                    time = date,
                    transactionCategory = transactionCategory
                )
            )
        }
    }

    //todo [pre-release]:
    //  - insert expenses from user input
    //  - insert stable income from user input
    private suspend fun firstAppStart() {
        val now = System.currentTimeMillis()
        for (expense in SensetiveData.firstInitData()) {
            expensesRepository.insert(expense)
        }
        val allExpenses = expensesRepository.getAllExpenses()
        _allExpenses.postValue(allExpenses)
        val stableIncome = SensetiveData.getStableIncome()
        PreferencesProvider.saveStableIncome(stableIncome)
        _stableIncome.postValue(PreferencesProvider.getStableIncome())

        val leftOverMoney = allExpenses.calculateLeftOver(stableIncome = stableIncome)
        setBudget(1, leftOverMoney)

        PreferencesProvider.saveCycleStartTime(now)
        PreferencesProvider.saveLastUpdateTime(now)
    }

    fun moneyIncrease(isBudgetRestart: Boolean, change: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val expenses = _allExpenses.value ?: listOf()

            if (isBudgetRestart) {
                for (expense in expenses) {
                    updateExpenseBalance(
                        expense,
                        (expense.currentBalance + expense.budget),
                        expense.budget,
                        System.currentTimeMillis(),
                        TransactionCategory.REGULAR.category
                    )
                }
                val now = System.currentTimeMillis()
                PreferencesProvider.saveCycleStartTime(now)
                PreferencesProvider.saveLastUpdateTime(now)
            } else {
                for (expense in expenses) {
                    if (expense.id == 1) {
                        updateExpenseBalance(
                            expense,
                            (expense.currentBalance + change.toInt()),
                            change.toInt(),
                            System.currentTimeMillis(),
                            TransactionCategory.INCREASE.category
                        )
                        break
                    }
                }
            }
            getAndPostAll()
        }
    }

    fun moneyDecrease(expense: Expense, change: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            updateExpenseBalance(
                expense,
                (expense.currentBalance - change.toInt()),
                -change.toInt(),
                System.currentTimeMillis(),
                TransactionCategory.SPENT.category
            )
            getAndPostAll()
        }
    }

    fun setBudget(expense: Expense, newBudget: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            setBudget(expenseId = expense.id, newBudget)
        }
    }

    private suspend fun setBudget(expenseId: Int, newBudget: Int) {
        Debug.log("set budget id: $expenseId budget: $newBudget")
        expensesRepository.updateBudget(expenseId, newBudget = newBudget)
        if (expenseId != 1) {
            setBudget(
                1,
                expensesRepository.getAllExpenses()
                    .calculateLeftOver(PreferencesProvider.getStableIncome())
            )
            return
        } else {
            Debug.log("get and post all")
            getAndPostAll()
        }
    }

    fun revertTransactionFromIo(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            revertTransaction(transaction)
            getAndPostAll()
        }
    }

    private suspend fun revertTransaction(transaction: Transaction) {
        transactionsRepository.deleteById(transaction.id)
        val expenseList = expensesRepository.getExpenseById(transaction.expenseId)
        if (expenseList.isNotEmpty()) {
            val expense = expenseList.first()
            updateExpenseBalance(
                expense,
                expense.currentBalance + (-transaction.change),
                (-transaction.change),
                System.currentTimeMillis(),
                -1
            )
        }
    }

    fun sendMoneyToOther(expense: Expense, sum: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            updateExpenseBalance(
                expense,
                (expense.currentBalance - sum),
                -sum,
                System.currentTimeMillis(),
                TransactionCategory.SPENT.category
            )
            val allExpenses = expensesRepository.getAllExpenses()
            val otherExpense = allExpenses.filter { it.id == 1 }.first()
            updateExpenseBalance(
                otherExpense,
                (otherExpense.currentBalance + sum),
                sum,
                System.currentTimeMillis(),
                TransactionCategory.INCREASE.category
            )
            getAndPostAll()
        }
    }

}