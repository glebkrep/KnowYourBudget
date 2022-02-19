package com.glbgod.knowyourbudget.ui.pages.transactionsList.data

import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity

data class TransactionItem(
    val transactionId: Int,
    val regularity: ExpenseRegularity,
    val iconResId: Int,
    val expenseName: String,
    val transactionComment: String,
    val moneyChange: Int,
    val time: Long
)