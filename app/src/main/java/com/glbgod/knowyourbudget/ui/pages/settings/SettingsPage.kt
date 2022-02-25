package com.glbgod.knowyourbudget.ui.pages.settings

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.glbgod.knowyourbudget.ui.BaseAction
import com.glbgod.knowyourbudget.ui.pages.budgetlist.views.ConfirmationDialog
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageEvent
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageState
import com.glbgod.knowyourbudget.ui.pages.settings.views.SettingsImportDataDialog
import com.glbgod.knowyourbudget.ui.pages.settings.views.SettingsPageView

@Composable
fun SettingsPage(
    outterNavController: NavController,
    settingsVM: SettingsVM = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val state by settingsVM.state.observeAsState()
    val action by settingsVM.action.observeAsState()

    val context = LocalContext.current
    LaunchedEffect(key1 = action, key2 = context, block = {
        if (action != null) {
            if (action is BaseAction.GoAway) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, (action as BaseAction.GoAway).route)
                context.startActivity(Intent.createChooser(intent, "Отправить в..."))
            }
        }
    })

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
                actionButtonText = "Экспортировать",
                onAccept = {
                    settingsVM.handleEvent(
                        SettingsPageEvent.OnSuccessExport
                    )
                },
                onCancel = {
                    settingsVM.handleEvent(
                        SettingsPageEvent.OnDismissDialog
                    )
                })
        }
        is SettingsPageState.ImportingData -> {
            SettingsImportDataDialog(
                onAccept = { importData ->
                    settingsVM.handleEvent(
                        SettingsPageEvent.OnSuccessImport(importData)
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