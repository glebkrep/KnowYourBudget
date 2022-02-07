package com.glbgod.knowyourbudget.ui.pages.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.glbgod.knowyourbudget.transactions.data.Transaction
import com.glbgod.knowyourbudget.transactions.data.TransactionCategory
import com.glbgod.knowyourbudget.ui.theme.UiConsts
import com.glbgod.knowyourbudget.utils.toDateTime
import com.glbgod.knowyourbudget.viewmodel.ExpensesViewModel

@Composable
fun TransactionsPage(viewModel: ExpensesViewModel) {
    val transactionItems by viewModel.allTransactions.observeAsState(listOf())
    LazyColumn(Modifier.padding(UiConsts.padding)) {
        if (transactionItems.isEmpty()) {
            item() {
                Text(text = "Nothing here yet")
            }
            return@LazyColumn
        }
        var even = true
        items(transactionItems) { item ->
            TransactionItem(
                transaction = item,
                when (item.transactionCategory) {
                    TransactionCategory.INCREASE.category -> {
                        Color(red = 0f, blue = 0f, green = 1f)
                    }
                    TransactionCategory.SPENT.category -> {
                        Color(red = 1f, blue = 0f, green = 0f)
                    }
                    else -> {
                        even = !even
                        Color(red = 0f, green = 0f, blue = 0f, alpha = if (even) 0.1f else 0.3f)
                    }
                }, viewModel
            )
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    bgColor: Color,
    viewModel: ExpensesViewModel
) {
    Column(
        Modifier
            .padding(start = UiConsts.padding, end = UiConsts.padding, top = UiConsts.padding)
            .background(bgColor)
            .border(1.dp, Color.Gray)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onTransItemClick(transaction, viewModel)
                    },
                    onTap = {
                    }
                )
            }
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(start = UiConsts.padding, end = UiConsts.padding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(transaction.expenseName)
            Text(transaction.change.toString())
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = UiConsts.padding, end = UiConsts.padding)) {
            Text(transaction.time.toDateTime().split(" ")[0])
        }
    }
}

private fun onTransItemClick(transaction: Transaction, viewModel: ExpensesViewModel) {
    viewModel.revertTransaction(transaction)
}