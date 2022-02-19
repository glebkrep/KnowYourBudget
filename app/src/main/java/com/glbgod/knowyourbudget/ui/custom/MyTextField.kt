package com.glbgod.knowyourbudget.ui.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> (Unit),
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onFocusChanged: (FocusState) -> (Unit) = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    TextField(
        value = value,
        onValueChange = { newVal ->
            onValueChange.invoke(newVal)
        },
        modifier = modifier
            .onFocusChanged {
                onFocusChanged.invoke(it)
            }
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        isError = isError,
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}