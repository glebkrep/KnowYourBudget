package com.glbgod.knowyourbudget.ui.pages.settings.data

import com.glbgod.knowyourbudget.feature.db.data.ExpenseModel
import com.glbgod.knowyourbudget.feature.db.data.TransactionModel
import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val cycleStartTime:Long,
    val restartMoney:Int,
    val monthStartBalance:Int,
    val expenses:List<ExpenseModel>,
    val transactions:List<TransactionModel>
)