package com.singhDevs.chezz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.singhDevs.chezz.R

@Composable
fun ResignDialog(
    message: String,
    onDismiss: () -> Unit,
    negativeButtonAction: () -> Unit,
    positiveButtonAction: () -> Unit
) {
    val darkBackground = Color(0xFF1A1A1A)
    val primaryAmber = Color(0xFFBF8F3F)
    val fieldBackground = Color(0xFF2A2A2A)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .background(darkBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp),
                    text = message,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal, fontSize = 19.sp)
                )
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 5.dp)
                            .background(fieldBackground)
                            .zIndex(10f),
                        onClick = negativeButtonAction
                    ) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(R.drawable.ic_cross),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    IconButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 5.dp)
                            .background(primaryAmber)
                            .zIndex(10f),
                        onClick = positiveButtonAction
                    ) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(R.drawable.ic_tick),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PopupDialogPreview() {
    ResignDialog("Are you sure you want to resign?", {}, {}) { }
}