package com.glbgod.knowyourbudget.ui.pages.budgetlist.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.glbgod.knowyourbudget.ui.theme.MyColors

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfirmationDialog(
    header: String,
    text: String,
    onCancel: () -> (Unit),
    onAccept: () -> (Unit)
) {
    Dialog(onDismissRequest = {
        onCancel.invoke()
    }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                androidx.compose.ui.Modifier
                    .background(Color.White)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = header,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text(
                        text = "Отмена",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .background(MyColors.ButtonGreen)
                            .clickable {
                                onCancel.invoke()
                            }
                            .padding(16.dp)
                    )
                    Text(
                        text = "Удалить",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .background(MyColors.ButtonRed)
                            .clickable {
                                onAccept.invoke()
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}