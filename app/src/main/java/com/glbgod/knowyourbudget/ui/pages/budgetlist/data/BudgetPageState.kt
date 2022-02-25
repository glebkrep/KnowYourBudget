package com.glbgod.knowyourbudget.ui.pages.budgetlist.data

import com.glbgod.knowyourbudget.data.AddingExpenseEditData
import com.glbgod.knowyourbudget.data.ExpenseItem
import com.glbgod.knowyourbudget.data.ExpensesData
import com.glbgod.knowyourbudget.data.TotalBudgetData

sealed class BudgetPageState(val totalBudgetData: TotalBudgetData, val expensesData: ExpensesData) {
    data class NoDialogs(
        private val _totalBudgetData: TotalBudgetData,
        private val _expensesData: ExpensesData
    ) :
        BudgetPageState(_totalBudgetData, _expensesData)

    data class ExpenseEditDialog(
        val selectedExpense: ExpenseItem,
        val newExpenseData: AddingExpenseEditData,
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class DeleteExpenseConfirmationDialog(
        val selectedExpense: ExpenseItem,
        val newExpenseData: AddingExpenseEditData,
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class NewExpenseDialog(
        val newExpenseData: AddingExpenseEditData,
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class EditTotalBalanceDialog(
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class EditBudgetPlannedDialog(
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

    data class AddTransactionDialog(
        val expenseItem: ExpenseItem,
        private val _totalBudgetData: TotalBudgetData, private val _expensesData: ExpensesData
    ) : BudgetPageState(_totalBudgetData, _expensesData)

}