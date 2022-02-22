package com.glbgod.knowyourbudget.feature.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("select * from TransactionModel where time>=:time order by time desc")
    suspend fun getAllItemsFromDate(time: Long): List<TransactionModel>

    @Query("select * from TransactionModel where parent_expense_id=:expenseId order by time desc")
    suspend fun getAllTransactionsForExpense(expenseId: Int): List<TransactionModel>

    @Query("update TransactionModel set change=:newValue where id=:transactionId")
    suspend fun updateTransactionValue(transactionId: Int, newValue: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: TransactionModel): Long

    @Query("delete from TransactionModel where id=:id")
    suspend fun deleteById(id: Int)

    @Query("select * from TransactionModel order by time desc")
    fun getAllTransactionsFlow(): Flow<List<TransactionModel>>

    @Query("delete from TransactionModel")
    suspend fun clearAll()

    @Query("select * from TransactionModel")
    suspend fun getAllItems(): List<TransactionModel>

}
