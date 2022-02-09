package com.glbgod.knowyourbudget.data

import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity

data class ExpenseCategoryData(
    val categoryName: String,
    val regularity: ExpenseRegularity,
    val items: List<ExpenseItem>,
)