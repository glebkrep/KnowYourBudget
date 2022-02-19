package com.glbgod.knowyourbudget.feature.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel
import kotlinx.coroutines.CoroutineScope

@Database(entities = [ExpenseModel::class, TransactionModel::class], version = 2)
abstract class BudgetRoomDB : RoomDatabase() {

    abstract fun expensesDao(): ExpensesDao
    abstract fun transactionsDao(): TransactionDao


    companion object {
        @Volatile
        private var INSTANCE: BudgetRoomDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BudgetRoomDB {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            } else {
                //todo add migrations
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        BudgetRoomDB::class.java, "local_db"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                    return instance
                }

            }
        }
    }
}
