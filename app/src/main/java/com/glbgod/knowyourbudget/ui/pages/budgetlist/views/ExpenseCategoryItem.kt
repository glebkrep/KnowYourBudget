package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.data.ExpenseItem


@OptIn(ExperimentalUnitApi::class)
@Composable
fun ExpenseCategoryItem(
    listExpenseItem: List<ExpenseItem>,
    onExpenseClick: (ExpenseItem) -> Unit,
    onExpenseLongClick: (ExpenseItem) -> Unit,
) {
    Column(
        Modifier
            .padding(vertical = 16.dp)
            .wrapContentSize()
    ) {
        Text(
            text = listExpenseItem.firstOrNull()?.category ?: "",
            Modifier
                .fillMaxWidth(), fontSize = 18.sp
        )
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            for (item in listExpenseItem) {
                ExpenseItem(
                    expenseItem = item,
                    onExpenseClick = {
                        onExpenseClick.invoke(it)
                    },
                    onLongPress = {
                        onExpenseLongClick.invoke(it)
                    })
            }
        }
    }
}
