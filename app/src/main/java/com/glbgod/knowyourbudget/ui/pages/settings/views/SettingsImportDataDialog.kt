package com.glbgod.knowyourbudget.ui.pages.settings.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glbgod.knowyourbudget.core.utils.firstNOrNull
import com.glbgod.knowyourbudget.ui.custom.MyDialog
import com.glbgod.knowyourbudget.ui.custom.MyTextField
import com.glbgod.knowyourbudget.ui.theme.MyColors

@Composable
fun SettingsImportDataDialog(onCancel: () -> (Unit), onAccept: (String) -> (Unit)) {
    var importInput by remember { mutableStateOf("") }

    MyDialog(
        backgroundColor = MyColors.DailyColor,
        isAcceptActive = (importInput.isNotEmpty()),
        onDismissRequest = { onCancel.invoke() },
        onBackClicked = { onCancel.invoke() },
        onYesClicked = {
            onAccept.invoke(importInput)
        }) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Импортировать данные",
                fontWeight = FontWeight.Medium,
                color = MyColors.DailyFont,
                fontSize = 20.sp,
            )
        }

        Text(
            text = "Данные для импорта",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        MyTextField(
            value = importInput.firstNOrNull(10),
            onValueChange = { newVal: String ->
                importInput = newVal
            },
            modifier = Modifier.padding(bottom = 8.dp),
        )
    }
}