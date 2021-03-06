package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.R
import com.glbgod.knowyourbudget.data.ExpenseCategoryData
import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity
import com.glbgod.knowyourbudget.ui.pages.budgetlist.data.BudgetPageEvent
import com.glbgod.knowyourbudget.ui.theme.Shapes
import com.glbgod.knowyourbudget.ui.theme.UiConsts

@OptIn(ExperimentalUnitApi::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun ExpenseInOneRegularity(
    regularity: ExpenseRegularity,
    expenseCategoriesData: List<ExpenseCategoryData>,
    isOpen: Boolean,
    onRegularityClick: (ExpenseRegularity) -> (Unit),
    onReceivedEvent: (BudgetPageEvent) -> (Unit)
) {
    Column(
        Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = Shapes.small,
            backgroundColor = regularity.regularityStyle.backgroundColor,
            onClick = {
                onRegularityClick.invoke(regularity)
            }) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        id =
                        if (!isOpen) R.drawable.ic_arrow_down else R.drawable.ic_arrow_top
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(16.dp),
                )
                Text(
                    regularity.text,
                    Modifier
                        .padding(UiConsts.padding),
                    fontSize = 20.sp,
                    color = regularity.regularityStyle.fontColor,
                    fontWeight = FontWeight.Medium
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_add_plus),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(16.dp)
                        .clickable {
                            onReceivedEvent.invoke(
                                BudgetPageEvent.AddExpenseClicked(
                                    regularity
                                )
                            )
                        },
                )
            }
        }

        if (isOpen && expenseCategoriesData.isNotEmpty()) {
            for (category in expenseCategoriesData) {
                ExpenseCategoryItem(
                    listExpenseItem = category.items,
                    onEvent = {
                        onReceivedEvent.invoke(it)
                    }
                )
            }
            Text(
                text = "Next refill in ${expenseCategoriesData.first().items.first().nextRefillInDays} days",
                Modifier.padding(UiConsts.padding)
            )
        }
    }
}