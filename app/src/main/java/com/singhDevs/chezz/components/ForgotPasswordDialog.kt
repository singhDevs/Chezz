package com.singhDevs.chezz.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.SignInActivity
import com.singhDevs.chezz.models.ResultType

@Composable
fun ForgotPasswordDialog(
    modifier: Modifier = Modifier,
    context: Context,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(15.dp)
        ) {
            Box(
                modifier = Modifier.background(colorResource(R.color.surface))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp, 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Light),
                            color = colorResource(R.color.primary_amber)
                        )

                        CloseButton(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .size(30.dp)
                                .align(Alignment.TopEnd),
                            onDismiss = onDismiss
                        )
                    }

                    Text(
                        text = "No worries. Just contact us and we’ll help you recover your account.\n\nPlease email us with the subject: \"Password Reset Request\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 20.dp)
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .background(colorResource(R.color.mail_btn), RoundedCornerShape(percent = 15))
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:hey.singhdevs@gmail.com?subject=Password%20Reset%20Request".toUri()
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Rounded.Email,
                                contentDescription = null,
                                modifier = Modifier.padding(5.dp),
                                tint = colorResource(R.color.primary_amber)
                            )

                            Text(
                                text = "hey.singhdevs@gmail.com",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = TextDecoration.Underline
                                ),
                                color = colorResource(R.color.primary_amber),
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showSystemUi = true)
//@Composable
//private fun ForgotPasswordDialogPreview() {
//    ForgotPasswordDialog(Modifier, SignInActivity()) { }
//}