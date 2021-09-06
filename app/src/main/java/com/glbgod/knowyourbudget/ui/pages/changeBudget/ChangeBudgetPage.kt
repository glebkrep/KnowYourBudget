package com.glbgod.knowyourbudget.ui.pages.changeBudget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.navigation.NavController
import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.expenses.data.ExpenseRegularity
import com.glbgod.knowyourbudget.ui.budgetlist.ExpenseItem
import com.glbgod.knowyourbudget.ui.theme.MyColors
import com.glbgod.knowyourbudget.ui.theme.UiConsts
import com.glbgod.knowyourbudget.viewmodel.ExpensesViewModel


@OptIn(ExperimentalUnitApi::class)
@Composable
fun ChangeBudgetPage(outterNavController: NavController, viewModel: ExpensesViewModel) {
    Column(Modifier.padding(UiConsts.padding)) {
        val expense by viewModel.currentExpense.observeAsState()
        var sum by remember {
            mutableStateOf(0)
        }

        var errorText by remember {
            mutableStateOf("")
        }

        Text(
            "Change Budget!",
            fontSize = TextUnit(20F, TextUnitType.Sp),
            modifier = Modifier.padding(top = UiConsts.padding, bottom = UiConsts.padding)
        )
        Divider(Modifier.padding(UiConsts.padding * 2))

        if (errorText != "") {
            Text(
                text = errorText,
                Modifier.padding(top = UiConsts.padding, bottom = UiConsts.padding),
                color = Color.Red
            )
        }

        if (expense == null) {
            errorText = "Loading..."
            return@Column
        } else {
            errorText = ""
        }

        val bgColor = when (expense!!.regularity) {
            ExpenseRegularity.DAILY.regularity -> MyColors.DailyColor
            ExpenseRegularity.WEEKLY.regularity -> MyColors.WeeklyColor
            else -> MyColors.MonthlyColor
        }
        ExpenseItem(
            expense = expense!!,
            onExpenseClick = {},
            isFromChangePage = true,
            bgColor = bgColor,
            onLongPress = {})


        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = UiConsts.padding, bottom = UiConsts.padding)
        ) {
            TextField(
                value = sum.toString(),
                onValueChange = {
                    try {
                        sum = it.toInt()
                        errorText = ""
                    } catch (e: Exception) {
                        errorText = "sum has to be a value"
                    }
                },
                label = { Text("New budget") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Button(
            modifier = Modifier.padding(top = UiConsts.padding, bottom = UiConsts.padding),
            onClick = {
                onTransactionClick(
                    sum,
                    expense,
                    viewModel,
                    error = {
                        errorText = it
                    },
                    goBack = {
                        outterNavController.popBackStack()
                    })
            }) {
            Text(text = "OK")
        }
    }
}

private fun onTransactionClick(
    sum: Int,
    expense: Expense?,
    viewModel: ExpensesViewModel,
    error: (String) -> (Unit),
    goBack: () -> (Unit)
) {
//    if (sum == 0 || sum < 0) {
//        error.invoke("sum should be positive value")
//        return
//    }
    if (expense == null) {
        error.invoke("Select expense!")
        return
    }
    viewModel.setBudget(expense, sum)
    goBack()
}
