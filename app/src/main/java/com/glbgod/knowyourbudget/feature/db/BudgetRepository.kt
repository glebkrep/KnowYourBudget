package com.glbgod.knowyourbudget.feature.db

import com.glbgod.knowyourbudget.data.ExpenseItem
import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionItem

class BudgetRepository(
    private val expensesDao: ExpensesDao,
    private val transactionsDao: TransactionDao
) {

    fun getAllExpensesFlow() = expensesDao.getAllItemsFlow()
    fun getAllTransactionsFlow() = transactionsDao.getAllTransactionsFlow()
    suspend fun getAllTransactionsFromDate(dateTime: Long) =
        transactionsDao.getAllItemsFromDate(dateTime)

    suspend fun getAllTransactionsForExpense(expense: ExpenseItem) =
        transactionsDao.getAllTransactionsForExpense(expense.id)

    suspend fun insertTransaction(transactionModel: TransactionModel) {
        transactionsDao.insert(transactionModel)
    }

    suspend fun insertExpense(expenseModel: ExpenseModel) {
        expensesDao.insert(expenseModel)
    }

    suspend fun updateExpense(id: Int, newExpenseModel: ExpenseModel) {
        expensesDao.updateExpense(
            id = id,
            name = newExpenseModel.name,
            iconResId = newExpenseModel.iconResId,
            budgetPerRegularity = newExpenseModel.budgetPerRegularity
        )
    }

    suspend fun deleteExpense(expense: ExpenseItem) {
        expensesDao.deleteExpense(id = expense.id)
    }


    suspend fun deleteTransaction(transactionItem: TransactionItem) {
        transactionsDao.deleteById(id = transactionItem.transactionId)
    }

    suspend fun updateTransaction(transactionItem: TransactionItem, newValue: Int) {
        transactionsDao.updateTransactionValue(transactionItem.transactionId, newValue)
    }

    suspend fun clearAllExpenses() {
        expensesDao.clearAll()
    }

    suspend fun clearAllTransactions() {
        transactionsDao.clearAll()
    }

    suspend fun getAllExpenses(): List<ExpenseModel> {
        return expensesDao.getAllItems()
    }

    suspend fun getAllTransactions(): List<TransactionModel> {
        return transactionsDao.getAllItems()
    }
}
