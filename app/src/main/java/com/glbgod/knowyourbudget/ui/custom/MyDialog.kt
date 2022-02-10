package com.glbgod.knowyourbudget.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.glbgod.knowyourbudget.ui.theme.MyColors

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MyDialog(
    backgroundColor: Color = Color.White,
    isAcceptActive:Boolean,
    onDismissRequest: () -> (Unit),
    onBackClicked: () -> (Unit),
    onYesClicked: () -> (Unit),
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = {
        onDismissRequest.invoke()
    }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                Modifier
                    .background(backgroundColor)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    content.invoke()
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text(
                        text = "Назад",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .background(MyColors.ButtonRed)
                            .clickable {
                                onBackClicked.invoke()
                            }
                            .padding(16.dp)
                    )
                    Text(
                        text = "Сохранить",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color =Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isAcceptActive)MyColors.ButtonGreen else Color(0xFFA5A5A5))
                            .clickable {
                                if (isAcceptActive){
                                    onYesClicked.invoke()
                                }
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}