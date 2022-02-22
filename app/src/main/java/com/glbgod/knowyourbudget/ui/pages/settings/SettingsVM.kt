package com.glbgod.knowyourbudget.ui.pages.settings

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.glbgod.knowyourbudget.core.utils.PreferencesProvider
import com.glbgod.knowyourbudget.feature.db.BudgetRepository
import com.glbgod.knowyourbudget.feature.db.BudgetRoomDB
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingOption
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageEvent
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageState

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
                TODO()
            }
            is SettingsPageEvent.OnSuccessExport -> {
                TODO()
            }
            is SettingsPageEvent.OnSuccessImport -> {
                TODO()
            }
        }
    }

}
