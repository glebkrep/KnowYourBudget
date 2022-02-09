package com.glbgod.knowyourbudget.ui.pages.budgetlist

import android.app.Application
import com.glbgod.knowyourbudget.core.utils.Debug
import com.glbgod.knowyourbudget.core.utils.PreferencesProvider
import com.glbgod.knowyourbudget.core.utils.Utils
import com.glbgod.knowyourbudget.core.utils.daysPassed
import com.glbgod.knowyourbudget.data.ExpenseCategoryData
import com.glbgod.knowyourbudget.data.ExpenseItem
import com.glbgod.knowyourbudget.data.ExpensesData
import com.glbgod.knowyourbudget.data.TotalBudgetData
import com.glbgod.knowyourbudget.feature.db.data.*
import com.glbgod.knowyourbudget.ui.AbstractPageAndroidVM
import com.glbgod.knowyourbudget.ui.BaseAction
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState

abstract class BudgetPageVMAbs(application: Application) :
    AbstractPageAndroidVM<BudgetPageEvent, BudgetPageState, BaseAction>(
        application,
        BaseAction.None
    ) {
    protected fun getTotalBudgetData(transactionModels: List<TransactionModel>): TotalBudgetData {
        val currentBalance = transactionModels.map { it.change.toLong() }.sum()
        val restartMoney = PreferencesProvider.getRestartMoney()
        return TotalBudgetData(currentBalance.toInt(), restartMoney)
    }

    protected fun getExpensesData(
        expenseModels: List<ExpenseWithTransactionsModel>,
    ): ExpensesData {
        Debug.log("getExpensesData (expenseModels: ${expenseModels.map { it.expense.name }})")
        val expenseItems = expenseModels.map { expenseWithTransactions ->
            expenseWithTransModelToExpenseItem(expenseWithTransactions)
        }
        Debug.log("getExpensesData (expenseItems: ${expenseItems.map { it.name }})")

        val groupedExpenseItems =
            expenseItems.groupBy { Pair(it.regularity, it.category) }
        Debug.log("getExpensesData (groupedExpenseItems: ${groupedExpenseItems.map { it.key.first.text.toString() + ":" + it.value.map { it.name } }})")

        val expensesData = ExpensesData(
            daily = groupedExpenseItems.filter {
                it.key.first == ExpenseRegularity.DAILY
            }.map {
                ExpenseCategoryData(
                    categoryName = it.key.second,
                    items = it.value,
                    regularity = ExpenseRegularity.DAILY
                )
            },
            weekly = groupedExpenseItems.filter {
                it.key.first == ExpenseRegularity.WEEKLY
            }.map {
                ExpenseCategoryData(
                    categoryName = it.key.second,
                    items = it.value,
                    regularity = ExpenseRegularity.WEEKLY
                )
            },
            //TODO: COUNT OTHER ITEM SEPARATLY
            monthly = groupedExpenseItems.filter {
                it.key.first == ExpenseRegularity.MONTHLY
            }.map {
                ExpenseCategoryData(
                    categoryName = it.key.second,
                    items = it.value,
                    regularity = ExpenseRegularity.MONTHLY
                )
            },
        )
        return expensesData
    }

    private fun expenseWithTransModelToExpenseItem(
        expenseWithTransactionsModel: ExpenseWithTransactionsModel
    ): ExpenseItem {
        val regularity =
            expenseRegularityByRegularityInt(expenseWithTransactionsModel.expense.regularity)

        val currentBalanceInTransactions =
            expenseWithTransactionsModel.transactions.map { it.change }.sum()
        val unlockedMoney = getMoneyUnlockedATM(expenseWithTransactionsModel.expense)
        val currentBalanceForPeriod = unlockedMoney + currentBalanceInTransactions

        val totalBalanceLeft =
            (regularity.refillsInMonth * expenseWithTransactionsModel.expense.budgetPerRegularity) + currentBalanceInTransactions

        var progressBarFloat =
            currentBalanceForPeriod.toFloat() / expenseWithTransactionsModel.expense.budgetPerRegularity.toFloat()
        if (progressBarFloat > 1f) {
            progressBarFloat = 1f
        }
        if (progressBarFloat < 0f) {
            progressBarFloat = 0f
        }

        val daysToNextRefill = when (regularity) {
            ExpenseRegularity.DAILY -> 1
            ExpenseRegularity.WEEKLY -> Utils.getDaysToWeekStart()
            ExpenseRegularity.MONTHLY -> Utils.getDaysToCycleRestart()
        }
        val newItem = ExpenseItem(
            id = expenseWithTransactionsModel.expense.expenseId,
            name = expenseWithTransactionsModel.expense.name,
            regularity = regularity,
            category = expenseWithTransactionsModel.expense.category,
            currentBalanceForPeriod = currentBalanceForPeriod.toInt(),
            totalBalanceForPeriod = expenseWithTransactionsModel.expense.budgetPerRegularity,
            totalBalanceLeft = totalBalanceLeft,
            iconResId = expenseWithTransactionsModel.expense.iconResId,
            progressFloat = progressBarFloat,
            nextRefillInDays = daysToNextRefill
        )
        return newItem
    }

    private fun getMoneyUnlockedATM(expenseModel: ExpenseModel): Long {
        val regularity = expenseRegularityByRegularityInt(expenseModel.regularity)
        val daysPassed =
            PreferencesProvider.getCycleStartTime().daysPassed(System.currentTimeMillis())
        val balancesUnlocked = when (regularity) {
            ExpenseRegularity.DAILY -> daysPassed + 1
            ExpenseRegularity.WEEKLY -> (daysPassed / 7) + 1
            else -> 1
        }
        val moneyUnlocked = balancesUnlocked * expenseModel.budgetPerRegularity
        return moneyUnlocked
    }


}