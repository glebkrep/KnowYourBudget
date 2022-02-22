package com.glbgod.knowyourbudget.ui.pages.settings

import android.app.Application
import com.glbgod.knowyourbudget.ui.AbstractPageAndroidVM
import com.glbgod.knowyourbudget.ui.BaseAction
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageEvent
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageState

abstract class SettingsPageVMAbs(application: Application) :
    AbstractPageAndroidVM<SettingsPageEvent, SettingsPageState, BaseAction>(
        application,
        BaseAction.None
    ) {
}