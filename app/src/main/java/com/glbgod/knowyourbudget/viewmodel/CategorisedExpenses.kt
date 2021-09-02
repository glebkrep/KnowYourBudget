package com.glbgod.knowyourbudget.viewmodel

import com.glbgod.knowyourbudget.expenses.data.Expense

data class CategorisedExpenses(
    val monthly:List<Expense>,
    val weekly:List<Expense>,
    val daily:List<Expense>
)