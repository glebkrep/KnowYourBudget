package com.glbgod.knowyourbudget.ui.pages.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.glbgod.knowyourbudget.ui.pages.budgetlist.views.ConfirmationDialog
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageEvent
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageState
import com.glbgod.knowyourbudget.ui.pages.settings.views.SettingsPageView

@Composable
fun SettingsPage(
    outterNavController: NavController,
    settingsVM: SettingsVM = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val state by settingsVM.state.observeAsState()
    if (state == null) return
    when (state) {
        is SettingsPageState.ClearingAppData -> {
            ConfirmationDialog(
                "Сбросить данные",
                "Вы точно хотите сброосить все данные? Данное действие нельзя будет отменить.",
                onAccept = {
                    settingsVM.handleEvent(
                        SettingsPageEvent.OnSuccessClearAll
                    )
                },
                onCancel = {
                    settingsVM.handleEvent(
                        SettingsPageEvent.OnDismissDialog
                    )
                })
        }
        is SettingsPageState.ExportingData -> {
            ConfirmationDialog(
                "Экспортировать данные",
                "Экспортировать данные приложения и отправить",
                onAccept = {
                    settingsVM.handleEvent(
                        SettingsPageEvent.OnSuccessClearAll
                    )
                },
                onCancel = {
                    settingsVM.handleEvent(
                        SettingsPageEvent.OnDismissDialog
                    )
                })
        }
        is SettingsPageState.ImportingData -> {
            ConfirmationDialog(
                "Импортировать данные",
                "Импортиоровать данные из файла",
                onAccept = {
                    settingsVM.handleEvent(
                        SettingsPageEvent.OnSuccessClearAll
                    )
                },
                onCancel = {
                    settingsVM.handleEvent(
                        SettingsPageEvent.OnDismissDialog
                    )
                })
        }
        else -> {}
    }
    SettingsPageView(state!!.settingsOptions) {
        settingsVM.handleEvent(it)
    }
}