package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.R
import com.glbgod.knowyourbudget.core.utils.toBeautifulString
import com.glbgod.knowyourbudget.data.TotalBudgetData
import com.glbgod.knowyourbudget.ui.custom.RoundProgressBar
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.theme.MyColors
import com.glbgod.knowyourbudget.ui.theme.UiConsts

@Composable
fun BudgetTopBar(totalBudgetData: TotalBudgetData,onEvent:(BudgetPageEvent)->(Unit)) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_rubble),
            contentDescription = "",
            modifier = Modifier.size(32.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
                .clickable {
                    onEvent.invoke(BudgetPageEvent.EditTotalBalanceClicked)
                }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                Text(
                    text = "${totalBudgetData.balance.toBeautifulString()} / ${totalBudgetData.outOf.toBeautifulString()})",
                    modifier = Modifier
                        .padding(UiConsts.padding)
                        .weight(1f), fontSize = 18.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "",
                    modifier = Modifier.size(15.dp)
                )
            }
            val start = totalBudgetData.balance
            val end = totalBudgetData.outOf
            val progress = if (start == null || end == null || start > end || end == 0) 1f
            else {
                start.toFloat() / end.toFloat()
            }
            RoundProgressBar(
                color = MyColors.ProgressGood,
                progress = progress,
                background = MyColors.ProgressBackground
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = "",
            modifier = Modifier
                .padding(start = 4.dp)
                .size(24.dp).clickable { onEvent.invoke(BudgetPageEvent.SettingsClicked) }
        )
    }
}