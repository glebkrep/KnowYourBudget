package com.glbgod.knowyourbudget.ui.pages.budgetlist.data

import com.glbgod.knowyourbudget.data.ExpenseEditData
import com.glbgod.knowyourbudget.data.ExpensesData
import com.glbgod.knowyourbudget.data.TotalBudgetData
import com.glbgod.knowyourbudget.data.TransactionEditData

sealed class BudgetPageState(val totalBudgetData: TotalBudgetData, val expensesData: ExpensesData) {
    data class NoDialogs(val _totalBudgetData: TotalBudgetData, val _expensesData: ExpensesData) :
        BudgetPageState(_totalBudgetData, _expensesData)

    data class ExpenseEditDialog(
        val newExpenseData: ExpenseEditData,
        val _totalBudgetData: TotalBudgetData, val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class DeleteExpenseConfirmationDialog(
        val expenseData: ExpenseEditData,
        val _totalBudgetData: TotalBudgetData, val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class NewExpenseEditDialog(
        val newExpenseData: ExpenseEditData,
        val _totalBudgetData: TotalBudgetData, val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class EditTotalBalanceDialog(
        val newTotalBalance: Int,
        val _totalBudgetData: TotalBudgetData, val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class AddTransactionDialog(
        val transactionEditData: TransactionEditData,
        val newExpenseData: ExpenseEditData,
        val _totalBudgetData: TotalBudgetData, val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

}