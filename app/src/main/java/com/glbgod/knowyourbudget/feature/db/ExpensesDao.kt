package com.glbgod.knowyourbudget.feature.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.ExpenseWithTransactionsModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpensesDao {
    @Query("select * from ExpenseModel")
    fun getAllItemsFlow(): Flow<List<ExpenseWithTransactionsModel>>

    @Query("select * from ExpenseModel order by regularity,category,name")
    suspend fun getAllItems(): List<ExpenseWithTransactionsModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expenseModel: ExpenseModel): Long

}
