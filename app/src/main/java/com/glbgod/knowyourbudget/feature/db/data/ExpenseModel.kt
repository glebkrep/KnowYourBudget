package com.glbgod.knowyourbudget.feature.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class ExpenseModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var expenseId: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "icon_res_id")
    val iconResId: Int,
    @ColumnInfo(name = "regularity")
    val regularity: Int,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "budget")
    val budgetPerRegularity: Int,
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false
)
