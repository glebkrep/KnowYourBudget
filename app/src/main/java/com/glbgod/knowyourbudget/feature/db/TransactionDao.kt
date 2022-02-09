package com.glbgod.knowyourbudget.feature.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("select * from TransactionModel order by time desc")
    fun getAllItems(): Flow<List<TransactionModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: TransactionModel): Long

    @Query("delete from TransactionModel where id=:id")
    suspend fun deleteById(id: Int)

}
