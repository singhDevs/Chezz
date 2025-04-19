package com.singhDevs.chezz.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.GameHistoryActivity
import com.singhDevs.chezz.activities.ProfileActivity
import com.singhDevs.chezz.models.Game
import com.singhDevs.chezz.models.Ratings
import com.singhDevs.chezz.network.User
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlayerProfileDialog(
    context: Context,
    user: User,
    token: String,
    games: List<Game>?,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit
) {
    // App color scheme
    val darkBackground = Color(0xFF1A1A1A)
    val darkerBackground = Color(0xFF121212)
    val textColor = Color(0xFFE0E0E0)
    val primaryAmber = Color(0xFFBF8F3F)
    val fieldBackground = Color(0xFF2A2A2A)

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            darkerBackground,
            darkBackground
        )
    )

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
                .background(gradientBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CloseButton(onDismiss = onDismiss)

                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(fieldBackground)
                        .border(3.dp, primaryAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = user.photoUrl,
                        error = painterResource(R.drawable.pfp_unavailable),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(
                                3.5.dp,
                                primaryAmber,
                                CircleShape
                            )
                    )
                }

                // Player name and info
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    modifier = Modifier.padding(top = 6.dp)
                )

                val dateFormat = SimpleDateFormat("d MMMM, yyyy", Locale.getDefault())
                Text(
                    text = "Joined on: ${dateFormat.format(user.createdAt)}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Light),
                    color = primaryAmber,
                    modifier = Modifier.padding(top = 5.dp, bottom = 16.dp)
                )

                HorizontalDivider(
                    color = fieldBackground,
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Menu options
                ProfileMenuItem(
                    icon = Icons.Outlined.Person,
                    text = "View your profile",
                    textColor = textColor,
                    iconTint = primaryAmber,
                    onClick = {
                        val intent = Intent(context, ProfileActivity::class.java)
                        intent.putExtra("userId", user.id)
                        context.startActivity(intent)
                    }
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Search,
                    text = "Games History",
                    textColor = textColor,
                    iconTint = primaryAmber,
                    onClick = {
                        val intent = Intent(context, GameHistoryActivity::class.java)
                        intent.putExtra("user", user)
                        intent.putExtra("token", token)
                        intent.putExtra("gamesList", games as Serializable)
                        context.startActivity(intent)
                    }
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Share,
                    text = "Share Profile",
                    textColor = textColor,
                    iconTint = primaryAmber,
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, generateProfileLink(user.id))
                            type = "text/plain"
                        }
                        context.startActivity(
                            Intent.createChooser(
                                shareIntent,
                                "Share Profile Via"
                            )
                        )
                    }
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Build,
                    text = "Contact Developer",
                    textColor = textColor,
                    iconTint = primaryAmber,
                    onClick = { openXProfile(context) }
                )

                HorizontalDivider(
                    color = fieldBackground,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Sign out button
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = onSignOut,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.sign_out_btn_color)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = textColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = "SIGN OUT",
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }

                // App version
                Text(
                    text = "Chezz  v1.2.4",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

fun generateProfileLink(userId: String) = "https://singhDevs.github.io/chezz/profile?userId=$userId"

fun openXProfile(context: Context) {
    val appUri = "twitter://user?screen_name=guranshSinghh".toUri()
    val appIntent = Intent(Intent.ACTION_VIEW, appUri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    // Web URI fallback
    val webUri = "https://x.com/guranshSinghh".toUri()
    val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    if (appIntent.resolveActivity(context.packageManager) != null)
        context.startActivity(appIntent)
    else
        context.startActivity(webIntent)

}


@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    textColor: Color,
    iconTint: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
fun HomeScreenWithProfileButton() {
    var showProfileDialog by remember { mutableStateOf(true) }

    // Your home screen UI
    Box(modifier = Modifier.fillMaxSize()) {
        // Top app bar with profile button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { showProfileDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color(0xFFBF8F3F)
                )
            }
        }

        if (showProfileDialog) {
            PlayerProfileDialog(
                context = LocalContext.current,
                user = User(
                    id = "124",
                    email = "a@a.com",
                    username = "plutamite",
                    photoUrl = "",
                    createdAt = Date(),
                    ratings = Ratings()
                ),
                token = "",
                games = emptyList(),
                onDismiss = { showProfileDialog = false },
                onSignOut = {
                    // Handle sign out
                    showProfileDialog = false
                }
            )
        }
    }
}