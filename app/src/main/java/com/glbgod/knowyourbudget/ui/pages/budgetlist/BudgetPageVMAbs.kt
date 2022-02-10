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
    protected fun recalculateData(
        expenseModelsRaw: List<ExpenseWithTransactionsModel>,
    ) {
        expenseModelsRaw.forEach { expenseWithTransactionsModelRaw ->
            expenseWithTransactionsModelRaw.transactions =
                expenseWithTransactionsModelRaw.transactions.filter { it.time >= PreferencesProvider.getCycleStartTime() }
        }
        val expenseModels = expenseModelsRaw
        //todo transactions should be filtered by this time
        val transactions = expenseModels.map { it.transactions }.flatten()
        val totalBudgetData = getTotalBudgetData(transactions)
        val expensesData = getExpensesData(expenseModels)

        if ((expensesData.daily + expensesData.monthly + expensesData.weekly).isNotEmpty()) {
            postState(BudgetPageState.NoDialogs(totalBudgetData, expensesData))
        }
    }

    private fun getTotalBudgetData(transactionModels: List<TransactionModel>): TotalBudgetData {
        val currentBalance = transactionModels.map { it.change.toLong() }.sum()
        val restartMoney = PreferencesProvider.getRestartMoney()
        return TotalBudgetData(currentBalance.toInt(), restartMoney)
    }

    private fun getExpensesData(
        expenseModels: List<ExpenseWithTransactionsModel>,
    ): ExpensesData {
        Debug.log("getExpensesData (expenseModels: ${expenseModels.map { it.expense.name }})")
        val expenseItems = expenseModels.map { expenseWithTransactions ->
            expenseWithTransModelToExpenseItem(
                expenseModels.map {
                    it.expense.budgetPerRegularity * expenseRegularityByRegularityInt(
                        it.expense.regularity
                    ).refillsInMonth
                }.sum(),
                expenseWithTransactionsModel = expenseWithTransactions
            )
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
        moneyOccupiedByAllExpenses: Int,
        expenseWithTransactionsModel: ExpenseWithTransactionsModel
    ): ExpenseItem {
        val regularity =
            expenseRegularityByRegularityInt(expenseWithTransactionsModel.expense.regularity)

        val currentBalanceInTransactions =
            expenseWithTransactionsModel.transactions.map { it.change }.sum()
        val unlockedMoney = getMoneyUnlockedATM(expenseWithTransactionsModel.expense)
        val currentBalanceForPeriod =
            if (expenseWithTransactionsModel.expense.expenseId == 1) {
                (currentBalanceInTransactions - PreferencesProvider.getMonthStartBalance()) + (PreferencesProvider.getMonthStartBalance() - moneyOccupiedByAllExpenses)
            } else {
                unlockedMoney + currentBalanceInTransactions
            }

        val totalBalanceLeft =
            if (expenseWithTransactionsModel.expense.expenseId == 1) {
                currentBalanceForPeriod.toInt()
            } else {
                (regularity.refillsInMonth * expenseWithTransactionsModel.expense.budgetPerRegularity) + currentBalanceInTransactions
            }

        var progressBarFloat =
            currentBalanceForPeriod.toFloat() /
                    (if (expenseWithTransactionsModel.expense.expenseId == 1) {
                        PreferencesProvider.getMonthStartBalance() - moneyOccupiedByAllExpenses
                    } else {
                        expenseWithTransactionsModel.expense.budgetPerRegularity.toFloat()
                    }).toFloat()
        if (progressBarFloat > 1f) {
            progressBarFloat = 1f
        }
        if (progressBarFloat < 0f) {
            progressBarFloat = 0f
        }

        val daysToNextRefill = when (regularity) {
            ExpenseRegularity.DAILY -> 1
            ExpenseRegularity.WEEKLY -> Utils.getDaysToWeekStart()
            ExpenseRegularity.MONTHLY -> Utils.getDaysToCycleRestartTime()
        }
        val newItem = ExpenseItem(
            id = expenseWithTransactionsModel.expense.expenseId,
            name = expenseWithTransactionsModel.expense.name,
            regularity = regularity,
            category = expenseWithTransactionsModel.expense.category,
            currentBalanceForPeriod = currentBalanceForPeriod.toInt(),
            totalBalanceForPeriod =
            if (expenseWithTransactionsModel.expense.expenseId != 1) {
                expenseWithTransactionsModel.expense.budgetPerRegularity
            } else {
                PreferencesProvider.getMonthStartBalance() - moneyOccupiedByAllExpenses
            },
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