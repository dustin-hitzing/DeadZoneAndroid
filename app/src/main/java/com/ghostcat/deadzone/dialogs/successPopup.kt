package com.ghostcat.deadzone.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SuccessPopup(
    visible: Boolean,
    title: String,
    subTitle: String,
    message: String,
    confirmText: String,
    bgColor: Color,
    confirmColor: Color,
    onConfirm: () -> Unit,
) {
    if (visible) {
        Dialog(onDismissRequest = onConfirm) {
            Box(
                modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(bgColor)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = 17.dp, vertical = 35.dp)
                ) {
                    Spacer(modifier = Modifier.height(45.dp))
                    Column {
                        Text(title, style = MaterialTheme.typography.headlineLarge)
                        Text(subTitle)
                        Spacer(modifier = Modifier.height(45.dp))
                        Text(message)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 35.dp)
                            .fillMaxWidth(),
                    ) {
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = confirmColor,
                                contentColor = Color.White
                            )
                        ) {
                            Text(confirmText)
                        }
                    }
                }


            }
        }
    }
}