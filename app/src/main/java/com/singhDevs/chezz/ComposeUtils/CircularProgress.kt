package com.singhDevs.chezz.ComposeUtils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R
import com.singhDevs.chezz.utils.BasicUtils

@Composable
fun CircularProgress(
    modifier: Modifier = Modifier,
    text: String? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                BasicUtils.getColor(
                    LocalContext.current,
                    R.color.light_square
                )
            ),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(45.dp),
                    color = BasicUtils.getColor(LocalContext.current, R.color.light_square),
                    trackColor = BasicUtils.getColor(LocalContext.current, R.color.dark_square),
                    strokeWidth = 5.dp
                )
                if (text != null) {
                    Text(
                        text = text,
                        modifier = Modifier.padding(top = 20.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = BasicUtils.getColor(LocalContext.current, R.color.text_dark_square)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun CircularProgressPreview() {
    CircularProgress()
}