package com.glbgod.knowyourbudget.ui.pages.settings.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingOption
import com.glbgod.knowyourbudget.ui.pages.settings.data.SettingsPageEvent
import com.glbgod.knowyourbudget.ui.theme.MyColors
import com.glbgod.knowyourbudget.ui.theme.Shapes

@Composable
fun SettingOptionView(settingOption: SettingOption, onEvent: (SettingsPageEvent) -> (Unit)) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp)
            .clickable {
                onEvent.invoke(
                    SettingsPageEvent.OnSettingOptionClick(
                        settingOption
                    )
                )
            },
        shape = Shapes.small,
        border = BorderStroke(2.dp, MyColors.Blackish)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = settingOption.text, fontSize = 20.sp)
        }
    }
}