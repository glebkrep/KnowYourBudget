package com.glbgod.knowyourbudget.ui.pages.budgetlist.data

import com.glbgod.knowyourbudget.data.ExpenseItem
import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel

sealed class BudgetPageEvent {
    object SettingsClicked : BudgetPageEvent()

    data class OnExpenseOpenCloseClicked(val regularity: ExpenseRegularity) : BudgetPageEvent()

    data class AddExpenseClicked(val regularity: ExpenseRegularity) : BudgetPageEvent()
    data class EditExpenseClicked(val expenseModel: ExpenseItem) : BudgetPageEvent()
    data class EditExpenseFinished(val newExpenseModel: ExpenseModel) : BudgetPageEvent()
    object DialogDismissed : BudgetPageEvent()

    data class AddTransactionToExpenseClicked(val expenseItem: ExpenseItem) : BudgetPageEvent()
    data class AddTransactionToExpenseFinished(val newTransactionModel: TransactionModel) :
        BudgetPageEvent()

    data class DeleteExpenseClicked(val expenseModel: ExpenseModel) : BudgetPageEvent()
    data class DeleteExpenseSuccess(val expenseModel: ExpenseModel) : BudgetPageEvent()


    object EditTotalBalanceClicked : BudgetPageEvent()
    data class EditTotalBalanceFinished(val balanceAdded: Int) : BudgetPageEvent()

}