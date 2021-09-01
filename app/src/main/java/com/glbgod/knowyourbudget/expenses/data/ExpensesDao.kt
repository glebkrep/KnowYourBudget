package com.glbgod.knowyourbudget.expenses.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpensesDao {
    @Query("select * from expense_table order by regularity,category,name")
    suspend fun getAllItems(): List<Expense>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expense: Expense): Long

    @Query("update expense_table set balance=:currentBalance where id=:id")
    suspend fun updateCurrentBalance(
        id: Int,
        currentBalance: Int
    )

    @Query("delete from expense_table")
    suspend fun clearAll()

    @Query("update expense_table set budget=:newBudget where id=:expenseId")
    suspend fun updateBudget(expenseId: Int, newBudget: Int)

    @Query("select * from expense_table where id=:id limit 2")
    suspend fun getExpenseById(id: Int): List<Expense>
}
