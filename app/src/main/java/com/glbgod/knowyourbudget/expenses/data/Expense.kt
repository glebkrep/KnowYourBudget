package com.glbgod.knowyourbudget.expenses.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "regularity")
    val regularity: Int,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "balance")
    val currentBalance: Int,
    @ColumnInfo(name = "budget")
    val budget: Int
)
