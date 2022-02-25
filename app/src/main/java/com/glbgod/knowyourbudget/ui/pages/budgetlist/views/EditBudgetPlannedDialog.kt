package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.core.utils.toBeautifulString
import com.glbgod.knowyourbudget.ui.custom.DatePickerView
import com.glbgod.knowyourbudget.ui.custom.MyDialog
import com.glbgod.knowyourbudget.ui.custom.MyTextField
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState

@Composable
fun EditBudgetPlannedDialog(
    state: BudgetPageState.EditBudgetPlannedDialog,
    onEvent: (BudgetPageEvent) -> (Unit)
) {
    var budgetInput by remember { mutableStateOf<String>(state.totalBudgetData.outOf.toString()) }
    var budgetError by remember { mutableStateOf("") }

    MyDialog(
        backgroundColor = Color.LightGray,
        isAcceptActive = (budgetInput.isNotEmpty() && budgetError.isEmpty()),
        onDismissRequest = { onEvent.invoke(BudgetPageEvent.DialogDismissed) },
        onYesClicked = {
            if (budgetError == "") {
                onEvent.invoke(
                    BudgetPageEvent.EditBudgetPlannedFinished(
                        budgetInput
                            .replace(" ", "")
                            .toInt()
                    )
                )
            }

        },
        onBackClicked = {
            onEvent.invoke(BudgetPageEvent.DialogDismissed)

        }) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Плановый месячный бюджет",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
            )
        }
        Text(
            text = "Сумма бюджета",
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        MyTextField(
            value = budgetInput.toString(),
            onValueChange = { newVal: String ->
                try {
                    val intVal = newVal.replace(" ", "").toInt()
                    budgetInput = intVal.toBeautifulString()
                    budgetError = ""
                } catch (e: Exception) {
                    budgetInput = newVal
                    budgetError = "Нужно ввести число"
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(bottom = 8.dp),
            isError = budgetError != "",
        )
    }

}