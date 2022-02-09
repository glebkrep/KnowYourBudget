package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.glbgod.knowyourbudget.core.utils.toBeautifulString
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState
import com.glbgod.knowyourbudget.ui.theme.MyColors

@Composable
fun MoneyIncomeDialog(
    state: BudgetPageState.EditTotalBalanceDialog,
    onEvent: (BudgetPageEvent) -> (Unit)
) {
    var sumInput by remember { mutableStateOf("") }
    var sumError by remember { mutableStateOf("") }

    var isRestart by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = {
        onEvent.invoke(BudgetPageEvent.DialogDismissed)
    }) {
        Card(
            Modifier
                .fillMaxWidth(), shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
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
                    TextField(
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text(
                        text = "Назад",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .background(MyColors.ButtonRed)
                            .clickable {
                                onEvent.invoke(BudgetPageEvent.DialogDismissed)
                            }
                            .padding(16.dp)
                    )
                    Text(
                        text = "Сохранить",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .background(MyColors.ButtonGreen)
                            .clickable {
                                if (sumError!="") return@clickable
                                onEvent.invoke(BudgetPageEvent.EditTotalBalanceFinished(sumInput.replace(" ","").toInt(),isRestart))
                            }
                            .padding(16.dp)
                    )

                }
            }


        }
    }
}