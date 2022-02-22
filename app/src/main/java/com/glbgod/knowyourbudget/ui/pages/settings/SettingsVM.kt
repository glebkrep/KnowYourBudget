package com.glbgod.knowyourbudget.ui.pages.settings

import android.R
import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewModelScope
import com.glbgod.knowyourbudget.core.utils.PreferencesProvider
import com.glbgod.knowyourbudget.feature.db.BudgetRepository
import com.glbgod.knowyourbudget.feature.db.BudgetRoomDB
import com.glbgod.knowyourbudget.ui.BaseAction
import com.glbgod.knowyourbudget.ui.pages.settings.data.ExportData
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingOption
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageEvent
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class SettingsVM(application: Application) : SettingsPageVMAbs(application) {

    private var budgetRepository: BudgetRepository = BudgetRepository(
        BudgetRoomDB.getDatabase(application, viewModelScope).expensesDao(),
        BudgetRoomDB.getDatabase(application, viewModelScope).transactionsDao()
    )

    init {
        PreferencesProvider.init(application)
        postState(
            SettingsPageState.DefState(
                listOf(
                    SettingOption.DeleteAll,
                    SettingOption.ExportData,
                    SettingOption.ImportData
                )
            )
        )
    }

    override fun handleEvent(event: SettingsPageEvent) {
        when (event) {
            is SettingsPageEvent.OnDismissDialog -> {
                val currentState = getCurrentStateNotNull()
                postState(
                    SettingsPageState.DefState(currentState.settingsOptions)
                )
            }
            is SettingsPageEvent.OnSettingOptionClick -> {
                val currentState = getCurrentStateNotNull()
                when (event.settingOption) {
                    SettingOption.DeleteAll -> {
                        postState(
                            SettingsPageState.ClearingAppData(
                                currentState.settingsOptions
                            )
                        )
                    }
                    SettingOption.ImportData -> {
                        postState(
                            SettingsPageState.ImportingData(
                                currentState.settingsOptions
                            )
                        )
                    }
                    SettingOption.ExportData -> {
                        postState(
                            SettingsPageState.ExportingData(
                                currentState.settingsOptions
                            )
                        )
                    }
                }
            }
            is SettingsPageEvent.OnSuccessClearAll -> {
                clearAll()
                handleEvent(SettingsPageEvent.OnDismissDialog)
            }
            is SettingsPageEvent.OnSuccessExport -> {
                export()
                handleEvent(SettingsPageEvent.OnDismissDialog)
            }
            is SettingsPageEvent.OnSuccessImport -> {
                clearAll()
                import(event.importData)
                PreferencesProvider.saveNotFirstStart()
                handleEvent(SettingsPageEvent.OnDismissDialog)
            }
        }
    }

    private fun clearAll() {
        PreferencesProvider.setEverythingToDefault()
        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.clearAllTransactions()
            budgetRepository.clearAllExpenses()
        }
    }

    private fun export() {
        viewModelScope.launch(Dispatchers.IO) {
            val cycleStartTime = PreferencesProvider.getCycleStartTime()
            val restartMoney = PreferencesProvider.getRestartMoney()
            val monthStartBalance = PreferencesProvider.getMonthStartBalance()
            val expenses = budgetRepository.getAllExpenses()
            val transactions = budgetRepository.getAllTransactions()
            val exportData = ExportData(
                cycleStartTime = cycleStartTime,
                restartMoney = restartMoney,
                monthStartBalance = monthStartBalance,
                expenses = expenses,
                transactions = transactions
            )
            val outputData = Json.encodeToString(exportData)
            postAction(BaseAction.GoAway(outputData))
            handleEvent(SettingsPageEvent.OnDismissDialog)
        }
    }

    private fun import(importData: String) {
        val exportData = Json.decodeFromString<ExportData>(importData)
        viewModelScope.launch(Dispatchers.IO) {
            PreferencesProvider.saveCycleStartTime(exportData.cycleStartTime)
            PreferencesProvider.saveRestartMoney(exportData.restartMoney)
            PreferencesProvider.saveMonthStartBalance(exportData.monthStartBalance)
            for (expense in exportData.expenses) {
                budgetRepository.insertExpense(expense)
            }
            for (transaction in exportData.transactions) {
                budgetRepository.insertTransaction(transaction)
            }
            handleEvent(SettingsPageEvent.OnDismissDialog)
        }
    }

}
