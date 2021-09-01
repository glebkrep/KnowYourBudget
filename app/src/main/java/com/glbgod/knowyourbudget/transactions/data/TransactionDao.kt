package com.glbgod.knowyourbudget.transactions.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDao {
    @Query("select * from transaction_table order by time desc")
    suspend fun getAllItems(): List<Transaction>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction): Long

    @Query("delete from transaction_table where id=:id")
    suspend fun deleteById(id: Int)

    @Query("select * from transaction_table where time>:time")
    suspend fun getTransactionsAfter(time: Long): List<Transaction>
}
