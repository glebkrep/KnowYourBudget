package com.glbgod.knowyourbudget.ui.pages.transactionsList

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.glbgod.knowyourbudget.ui.pages.transactionsList.data.TransactionsPageState
import com.glbgod.knowyourbudget.ui.pages.transactionsList.views.TransactionPageListView

@Composable
fun TransactionPage(
    outterNavController: NavController,
    transactionVM: TransactionsVM = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val state by transactionVM.state.observeAsState()
    if (state is TransactionsPageState.DefaultState){
        TransactionPageListView(state = state as TransactionsPageState.DefaultState)
    }
}