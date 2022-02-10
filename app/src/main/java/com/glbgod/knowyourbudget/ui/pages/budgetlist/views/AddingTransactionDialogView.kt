package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.core.utils.toBeautifulString
import com.glbgod.knowyourbudget.ui.custom.MyDialog
import com.glbgod.knowyourbudget.ui.custom.MyTextField
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageState
import com.glbgod.knowyourbudget.ui.theme.MyColors
import com.glbgod.knowyourbudget.ui.theme.UiConsts

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddingTransactionDialogView(
    state: BudgetPageState.AddTransactionDialog,
    onEvent: (BudgetPageEvent) -> (Unit)
) {
    var sumInput by remember { mutableStateOf("") }
    var sumError by remember { mutableStateOf("") }

    var commentInput by remember {
        mutableStateOf("")
    }

    MyDialog(
        backgroundColor = UiConsts.iconsMap.get(state.expenseItem.iconResId)!!.backgroundColor,
        onDismissRequest = { onEvent.invoke(BudgetPageEvent.DialogDismissed) },
        onBackClicked = { onEvent.invoke(BudgetPageEvent.DialogDismissed) },
        onYesClicked = {
            if (sumError == "") {
                onEvent.invoke(
                    BudgetPageEvent.AddTransactionToExpenseFinished(
                        sum = sumInput
                            .replace(" ", "")
                            .toInt(),
                        expenseItem = state.expenseItem,
                        comment = commentInput
                    )
                )
            }
        }) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = state.expenseItem.name,
                fontWeight = FontWeight.Medium,
                color = UiConsts.iconsMap.get(state.expenseItem.iconResId)!!.textColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = state.expenseItem.iconResId),
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp)
                    .size(32.dp)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp)
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.expenseItem.currentBalanceForPeriod.toBeautifulString() + "/" + state.expenseItem.totalBalanceForPeriod.toBeautifulString(),
                        Modifier,
                        fontSize = 15.sp
                    )
                }
                LinearProgressIndicator(
                    progress = state.expenseItem.progressFloat,
                    Modifier.fillMaxWidth(),
                    color = if (state.expenseItem.progressFloat > 0.7f) MyColors.ProgressGood else MyColors.ProgressMedium,
                    backgroundColor = MyColors.ProgressBackground
                )
            }
        }


        Text(
            text = "Сумма",
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
            text = "Комментарий",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        MyTextField(
            value = commentInput.toString(),
            onValueChange = { newVal: String ->
                commentInput = newVal
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}