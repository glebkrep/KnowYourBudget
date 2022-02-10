package com.glbgod.knowyourbudget.ui.pages.budgetlist.data

import com.glbgod.knowyourbudget.data.*

sealed class BudgetPageState(val totalBudgetData: TotalBudgetData, val expensesData: ExpensesData) {
    data class NoDialogs(private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData) :
        BudgetPageState(_totalBudgetData, _expensesData)

    data class ExpenseEditDialog(
        val newExpenseData: AddingExpenseEditData,
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class DeleteExpenseConfirmationDialog(
        val expenseData: AddingExpenseEditData,
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class NewExpenseDialog(
        val newExpenseData: AddingExpenseEditData,
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class EditTotalBalanceDialog(
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class AddTransactionDialog(
        val expenseItem: ExpenseItem,
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

}