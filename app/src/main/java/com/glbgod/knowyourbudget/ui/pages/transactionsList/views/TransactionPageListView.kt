package com.glbgod.knowyourbudget.ui.pages.transactionsList.views

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageState

@Composable
fun TransactionPageListView(state:TransactionsPageState.DefaultState) {
    LazyColumn(Modifier.padding(horizontal = 16.dp)){
        items(state.transactionItems){
            TransactionView(it)
        }
    }
}