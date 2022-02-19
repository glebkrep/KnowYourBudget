package com.glbgod.knowyourbudget.ui.pages.transactionsList.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.glbgod.knowyourbudget.R
import com.glbgod.knowyourbudget.core.utils.toBeautifulString
import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity
import com.glbgod.knowyourbudget.ui.custom.MyDialog
import com.glbgod.knowyourbudget.ui.custom.MyTextField
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionItem
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageEvent
import com.glbgod.knowyourbudget.ui.theme.MyColors
import kotlin.math.absoluteValue

@Composable
fun EditingTransactionDialog(
    transactionItem: TransactionItem,
    onEvent: (TransactionsPageEvent) -> (Unit)
) {
    var transactionChange by remember {
        mutableStateOf(
            transactionItem.moneyChange.absoluteValue.toBeautifulString()
        )
    }
    var transactionChangeError by remember { mutableStateOf("") }

    MyDialog(
        backgroundColor = transactionItem.regularity.regularityStyle.backgroundColor,
        isAcceptActive = (transactionChangeError.isEmpty() && transactionChange.replace(" ","").toInt() > 0),
        onDismissRequest = {
            onEvent.invoke(
                TransactionsPageEvent.DismissDialog
            )
        },
        onBackClicked = {
            onEvent.invoke(
                TransactionsPageEvent.DismissDialog
            )
        },
        onYesClicked = {
            onEvent.invoke(
                TransactionsPageEvent.OnTransactionEditSuccess(
                    transactionItem, transactionChange.replace(" ","").toInt()
                )
            )
        }) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(
                painter = painterResource(id = R.drawable.ic_trash),
                contentDescription = "",
                tint =
                Color.Transparent,
                modifier = Modifier
                    .padding(
                        start = 4.dp, end = 8.dp
                    )
                    .size(22.dp)
            )
            Text(
                text = transactionItem.expenseName,
                fontWeight = FontWeight.Medium,
                color = transactionItem.regularity.regularityStyle.fontColor,
                fontSize = 20.sp,
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_trash),
                contentDescription = "",
                tint =
                Color.Unspecified,
                modifier = Modifier
                    .padding(
                        start = 4.dp, end = 8.dp
                    )
                    .size(22.dp)
                    .clickable {
                        onEvent.invoke(
                            TransactionsPageEvent.OnTransactionDeleteClicked(
                                transactionItem = transactionItem
                            )
                        )
                    }
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)) {
            TransactionView(transactionItem = transactionItem, onEvent = {

            })
        }

        Text(
            text = "Сумма транзакции",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        MyTextField(
            value = transactionChange,
            onValueChange = { newVal: String ->
                try {
                    val intVal = newVal.replace(" ", "").toInt()
                    if (intVal>0){
                        transactionChange = intVal.toBeautifulString()
                        transactionChangeError = ""
                    }
                    else {
                        transactionChange = newVal
                        transactionChangeError = "Нужно ввести число больше 0"
                    }
                } catch (e: Exception) {
                    transactionChange = newVal
                    transactionChangeError = "Нужно ввести число"
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(bottom = 8.dp),
            isError = transactionChangeError != "",
        )
    }
}