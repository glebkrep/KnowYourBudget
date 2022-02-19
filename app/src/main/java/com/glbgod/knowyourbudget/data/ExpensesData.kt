package com.glbgod.knowyourbudget.data

data class ExpensesData(
    val daily: List<ExpenseCategoryData>,
    val weekly: List<ExpenseCategoryData>,
    val monthly: List<ExpenseCategoryData>
)
