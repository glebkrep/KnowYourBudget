package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.core.utils.toBeautifulString
import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity
import com.glbgod.knowyourbudget.ui.custom.MyDialog
import com.glbgod.knowyourbudget.ui.custom.MyTextField
import com.glbgod.knowyourbudget.ui.custom.TextFieldWithAutoComplete
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState
import com.glbgod.knowyourbudget.ui.theme.MyColors

@Composable
fun AddingExpenseDialog(
    state: BudgetPageState.NewExpenseDialog,
    onEvent: (BudgetPageEvent) -> (Unit)
) {

    var leftoverAfterAddingExpense by remember {
        mutableStateOf<String>("-")
    }
    var perMonthTotal by remember {
        mutableStateOf<String>("-")
    }

    var selectedIcon by remember { mutableStateOf<Int?>(null) }
    var name by remember { mutableStateOf("") }
    var regularityBudget by remember { mutableStateOf("") }
    var regularityBudgetError by remember { mutableStateOf("") }

    var selectedCategory by remember { mutableStateOf("") }
    var dropDownOptions by remember {
        mutableStateOf<List<String>>(state.newExpenseData.categoryList)
    }
    var dropDownExpanded by remember {
        mutableStateOf(false)
    }

    MyDialog(
        backgroundColor = state.newExpenseData.regularity.regularityStyle.backgroundColor,
        isAcceptActive = (selectedIcon != null && selectedCategory.isNotEmpty() && name.isNotEmpty()
                && regularityBudget != "" && regularityBudgetError.isEmpty()
                && (leftoverAfterAddingExpense.replace(" ", "").toInt() >= 0)),
        onDismissRequest = {
            onEvent.invoke(
                BudgetPageEvent.DialogDismissed
            )
        },
        onBackClicked = {
            onEvent.invoke(
                BudgetPageEvent.DialogDismissed
            )
        },
        onYesClicked = {
            onEvent.invoke(
                BudgetPageEvent.EditExpenseFinished(
                    ExpenseModel(
                        name = name,
                        iconResId = selectedIcon!!,
                        regularity = state.newExpenseData.regularity.regularity,
                        category = selectedCategory,
                        budgetPerRegularity = regularityBudget.replace(" ", "").toInt()
                    )
                )
            )
        }) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = state.newExpenseData.regularity.text,
                fontWeight = FontWeight.Medium,
                color = state.newExpenseData.regularity.regularityStyle.fontColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Text(
            text = "Иконка",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
        ) {
            items(state.newExpenseData.iconsIdList) {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "",
                    tint = if (it == selectedIcon) {
                        Color.Unspecified
                    } else state.newExpenseData.regularity.regularityStyle.fontColor,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 8.dp)
                        .size(32.dp)
                        .clickable {
                            selectedIcon = it
                        }
                )
            }
        }

        Text(
            text = "Категория",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextFieldWithAutoComplete(
            value = selectedCategory,
            onUpdate = { value ->
                dropDownExpanded = true
                selectedCategory = value
                dropDownOptions =
                    state.newExpenseData.categoryList.filter { it.startsWith(value) && it != value }
                        .take(5)
            },
            onFocusLost = {
                dropDownExpanded = false
            },
            onFocusReceived = {
                dropDownExpanded = true
            },
            onDismissRequest = { dropDownExpanded = false },
            dropDownExpanded = dropDownExpanded,
            list = dropDownOptions
        )

        Text(
            text = "Наименование",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        MyTextField(
            value = name,
            onValueChange = { newVal: String ->
                name = newVal
            },
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Text(
            text = "Бюджет на ${
                when (state.newExpenseData.regularity) {
                    ExpenseRegularity.DAILY -> "День"
                    ExpenseRegularity.WEEKLY -> "Неделю"
                    ExpenseRegularity.MONTHLY -> "Месяц"
                }
            }",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        MyTextField(
            value = regularityBudget,
            onValueChange = { newVal: String ->
                try {
                    val intVal = newVal.replace(" ", "").toInt()
                    regularityBudget = intVal.toBeautifulString()
                    regularityBudgetError = ""
                    val perMonthTotalInt = state.newExpenseData.regularity.refillsInMonth * intVal
                    perMonthTotal = perMonthTotalInt.toBeautifulString()
                    leftoverAfterAddingExpense =
                        (state.newExpenseData.freeToUseFunds - perMonthTotalInt).toBeautifulString()
                } catch (e: Exception) {
                    regularityBudget = newVal
                    regularityBudgetError = "Нужно ввести число"
                    leftoverAfterAddingExpense = "-"
                    perMonthTotal = "-"
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(bottom = 8.dp),
            isError = regularityBudgetError != "",
        )

        Text(
            text = "Всего в месяц выйдет: $perMonthTotal",
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Divider(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        Text(
            text = "Доступные средства: ${state.newExpenseData.freeToUseFunds}",
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Останется после добавления траты: $leftoverAfterAddingExpense",
            color = try {
                if (leftoverAfterAddingExpense.replace(" ", "")
                        .toInt() < 0
                ) MyColors.ProgressBad else Color.Unspecified
            } catch (e: java.lang.Exception) {
                Color.Unspecified
            }
        )

    }
}