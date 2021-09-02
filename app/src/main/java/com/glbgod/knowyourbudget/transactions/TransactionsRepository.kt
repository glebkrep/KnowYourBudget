package com.glbgod.knowyourbudget.transactions

import com.glbgod.knowyourbudget.transactions.data.Transaction
import com.glbgod.knowyourbudget.transactions.data.TransactionDao
import com.glbgod.knowyourbudget.utils.Debug

class TransactionsRepository(private val transactionDao: TransactionDao) {

    fun getAllTransactions() = transactionDao.getAllItems()

    suspend fun deleteById(id: Int) {
        transactionDao.deleteById(id = id)
    }

    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }
}
