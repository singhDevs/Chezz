package com.singhDevs.chezz.components

import android.graphics.Color.alpha
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.singhDevs.chezz.R

@Composable
fun PlayerDisplayTab(
    username: String,
    photoUrl: String,
    gameModeIcon: Int? = null,
    rating: Int? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = photoUrl,
            error = painterResource(R.drawable.pfp_unavailable),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(15.dp))
        )
        Column(
            modifier = Modifier.padding(start = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = username,
                fontSize = 20.sp,
                modifier = Modifier,
                color = Color.White
            )

            if (gameModeIcon != null && rating != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(gameModeIcon),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = rating.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

}

@Preview
@Composable
private fun PlayerDisplayTabPreview() {
    PlayerDisplayTab(
        username = "Plutamite",
        photoUrl = "https://hypixel.net/attachments/face-png.2475043/",
        gameModeIcon = R.drawable.ic_blitz,
        rating = 1500
    )
}