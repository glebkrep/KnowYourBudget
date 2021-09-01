package com.glbgod.knowyourbudget.viewmodel

import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.transactions.data.Transaction

data class AllBudgetData(
    val expenses: List<Expense>,
    val transactions: List<Transaction>
)