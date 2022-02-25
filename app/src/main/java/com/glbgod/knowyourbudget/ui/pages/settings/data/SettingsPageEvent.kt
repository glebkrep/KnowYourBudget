package com.glbgod.knowyourbudget.ui.pages.settings.data

sealed class SettingsPageEvent {
    data class OnSettingOptionClick(val settingOption: SettingOption) : SettingsPageEvent()
    object OnDismissDialog : SettingsPageEvent()
    object OnSuccessClearAll : SettingsPageEvent()
    object OnSuccessExport : SettingsPageEvent()
    data class OnSuccessImport(val importData: String) : SettingsPageEvent()

}