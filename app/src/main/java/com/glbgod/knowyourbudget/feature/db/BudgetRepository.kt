package com.glbgod.knowyourbudget.feature.db

import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel

class BudgetRepository(
    private val expensesDao: ExpensesDao,
    private val transactionsDao: TransactionDao
) {

    fun getAllExpensesFlow() = expensesDao.getAllItemsFlow()

    suspend fun getAllTransactionsFromDate(dateTime:Long) = transactionsDao.getAllItemsFromDate(dateTime)

    suspend fun insertTransaction(transactionModel: TransactionModel) {
        transactionsDao.insert(transactionModel)
    }

    suspend fun insertExpense(expenseModel: ExpenseModel) {
        expensesDao.insert(expenseModel)
    }

}
