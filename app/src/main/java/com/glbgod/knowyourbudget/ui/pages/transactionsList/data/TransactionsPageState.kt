package com.glbgod.knowyourbudget.ui.pages.transactionsList.data

sealed class TransactionsPageState{
    data class DefaultState(val transactionItems:List<TransactionItem>):TransactionsPageState()
}
