package com.glbgod.knowyourbudget.expenses.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpensesDao {
    @Query("select * from expense_table order by regularity,category,name")
    fun getAllItems(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expense: Expense): Long

    @Query("update expense_table set balance=balance+:balanceUpdate where id=:id")
    suspend fun updateCurrentBalance(
        id: Int,
        balanceUpdate: Int
    )

    @Query("delete from expense_table")
    suspend fun clearAll()

    @Query("update expense_table set budget=:newBudget where id=:expenseId")
    suspend fun updateBudget(expenseId: Int, newBudget: Int)

    @Query("select * from expense_table where id=:id limit 2")
    fun getExpenseById(id: Int): Flow<List<Expense>>

    @Query("update expense_table set balance=balance+budget")
    suspend fun updateAllBalances()

    @Query("update expense_table set balance=balance+(budget*:budgetsAdded) where regularity=:regularity")
    suspend fun updateRegularBalances(budgetsAdded:Int,regularity:Int)
}
