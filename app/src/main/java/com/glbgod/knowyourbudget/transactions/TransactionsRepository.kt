package com.glbgod.knowyourbudget.transactions

import com.glbgod.knowyourbudget.transactions.data.Transaction
import com.glbgod.knowyourbudget.transactions.data.TransactionDao

class TransactionsRepository(private val transactionDao: TransactionDao) {

    suspend fun getAllTransactions() = transactionDao.getAllItems()

    suspend fun deleteById(id: Int) {
        transactionDao.deleteById(id = id)
    }

    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun getTransactionsAfter(long: Long) = transactionDao.getTransactionsAfter(long)


}
