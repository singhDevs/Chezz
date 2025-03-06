package com.singhDevs.chezz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import com.singhDevs.chezz.R

@Composable
fun PopupDialog(
    modifier: Modifier = Modifier,
    message: String,
    onClickOutside: () -> Unit,
    negativeButtonAction: () -> Unit,
    positiveButtonAction: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        contentAlignment = Alignment.Center){
        Card(
            modifier = Modifier.padding(30.dp),
            elevation = CardDefaults.elevatedCardElevation(10.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Popup(
                onDismissRequest = onClickOutside,
                alignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(colorResource(R.color.draw_button_color))
                        .clip(RoundedCornerShape(15.dp))
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp),
                        text = message,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace,
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
                                .background(colorResource(R.color.game_buttons_color))
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
                                .background(colorResource(R.color.dark_square))
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
}

@Preview(showSystemUi = true)
@Composable
private fun PopupDialogPreview() {
    PopupDialog(Modifier, "Are you sure you want to resign?", {}, {}) { }
}