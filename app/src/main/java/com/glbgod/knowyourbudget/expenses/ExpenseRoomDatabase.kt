package com.glbgod.knowyourbudget.expenses

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.expenses.data.ExpensesDao
import com.glbgod.knowyourbudget.transactions.data.Transaction
import com.glbgod.knowyourbudget.transactions.data.TransactionDao
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Expense::class, Transaction::class], version = 2)
abstract class ExpenseRoomDatabase : RoomDatabase() {

    abstract fun expensesDao(): ExpensesDao
    abstract fun transactionsDao(): TransactionDao


    companion object {
        @Volatile
        private var INSTANCE: ExpenseRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ExpenseRoomDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            } else {
                //todo add migrations
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ExpenseRoomDatabase::class.java, "local_db"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                    return instance
                }

            }
        }
    }
}
