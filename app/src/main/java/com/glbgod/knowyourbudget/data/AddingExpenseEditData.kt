package com.glbgod.knowyourbudget.data

import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity

data class AddingExpenseEditData(
    val regularity: ExpenseRegularity,
    val iconsIdList: List<Int>,
    val categoryList: List<String>,
    val freeToUseFunds: Int
)
