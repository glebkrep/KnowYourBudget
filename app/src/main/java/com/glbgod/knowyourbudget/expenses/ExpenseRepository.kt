package com.glbgod.knowyourbudget.expenses

import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.expenses.data.ExpensesDao

class ExpenseRepository(private val expensesDao: ExpensesDao) {

    suspend fun getAllExpenses() = expensesDao.getAllItems()

    suspend fun updateBalance(id: Int, updatedBalance: Int) {
        expensesDao.updateCurrentBalance(id, updatedBalance)
    }

    suspend fun getExpenseById(id: Int) = expensesDao.getExpenseById(id)

    suspend fun clearAll() {
        expensesDao.clearAll()
    }

    suspend fun insert(expense: Expense) {
        expensesDao.insert(expense)
    }

    suspend fun updateBudget(expenseId: Int, newBudget: Int) {
        expensesDao.updateBudget(expenseId, newBudget)
    }

}
