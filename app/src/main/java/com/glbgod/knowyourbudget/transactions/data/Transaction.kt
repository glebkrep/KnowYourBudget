package com.glbgod.knowyourbudget.transactions.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "expense_id")
    val expenseId: Int,
    @ColumnInfo(name = "expense_name")
    val expenseName: String,
    @ColumnInfo(name = "change")
    val change: Int,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "trans_category")
    val transactionCategory: Int
)
