package com.singhDevs.chezz.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.singhDevs.chezz.R

@Composable
fun PlayerDisplayTab(
    username: String,
    photoUrl: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = photoUrl,
            error = painterResource(R.drawable.pfp_unavailable),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(15.dp))
        )
        Text(
            text = username,
            fontSize = 20.sp,
            modifier = Modifier.padding(10.dp, 0.dp),
            color = Color.White
        )
    }

}

@Preview
@Composable
private fun PlayerDisplayTabPreview() {
    PlayerDisplayTab(
        username = "Plutamite",
        photoUrl = "https://hypixel.net/attachments/face-png.2475043/"
    )
}