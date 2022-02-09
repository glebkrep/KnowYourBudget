package com.glbgod.knowyourbudget.data

import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity

data class ExpenseEditData(
    val regularity: ExpenseRegularity,
    val iconsIdList: List<Int>,
    val categoryList: List<String>,
    val name: String,
    val budgetPerRegularity: Int,
    val freeToUseFunds: Int
)
