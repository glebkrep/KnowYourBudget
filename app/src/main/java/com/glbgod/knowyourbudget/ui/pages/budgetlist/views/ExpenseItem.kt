package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.R
import com.glbgod.knowyourbudget.core.utils.toBeautifulString
import com.glbgod.knowyourbudget.data.ExpenseItem
import com.glbgod.knowyourbudget.ui.theme.MyColors
import com.glbgod.knowyourbudget.ui.theme.Shapes
import com.glbgod.knowyourbudget.ui.theme.UiConsts

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ExpenseItem(
    expenseItem: ExpenseItem,
    onExpenseClick: (ExpenseItem) -> (Unit),
    onLongPress: (ExpenseItem) -> (Unit)
) {

    val modifier = Modifier
        .padding(top = 8.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    onLongPress.invoke(expenseItem)
                },
                onTap = {
                    onExpenseClick.invoke(expenseItem)
                }
            )
        }
    Card(
        modifier = modifier,
        shape = Shapes.small,
        border = BorderStroke(2.dp, expenseItem.regularity.regularityStyle.backgroundColor)
    ) {
        Row(Modifier.padding(UiConsts.padding), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_rubble),
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
                    Text(expenseItem.name)
                    Text(
                        text = expenseItem.currentBalanceForPeriod.toBeautifulString() + "/" + expenseItem.totalBalanceForPeriod.toBeautifulString(),
                        Modifier,
                        fontSize = 15.sp
                    )
                }
                LinearProgressIndicator(
                    progress = expenseItem.progressFloat,
                    Modifier.fillMaxWidth(),
                    color = if (expenseItem.progressFloat > 0.7f) MyColors.ProgressGood else MyColors.ProgressMedium,
                    backgroundColor = MyColors.ProgressBackground
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Остаток на месяц ${expenseItem.totalBalanceLeft.toBeautifulString()}",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}