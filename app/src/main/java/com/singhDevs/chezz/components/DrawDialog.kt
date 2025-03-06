package com.singhDevs.chezz.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R

@Composable
fun DrawDialog(
    modifier: Modifier = Modifier,
    onDrawAccepted: () -> Unit,
    onDrawRejected: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier.size(30.dp),
            onClick = onDrawRejected
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cross),
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            text = "Draw?",
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            color = Color.White
        )
        IconButton(
            modifier = Modifier.size(30.dp),
            onClick = onDrawAccepted
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_tick),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}