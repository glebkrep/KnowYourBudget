package com.glbgod.knowyourbudget.ui.pages.budgetlist.data

import com.glbgod.knowyourbudget.data.ExpenseItem
import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity

sealed class BudgetPageEvent {
    object SettingsClicked : BudgetPageEvent()

    data class AddExpenseClicked(val regularity: ExpenseRegularity) : BudgetPageEvent()
    data class EditExpenseClicked(val expenseItem: ExpenseItem) : BudgetPageEvent()
    data class EditExpenseFinished(val newExpenseModel: ExpenseModel) : BudgetPageEvent()
    object DialogDismissed : BudgetPageEvent()

    data class AddTransactionToExpenseClicked(val expenseItem: ExpenseItem) : BudgetPageEvent()
    data class AddTransactionToExpenseFinished(
        val expenseItem: ExpenseItem,
        val sum: Int,
        val comment: String
    ) :
        BudgetPageEvent()

    data class DeleteExpenseClicked(val expenseItem: ExpenseItem) : BudgetPageEvent()
    data class DeleteExpenseFinished(val expenseItem: ExpenseItem, val isSuccess: Boolean) :
        BudgetPageEvent()


    object EditTotalBalanceClicked : BudgetPageEvent()
    data class EditTotalBalanceFinished(val balanceAdded: Int, val isRestart: Boolean,val incomeTime:Long) :
        BudgetPageEvent()

}