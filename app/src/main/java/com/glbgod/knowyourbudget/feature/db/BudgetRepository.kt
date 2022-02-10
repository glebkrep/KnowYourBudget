package com.glbgod.knowyourbudget.feature.db

import com.glbgod.knowyourbudget.data.ExpenseItem
import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel

class BudgetRepository(
    private val expensesDao: ExpensesDao,
    private val transactionsDao: TransactionDao
) {

    fun getAllExpensesFlow() = expensesDao.getAllItemsFlow()

    suspend fun getAllTransactionsFromDate(dateTime:Long) = transactionsDao.getAllItemsFromDate(dateTime)

    suspend fun insertTransaction(transactionModel: TransactionModel) {
        transactionsDao.insert(transactionModel)
    }

    suspend fun insertExpense(expenseModel: ExpenseModel) {
        expensesDao.insert(expenseModel)
    }

    suspend fun updateExpense(id:Int, newExpenseModel:ExpenseModel){
        expensesDao.updateExpense(
            id = id,
            name = newExpenseModel.name,
            iconResId = newExpenseModel.iconResId,
            budgetPerRegularity = newExpenseModel.budgetPerRegularity
        )
    }

    suspend fun fakeDeleteExpense(expense:ExpenseItem){
        expensesDao.fakeDeleteExpense(id=expense.id)
    }

}
