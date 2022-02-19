package com.glbgod.knowyourbudget.ui.pages.transactionsList.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.core.utils.toBeautifulString
import com.glbgod.knowyourbudget.core.utils.toDateTime
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionItem
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageEvent
import com.glbgod.knowyourbudget.ui.theme.MyColors
import com.glbgod.knowyourbudget.ui.theme.Shapes
import com.glbgod.knowyourbudget.ui.theme.UiConsts

@Composable
fun TransactionView(transactionItem: TransactionItem, onEvent: (TransactionsPageEvent) -> (Unit)) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp),
        shape = Shapes.small,
        border = BorderStroke(2.dp, transactionItem.regularity.regularityStyle.backgroundColor)
    ) {
        Row(
            Modifier
                .padding(UiConsts.padding)
                .clickable {
                    onEvent.invoke(TransactionsPageEvent.OnTransactionClicked(transactionItem))
                }, verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = transactionItem.iconResId),
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = transactionItem.expenseName,
                        fontSize = 15.sp,
                    )
                    Text(
                        text = transactionItem.moneyChange.toBeautifulString(),
                        fontSize = 15.sp,
                        color = if (transactionItem.moneyChange > 0) {
                            MyColors.ProgressGood
                        } else MyColors.ProgressBad
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = transactionItem.transactionComment,
                        fontSize = 12.sp,
                        color = transactionItem.regularity.regularityStyle.fontColor
                    )
                    Text(
                        text = transactionItem.time.toDateTime(),
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}