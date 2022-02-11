package com.glbgod.knowyourbudget.feature.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class TransactionModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "parent_expense_id")
    val parentExpenseId: Int,
    @ColumnInfo(name = "comment")
    val comment: String = "",
    @ColumnInfo(name = "change")
    val change: Int,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "trans_category")
    val transactionCategory: Int
)
