package com.glbgod.knowyourbudget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.glbgod.knowyourbudget.expenses.ExpenseRepository
import com.glbgod.knowyourbudget.expenses.ExpenseRoomDatabase
import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.expenses.data.ExpenseRegularity
import com.glbgod.knowyourbudget.transactions.TransactionsRepository
import com.glbgod.knowyourbudget.transactions.data.Transaction
import com.glbgod.knowyourbudget.transactions.data.TransactionCategory
import com.glbgod.knowyourbudget.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ExpensesViewModel(application: Application) : AndroidViewModel(application) {
    var currentExpense: MutableLiveData<Expense> = MutableLiveData()
    private var expensesRepository: ExpenseRepository = ExpenseRepository(
        ExpenseRoomDatabase.getDatabase(application, viewModelScope).expensesDao()
    )
    private var transactionsRepository: TransactionsRepository = TransactionsRepository(
        ExpenseRoomDatabase.getDatabase(application, viewModelScope).transactionsDao()
    )

    // todo: store expenses categorised not to do that on ui thread every update
    private val _allExpenses: MutableLiveData<List<Expense>> = MutableLiveData()
    val allExpenses: LiveData<List<Expense>> = _allExpenses

    private val _allTransactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val allTransactions: LiveData<List<Transaction>> = _allTransactions

    private val _currentBalance: MutableLiveData<CurrentBalance> = MutableLiveData()
    val currentBalance: LiveData<CurrentBalance> = _currentBalance

    init {
        PreferencesProvider.init(application)
        viewModelScope.launch(Dispatchers.IO){
            expensesRepository.getAllExpenses()
                .map { allExpenses ->
                    allExpenses
                }
                .collect { expenses->
                    //todo:
                    //  - improve performance here
                    val leftOverMoney = expenses.calculateLeftOver(stableIncome = PreferencesProvider.getStableIncome())
                    if (expenses.first { it.id == 1 }.budget !=leftOverMoney){
                        updateBudget(1, leftOverMoney)
                    }
                    _allExpenses.postValue(expenses)
                }
        }
        viewModelScope.launch(Dispatchers.IO){
            transactionsRepository.getAllTransactions()
                .map { allTransactions ->
                    Debug.log("all transactions: $allTransactions")
                    val viableTransactions = allTransactions.filter { it.time>=PreferencesProvider.getCycleStartTime() }
                    viableTransactions
                }
                .collect { transactions->
                    var sumSpent = 0
                    for (trans in transactions) {
                        if (trans.transactionCategory == TransactionCategory.SPENT.category) {
                            sumSpent += trans.change
                        } else if (trans.transactionCategory == TransactionCategory.INCREASE.category) {
                            sumSpent += trans.change
                        }
                    }
                    _currentBalance.postValue(
                        CurrentBalance(
                            monthStartBalance = PreferencesProvider.getMonthStartBalance(),
                            currentMoney = PreferencesProvider.getMonthStartBalance() + sumSpent,
                            stableIncome = PreferencesProvider.getStableIncome()
                        )
                    )
                    _allTransactions.postValue(transactions)
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val lastUpdateTime = PreferencesProvider.getLastUpdateTime()
            if (!PreferencesProvider.getFirstStarted()) {
                firstAppStart()
            } else if (lastUpdateTime != 0L && !lastUpdateTime.isToday()) {
                doRecalculation(lastUpdateTime)
            }
        }
    }

    private suspend fun doRecalculation(lastUpdateTime: Long,likeTodayIs:Long? = null) {
        val now = likeTodayIs?:System.currentTimeMillis()
        val daysPassed = lastUpdateTime.daysPassed(now)

        val cycleStart = PreferencesProvider.getCycleStartTime()
        val weeksPassed = lastUpdateTime.weeksPassed(cycleStart, now)

        if (daysPassed>0){
            expensesRepository.updateRegularBalances(regularity = ExpenseRegularity.DAILY.regularity,daysPassed.toInt())
        }
        if (weeksPassed>0){
            expensesRepository.updateRegularBalances(regularity = ExpenseRegularity.WEEKLY.regularity,weeksPassed)
        }
        PreferencesProvider.saveLastUpdateTime(System.currentTimeMillis())
    }

    private suspend fun updateExpenseBalance(
        expenseId: Int,
        expenseName: String,
        balanceUpdate: Int,
        date: Long,
        transactionCategory: Int
    ) {
        expensesRepository.updateBalance(expenseId, balanceUpdate)
        if (transactionCategory != -1) {
            transactionsRepository.insert(
                Transaction(
                    expenseId = expenseId,
                    expenseName = expenseName,
                    change = balanceUpdate,
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
        val stableIncome = SensetiveData.getStableIncome()
        PreferencesProvider.saveStableIncome(stableIncome)
//        PreferencesProvider.saveMonthStartBalance(stableIncome)
        _currentBalance.postValue(
            CurrentBalance(
                monthStartBalance = PreferencesProvider.getMonthStartBalance(),
                currentMoney = 0,
                stableIncome = PreferencesProvider.getStableIncome()
            )
        )
//        PreferencesProvider.saveCycleStartTime(now)
//        PreferencesProvider.saveLastUpdateTime(now)
        PreferencesProvider.saveFirstStarted(true)
    }

    fun moneyIncrease(isBudgetRestart: Boolean, change: Long,time:Long=0L) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isBudgetRestart) {
                if (time==0L) throw Exception("when resetting budget time should be provided")
                if (time>System.currentTimeMillis()) throw Exception("you can't update balance in future")
                //todo [release v0.2] add transactions for all updates?
                val lastUpdateTime = PreferencesProvider.getLastUpdateTime()
                val likeTodayIs = Utils.getCycleRestartTimeLong()
                PreferencesProvider.saveCycleStartTime(time)
                PreferencesProvider.saveLastUpdateTime(time)

                var stableIncome = PreferencesProvider.getStableIncome()
                PreferencesProvider.saveMonthStartBalance((_currentBalance.value?.currentMoney?:0)+stableIncome)

                _currentBalance.postValue(
                    CurrentBalance(
                        monthStartBalance = PreferencesProvider.getMonthStartBalance(),
                        currentMoney = PreferencesProvider.getMonthStartBalance(),
                        stableIncome = PreferencesProvider.getStableIncome()
                    )
                )
                if (lastUpdateTime==0L){
                    if (!time.isToday() && time<System.currentTimeMillis()){
                        Debug.log("budget update! from money increase restart")
                        expensesRepository.updateRegularBalances(ExpenseRegularity.DAILY.regularity,time.daysPassed(System.currentTimeMillis()).toInt()+1)
                        expensesRepository.updateRegularBalances(ExpenseRegularity.WEEKLY.regularity,time.weeksPassed(time,System.currentTimeMillis())+1)
                        expensesRepository.updateAllMonthlyBalances()
                    }
                    else{
                        expensesRepository.updateAllBalances()
                    }
                }
                else {
                    doRecalculation(lastUpdateTime,likeTodayIs = likeTodayIs)
                    expensesRepository.updateRegularBalances(ExpenseRegularity.DAILY.regularity,1)
                    expensesRepository.updateRegularBalances(ExpenseRegularity.WEEKLY.regularity,1)
                    expensesRepository.updateAllMonthlyBalances()
                }
            } else {
                updateExpenseBalance(
                    expenseId = 1,
                    expenseName = "Left over money",
                    balanceUpdate = change.toInt(),
                    date = System.currentTimeMillis(),
                    transactionCategory = TransactionCategory.INCREASE.category
                )
            }
        }
    }


    fun moneyDecrease(expense: Expense, change: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            updateExpenseBalance(
                expenseId = expense.id,
                expenseName = expense.name,
                balanceUpdate = -change.toInt(),
                date = System.currentTimeMillis(),
                transactionCategory = TransactionCategory.SPENT.category
            )
        }
    }

    fun setBudget(expense: Expense, newBudget: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Debug.log("set budget id: ${expense.id} budget: $newBudget")
            updateBudget(expense.id,newBudget)
        }
    }

    private suspend fun updateBudget(expenseId: Int, newBudget: Int){
        expensesRepository.updateBudget(expenseId = expenseId, newBudget = newBudget)
    }

    fun revertTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionsRepository.deleteById(transaction.id)
            updateExpenseBalance(
                expenseId = transaction.expenseId,
                expenseName = transaction.expenseName,
                balanceUpdate = -transaction.change,
                date = System.currentTimeMillis(),
                -1
            )
        }
    }

    fun sendMoneyToOther(expenseId: Int, expenseName: String, sum: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            updateExpenseBalance(
                expenseId = expenseId,
                expenseName = expenseName,
                balanceUpdate = -sum,
                date = System.currentTimeMillis(),
                transactionCategory = TransactionCategory.SPENT.category
            )
            updateExpenseBalance(
                expenseId = 1,
                expenseName = "Left over money",
                balanceUpdate = sum,
                date = System.currentTimeMillis(),
                transactionCategory = TransactionCategory.INCREASE.category
            )
        }
    }

}