package com.glbgod.knowyourbudget.ui.pages.addTransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.glbgod.knowyourbudget.ui.custom.DatePickerView
import com.glbgod.knowyourbudget.ui.pages.budgetlist.ExpenseItem
import com.glbgod.knowyourbudget.ui.theme.MyColors
import com.glbgod.knowyourbudget.ui.theme.UiConsts
import com.glbgod.knowyourbudget.viewmodel.ExpensesViewModel

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ChangePage(outterNavController: NavController, viewModel: ExpensesViewModel) {
    Column(Modifier.padding(UiConsts.padding)) {
        val expense by viewModel.currentExpense.observeAsState(null)
        val allExpenses by viewModel.allExpenses.observeAsState(listOf())
        var expanded by remember { mutableStateOf(false) }
        var checked by remember { mutableStateOf(false) }
        var selectedExpense by remember { mutableStateOf(expense) }
        if (selectedExpense == null) {
            selectedExpense = expense
        }
        var isIncrease by remember { mutableStateOf(false) }
        var sum by remember { mutableStateOf(0) }
        var isRestartBudget by remember { mutableStateOf(false) }
        var errorText by remember { mutableStateOf("") }
        var datePicked : Long by remember {
            mutableStateOf(System.currentTimeMillis())
        }

        // Header
        Text(
            "New Tansaction!",
            fontSize = TextUnit(20F, TextUnitType.Sp),
            modifier = Modifier.padding(top = UiConsts.padding, bottom = UiConsts.padding)
        )
        Divider(Modifier.padding(UiConsts.padding * 2))
        //

        // Error text
        if (errorText != "") {
            Text(
                text = errorText,
                Modifier.padding(top = UiConsts.padding, bottom = UiConsts.padding),
                color = androidx.compose.ui.graphics.Color.Red
            )
        }
        //

        // Expense selector
        if (!isIncrease) {
            Button(
                onClick = { expanded = !expanded },
                Modifier.padding(top = UiConsts.padding, bottom = UiConsts.padding)
            ) {
                Text(text = "${selectedExpense?.name ?: ""} [${selectedExpense?.category ?: ""}]")
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allExpenses.forEach { expense ->
                DropdownMenuItem(onClick = {
                    selectedExpense = expense
                    expanded = !expanded
                    errorText = ""
                }) {
                    val bgColor = when (expense.regularity) {
                        ExpenseRegularity.DAILY.regularity -> MyColors.DailyColor
                        ExpenseRegularity.WEEKLY.regularity -> MyColors.WeeklyColor
                        else -> MyColors.MonthlyColor
                    }
                    ExpenseItem(
                        expense = expense,
                        onExpenseClick = {},
                        isFromChangePage = true,
                        bgColor = bgColor,
                        onLongPress = {})
                }
            }
        }
        //

        // Currently selected expense
        if (selectedExpense != null && !isIncrease) {
            val bgColor = when (selectedExpense!!.regularity) {
                ExpenseRegularity.DAILY.regularity -> MyColors.DailyColor
                ExpenseRegularity.WEEKLY.regularity -> MyColors.WeeklyColor
                else -> MyColors.MonthlyColor
            }
            ExpenseItem(
                expense = selectedExpense!!,
                onExpenseClick = {},
                isFromChangePage = true,
                bgColor = bgColor,
                onLongPress = {})
        }
        //


        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = UiConsts.padding, bottom = UiConsts.padding)
        ) {
            Text(
                text = "is increase?",
                Modifier.padding(top = UiConsts.padding, bottom = UiConsts.padding)
            )
            Switch(checked = isIncrease, onCheckedChange = { isIncrease = !isIncrease })
        }
        if (!isRestartBudget){
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
                    label = { Text("Sum") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
        if (isIncrease) {
            Row(Modifier.padding(top = UiConsts.padding, bottom = UiConsts.padding)) {
                Text(text = "restart budget? ")
                Checkbox(checked = checked, onCheckedChange = {
                    isRestartBudget = it
                    checked = !checked
                })
            }
        }
        if (isIncrease&&isRestartBudget){
            Row() {
                DatePickerView(currentDate = datePicked,updatedDate = {timePicked->
                    timePicked?.let {
                        datePicked = it
                    }
                })
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier.padding(top = UiConsts.padding, bottom = UiConsts.padding),
                onClick = {
                    onTransactionClick(
                        sum,
                        selectedExpense,
                        isIncrease,
                        isRestart = isRestartBudget,
                        viewModel = viewModel,
                        datePicked = datePicked,
                        error = {
                            errorText = it
                        },
                        goBack = {
                            outterNavController.popBackStack()
                        })
                }) {
                Text(text = "Save Transaction")
            }

            if (!isIncrease) {
                Button(modifier = Modifier.padding(
                    top = UiConsts.padding,
                    bottom = UiConsts.padding
                ),
                    onClick = {
                        onSendToOtherClick(sum, selectedExpense, viewModel, error = {
                            errorText = it
                        },
                            goBack = {
                                outterNavController.popBackStack()
                            })
                    }) {
                    Text(text = "Send to Other")
                }
            }
        }

    }
}

private fun onSendToOtherClick(
    sum: Int,
    expense: Expense?,
    viewModel: ExpensesViewModel,
    error: (String) -> (Unit),
    goBack: () -> (Unit)
) {
    if (sum == 0 || sum < 0) {
        error.invoke("sum should be positive value")
        return
    }
    if (expense == null) {
        error.invoke("Select expense!")
        return
    }
    sendMoneyToOther(sum, expense, viewModel)
    goBack.invoke()
}

private fun onTransactionClick(
    sum: Int,
    expense: Expense?,
    isIncrease: Boolean,
    isRestart: Boolean,
    datePicked:Long,
    viewModel: ExpensesViewModel,
    error: (String) -> (Unit),
    goBack: () -> (Unit)
) {
    if ((sum == 0 || sum < 0) &&!isRestart) {
        error.invoke("sum should be positive value")
        return
    }
    if (isIncrease) {
        moneyIncrease(sum, isRestart, viewModel,datePicked)
        goBack.invoke()
        return
    } else {
        if (expense == null) {
            error.invoke("Select expense!")
            return
        }
        moneyDecrease(sum, expense, viewModel)
        goBack.invoke()
    }

}

private fun sendMoneyToOther(sum: Int, expense: Expense, viewModel: ExpensesViewModel) {
    viewModel.sendMoneyToOther(expense.id, expense.name, sum)
}

private fun moneyIncrease(moneyIncrease: Int, isRestart: Boolean, viewModel: ExpensesViewModel,time:Long) {
    viewModel.moneyIncrease(isRestart, moneyIncrease.toLong(),time)
}

private fun moneyDecrease(change: Int, expense: Expense, viewModel: ExpensesViewModel) {
    viewModel.moneyDecrease(expense, change.toLong())
}