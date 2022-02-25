package com.glbgod.knowyourbudget.feature.db

import androidx.room.*
import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.ExpenseWithTransactionsModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpensesDao {
    @Transaction
    @Query("select * from ExpenseModel")
    fun getAllItemsFlow(): Flow<List<ExpenseWithTransactionsModel>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expenseModel: ExpenseModel): Long

    @Query("UPDATE ExpenseModel SET name=:name, icon_res_id=:iconResId, budget=:budgetPerRegularity where id=:id")
    suspend fun updateExpense(id: Int, name: String, iconResId: Int, budgetPerRegularity: Int)

    @Query("DELETE from ExpenseModel WHERE id=:id")
    suspend fun deleteExpense(id: Int)

    @Query("DELETE from ExpenseModel")
    fun clearAll()

    @Query("select * from ExpenseModel")
    suspend fun getAllItems(): List<ExpenseModel>

    @Query("update ExpenseModel set name=:newName where id=:id")
    suspend fun fakeUpdate(id:Int,newName:String)
}
