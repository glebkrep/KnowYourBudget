package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.core.utils.toBeautifulString
import com.glbgod.knowyourbudget.ui.custom.MyDialog
import com.glbgod.knowyourbudget.ui.custom.MyTextField
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MoneyIncomeDialog(
    state: BudgetPageState.EditTotalBalanceDialog,
    onEvent: (BudgetPageEvent) -> (Unit)
) {
    var sumInput by remember { mutableStateOf("") }
    var sumError by remember { mutableStateOf("") }

    var isRestart by remember { mutableStateOf(false) }

    MyDialog(
        backgroundColor = Color.LightGray,
        isAcceptActive = (sumInput.isNotEmpty() && sumError.isEmpty()),
        onDismissRequest = { onEvent.invoke(BudgetPageEvent.DialogDismissed) },
        onYesClicked = {
            if (sumError == "") {
                onEvent.invoke(
                    BudgetPageEvent.EditTotalBalanceFinished(
                        sumInput
                            .replace(" ", "")
                            .toInt(), isRestart
                    )
                )
            }

        },
        onBackClicked = {
            onEvent.invoke(BudgetPageEvent.DialogDismissed)

        }) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Пополенение баланса",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(
            text = "Сумма пополнения",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        MyTextField(
            value = sumInput.toString(),
            onValueChange = { newVal: String ->
                try {
                    val intVal = newVal.replace(" ", "").toInt()
                    sumInput = intVal.toBeautifulString()
                    sumError = ""
                } catch (e: Exception) {
                    sumInput = newVal
                    sumError = "Нужно ввести число"
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(bottom = 8.dp),
            isError = sumError != "",
        )
        Text(
            text = "Начать новый цикл?",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Checkbox(checked = isRestart, onCheckedChange = {
            isRestart = it
        })
    }

}