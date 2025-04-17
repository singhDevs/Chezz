package com.singhDevs.chezz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val primaryAmber = Color(0xFFBF8F3F)
    val fieldBackground = Color(0xFF2A2A2A)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(fieldBackground),
            onClick = onDrawRejected
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(R.drawable.ic_cross),
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = "Draw?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        IconButton(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(fieldBackground),
            onClick = onDrawAccepted
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(R.drawable.ic_tick),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}