package com.glbgod.knowyourbudget.ui.pages.transactionsList.data

sealed class TransactionsPageState(val transactionItems: List<TransactionItem>) {
    data class DefaultState(val _transactionItems: List<TransactionItem>) :
        TransactionsPageState(_transactionItems)

    data class EditDialog(
        val selectedItem: TransactionItem,
        val _transactionItems: List<TransactionItem>
    ) : TransactionsPageState(_transactionItems)

    data class DeleteConfirmationDialog(
        val selectedItem: TransactionItem,
        val _transactionItems: List<TransactionItem>
    ) : TransactionsPageState(_transactionItems)
}
