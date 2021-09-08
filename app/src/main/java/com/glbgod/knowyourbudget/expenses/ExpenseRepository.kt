package com.glbgod.knowyourbudget.expenses

import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.expenses.data.ExpenseRegularity
import com.glbgod.knowyourbudget.expenses.data.ExpensesDao

class ExpenseRepository(private val expensesDao: ExpensesDao) {

    fun getAllExpenses() = expensesDao.getAllItems()

    suspend fun updateBalance(id: Int, balanceUpdate: Int) {
        expensesDao.updateCurrentBalance(id, balanceUpdate)
    }

    suspend fun insert(expense: Expense) {
        expensesDao.insert(expense)
    }

    suspend fun updateBudget(expenseId: Int, newBudget: Int) {
        expensesDao.updateBudget(expenseId, newBudget)
    }

    suspend fun updateAllMonthlyBalances() {
        expensesDao.updateRegularBalances(budgetsAdded = 1,regularity = ExpenseRegularity.MONTHLY.regularity)
    }

    suspend fun updateRegularBalances(regularity:Int,budgetsAdded:Int) {
        expensesDao.updateRegularBalances(budgetsAdded = budgetsAdded,regularity = regularity)
    }

}
