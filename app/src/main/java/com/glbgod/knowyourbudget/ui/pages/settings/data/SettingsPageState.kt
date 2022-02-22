package com.glbgod.knowyourbudget.ui.pages.settings.data

sealed class SettingsPageState(val settingsOptions: List<SettingOption>) {
    data class DefState(private val _settingsOptions: List<SettingOption>) :
        SettingsPageState(_settingsOptions)

    data class ClearingAppData(private val _settingsOptions: List<SettingOption>) :
        SettingsPageState(_settingsOptions)

    data class ExportingData(private val _settingsOptions: List<SettingOption>) :
        SettingsPageState(_settingsOptions)

    data class ImportingData(private val _settingsOptions: List<SettingOption>) :
        SettingsPageState(_settingsOptions)

}