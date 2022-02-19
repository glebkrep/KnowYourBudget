package com.glbgod.knowyourbudget.ui.pages.transactionsList.data

sealed class TransactionsPageEvent{
    data class OnTransactionClicked(val transactionItem: TransactionItem):TransactionsPageEvent()
    data class OnTransactionEditSuccess(val transactionItem: TransactionItem, val newValue:Int):TransactionsPageEvent()
    data class OnTransactionDeleteClicked(val transactionItem: TransactionItem):TransactionsPageEvent()
    data class OnTransactionDeleteSuccess(val transactionItem: TransactionItem):TransactionsPageEvent()

    object DismissDialog:TransactionsPageEvent()
}
