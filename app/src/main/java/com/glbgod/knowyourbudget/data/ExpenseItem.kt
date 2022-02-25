package com.glbgod.knowyourbudget.data

import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity

data class ExpenseItem(
    val id: Int,
    val name: String,
    val regularity: ExpenseRegularity,
    val category: String,
    val currentBalanceForPeriod: Int,
    val balancePlannedForPeriod: Int,
    val totalBalanceLeft: Int,
    val iconResId: Int,
    val nextRefillInDays: Int,
    val progressFloat: Float
)