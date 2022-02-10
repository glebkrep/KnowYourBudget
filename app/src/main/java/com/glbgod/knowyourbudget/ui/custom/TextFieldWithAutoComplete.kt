package com.glbgod.knowyourbudget.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.window.PopupProperties
import com.glbgod.knowyourbudget.core.utils.Debug


@Composable
fun TextFieldWithAutoComplete(
    modifier: Modifier = Modifier,
    value: String,
    onUpdate: (String) -> Unit,
    onDismissRequest: () -> Unit,
    dropDownExpanded: Boolean,
    onFocusReceived: () -> (Unit),
    onFocusLost: () -> (Unit),
    list: List<String>,
) {
    Box(modifier) {
        MyTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused)
                        onDismissRequest()
                },
            onFocusChanged = {
                Debug.log("Focus: hasFocus:${it.hasFocus}|isCaptured:${it.isCaptured}|isFocused:${it.isFocused}")
                if (it.isFocused) {
                    onFocusReceived.invoke()
                } else onFocusLost.invoke()
            },
            value = value,
            onValueChange = {
                onUpdate.invoke(it)
            },
        )
        DropdownMenu(
            expanded = dropDownExpanded,
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = onDismissRequest
        ) {
            list.forEach { text ->
                DropdownMenuItem(onClick = {
                    onUpdate(
                        text
                    )
                }) {
                    Text(text = text)
                }
            }
        }
    }
}