package com.glbgod.knowyourbudget.ui.pages.transactionsList

import android.app.Application
import com.glbgod.knowyourbudget.feature.db.data.ExpenseWithTransactionsModel
import com.glbgod.knowyourbudget.feature.db.data.expenseRegularityByRegularityInt
import com.glbgod.knowyourbudget.ui.AbstractPageAndroidVM
import com.glbgod.knowyourbudget.ui.BaseAction
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionItem
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageEvent
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageState

abstract class TransactionsPageVMAbs(application: Application) :
    AbstractPageAndroidVM<TransactionsPageEvent, TransactionsPageState, BaseAction>(
        application,
        BaseAction.None
    ) {
    protected fun workWithData(expensesWithTransactions: List<ExpenseWithTransactionsModel>) {
        val transactions =
            expensesWithTransactionsToListOfTransactionItems(expensesWithTransactions)
        postState(
            TransactionsPageState.DefaultState(
                transactionItems = transactions
            )
        )
    }

    private fun expensesWithTransactionsToListOfTransactionItems(expensesWithTransactions: List<ExpenseWithTransactionsModel>): List<TransactionItem> {
        val mutableListOfTransactionItems: MutableList<TransactionItem> = mutableListOf()
        for (expense in expensesWithTransactions) {
            for (transaction in expense.transactions) {
                mutableListOfTransactionItems.add(
                    TransactionItem(
                        transactionId = transaction.id,
                        regularity = expenseRegularityByRegularityInt(expense.expense.regularity),
                        iconResId = expense.expense.iconResId,
                        expenseName = expense.expense.name,
                        transactionComment = transaction.comment,
                        moneyChange = transaction.change,
                        time = transaction.time
                    )
                )
            }
        }
        return mutableListOfTransactionItems.toList().sortedByDescending { it.time }
    }
}
