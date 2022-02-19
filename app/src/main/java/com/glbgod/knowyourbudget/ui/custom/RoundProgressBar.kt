package com.glbgod.knowyourbudget.ui.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundProgressBar(progress: Float, color: Color, background: Color) {
    LinearProgressIndicator(
        progress = progress,
        Modifier
            .fillMaxWidth()
            .width(0.5.dp),
        color = color,
        backgroundColor = background
    )
}
