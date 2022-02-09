package com.glbgod.knowyourbudget.feature.db.data

import androidx.room.Embedded
import androidx.room.Relation

data class ExpenseWithTransactionsModel(
    @Embedded
    val expense: ExpenseModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent_expense_id"
    )
    val transactions: List<TransactionModel>

)