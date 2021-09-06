package com.glbgod.knowyourbudget.ui.pages.budgetlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.expenses.data.ExpenseRegularity
import com.glbgod.knowyourbudget.ui.theme.MyColors
import com.glbgod.knowyourbudget.ui.theme.RegularityStyle
import com.glbgod.knowyourbudget.ui.theme.UiConsts
import com.glbgod.knowyourbudget.utils.Utils
import com.glbgod.knowyourbudget.utils.toBeautifulString
import com.glbgod.knowyourbudget.viewmodel.ExpensesViewModel

@Composable
fun BudgetPage(
    viewModel: ExpensesViewModel,
    onExpenseClick: (Expense) -> Unit,
    onLongPress: (Expense) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(Modifier.verticalScroll(scrollState)) {
        val allExpenses by viewModel.allExpenses.observeAsState(listOf())
        val currentBalance by viewModel.currentBalance.observeAsState()

        Column(Modifier.padding(UiConsts.padding).fillMaxWidth()) {
            Text(
                text = "${currentBalance?.currentMoney?.toBeautifulString()} out of ${currentBalance?.stableIncome?.toBeautifulString()}",
                modifier = Modifier.padding(UiConsts.padding)
            )
            LinearProgressIndicator(progress = (currentBalance?.currentMoney?.toFloat()?:1f)/(currentBalance?.stableIncome?.toFloat()?:1f), Modifier.fillMaxWidth())
        }

        // todo: move this calculation to viewModel and get categorised data from there
        val dailyItems = allExpenses.filter { it.regularity == ExpenseRegularity.DAILY.regularity }
        val weeklyItems = allExpenses.filter { it.regularity == ExpenseRegularity.WEEKLY.regularity }
        val monthlyItems =
            allExpenses.filter { it.regularity == ExpenseRegularity.MONTHLY.regularity }
        //

        //daily
        ExpenseInOneRegularity(
            expenses = dailyItems,
            regularityStyle = RegularityStyle(MyColors.DailyColor, Color.Black),
            onExpenseClick = { onExpenseClick.invoke(it) },
            onLongPress = { onLongPress.invoke(it) })
        //weekly
        ExpenseInOneRegularity(
            expenses = weeklyItems,
            regularityStyle = RegularityStyle(MyColors.WeeklyColor, Color.Black),
            onExpenseClick = { onExpenseClick.invoke(it) },
            onLongPress = { onLongPress.invoke(it) })
        //monthly
        ExpenseInOneRegularity(
            expenses = monthlyItems,
            regularityStyle = RegularityStyle(MyColors.MonthlyColor, Color.Black),
            onExpenseClick = { onExpenseClick.invoke(it) },
            onLongPress = { onLongPress.invoke(it) })
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun ExpenseInOneRegularity(
    expenses: List<Expense>,
    regularityStyle: RegularityStyle,
    onExpenseClick: (Expense) -> Unit,
    onLongPress: (Expense) -> Unit
) {
    Column(
        Modifier
            .padding(UiConsts.padding)
            .fillMaxWidth()
            .background(regularityStyle.backgroundColor)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                when (expenses.firstOrNull()?.regularity) {
                    ExpenseRegularity.DAILY.regularity -> "Daily"
                    ExpenseRegularity.WEEKLY.regularity -> "Weekly"
                    ExpenseRegularity.MONTHLY.regularity -> "Monthly"
                    else -> ""
                },
                Modifier
                    .padding(UiConsts.padding),
                fontSize = TextUnit(20F, TextUnitType.Sp)
            )
            if (expenses.firstOrNull()?.regularity == ExpenseRegularity.WEEKLY.regularity) {
                Text(
                    text = "Next refill in ${Utils.getDaysToWeekStart()} days",
                    Modifier.padding(UiConsts.padding)
                )
            }
            if (expenses.firstOrNull()?.regularity == ExpenseRegularity.MONTHLY.regularity) {
                Text(
                    text = "Next paycheck expected in ${Utils.getCycleRestartTime()} days",
                    Modifier.padding(UiConsts.padding)
                )
            }
        }

        val categories: MutableMap<String, MutableList<Expense>> = mutableMapOf()
        for (expense in expenses) {
            categories[expense.category]?.add(expense) ?: categories.putIfAbsent(
                expense.category,
                mutableListOf(expense)
            )
        }
        val colors = Utils.generateColors(categories.keys.toList(), regularityStyle.backgroundColor)
        for (categoryName in categories.keys) {
            val bgColor = colors[categoryName] ?: regularityStyle.backgroundColor
            CategoryItem(
                expenses = categories.get(categoryName) ?: listOf(),
                color = bgColor,
                onExpenseClick = {
                    onExpenseClick.invoke(it)
                },
                onLongPress = {
                    onLongPress.invoke(it)
                })
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun CategoryItem(
    expenses: List<Expense>,
    color: Color,
    onExpenseClick: (Expense) -> Unit,
    onLongPress: (Expense) -> Unit
) {
    Column(
        Modifier
            .background(color)
            .padding(UiConsts.padding)
            .padding(start = UiConsts.padding, end = UiConsts.padding)
            .wrapContentSize()
    ) {
        Text(
            text = expenses.firstOrNull()?.category ?: "",
            Modifier
                .fillMaxWidth(), fontSize = TextUnit(18F, TextUnitType.Sp)
        )
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            for (item in expenses) {
                ExpenseItem(
                    expense = item,
                    isFromChangePage = false,
                    bgColor = Color(red = 0f, green = 0f, blue = 0f, alpha = 0.1f),
                    onExpenseClick = {
                        onExpenseClick.invoke(it)
                    },
                    onLongPress = {
                        onLongPress.invoke(it)
                    })
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ExpenseItem(
    expense: Expense,
    isFromChangePage: Boolean,
    bgColor: Color,
    onExpenseClick: (Expense) -> (Unit),
    onLongPress: (Expense) -> (Unit)
) {

    val modifier =
        if (!isFromChangePage) {
            Modifier
                .padding(start = UiConsts.padding, end = UiConsts.padding, top = UiConsts.padding)
                .background(bgColor)
                .border(1.dp, Color.Gray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            onLongPress.invoke(expense)
                        },
                        onTap = {
                            onExpenseClick.invoke(expense)
                        }
                    )
                }
        } else {
            Modifier
                .padding(start = UiConsts.padding, end = UiConsts.padding, top = UiConsts.padding)
                .background(bgColor)
                .border(1.dp, Color.Gray)

        }
    Column(modifier) {
        Column(Modifier.padding(UiConsts.padding)) {
            Text(expense.name)

            val start = expense.currentBalance
            val end = expense.budget
            val progress = if (start > end || end == 0) 1f
            else {
                start.toFloat() / end.toFloat()
            }
            Row(
                modifier = if (isFromChangePage) Modifier.fillMaxWidth() else Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = expense.currentBalance.toBeautifulString(),
                    Modifier,
                    fontSize = TextUnit(14F, TextUnitType.Sp)
                )
                Text(text = expense.budget.toBeautifulString(), fontSize = TextUnit(14F, TextUnitType.Sp))
            }
            LinearProgressIndicator(progress = progress, Modifier.fillMaxWidth())
        }
    }
}