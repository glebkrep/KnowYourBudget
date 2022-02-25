package com.glbgod.knowyourbudget.ui.pages.settings.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingOption
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageEvent

@Composable
fun SettingsPageView(settingOptions: List<SettingOption>, onEvent: (SettingsPageEvent) -> (Unit)) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Настройки",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        LazyColumn(Modifier.fillMaxWidth()) {
            items(settingOptions) { settingOption ->
                SettingOptionView(settingOption) {
                    onEvent.invoke(it)
                }
            }
        }
    }
}
