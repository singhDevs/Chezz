package com.singhDevs.chezz.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.singhDevs.chezz.R
import com.singhDevs.chezz.activities.ProfileActivity
import com.singhDevs.chezz.models.Ratings
import com.singhDevs.chezz.network.User
import java.text.SimpleDateFormat
import java.util.*

object ChezzAppTheme {
    val DarkerBackground = Color(0xFF121212)
    val DarkBackground = Color(0xFF1A1A1A)
    val FieldBackground = Color(0xFF2A2A2A)
    val PrimaryAmber = Color(0xFFBF8F3F)
    val AccentBrown = Color(0xFF8B5A2B)
    val TextColor = Color(0xFFE0E0E0)

    val redColor = Color(0xFFE57373) // Reddish
    val greenColor = Color(0xFF81C784) // Greenish
    val blueColor = Color(0xFF64B5F6) // Bluish
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    user: User,
    context: ProfileActivity
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ChezzAppTheme.DarkerBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp, vertical = 8.dp)
        ) {
            // Top Bar
            TopAppBar(context)

            Spacer(modifier = Modifier.height(16.dp))

            // User Profile Header
            ProfileHeader(user)

            Spacer(modifier = Modifier.height(24.dp))

            // Ratings Section
            RatingsSection(user)

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Section
            StatsSection(user)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(context: ProfileActivity) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Player Profile",
                color = ChezzAppTheme.TextColor,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                context.finish()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = ChezzAppTheme.PrimaryAmber
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun ProfileHeader(user: User) {
    val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(user.createdAt)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Profile Image with Gradient Border
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            // Gradient circle behind the image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ChezzAppTheme.PrimaryAmber,
                                ChezzAppTheme.AccentBrown
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Profile image
            AsyncImage(
                model = user.photoUrl,
                error = painterResource(R.drawable.pfp_unavailable),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(ChezzAppTheme.DarkBackground)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user.username,
            color = ChezzAppTheme.TextColor,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "on Chezz since $formattedDate",
            color = ChezzAppTheme.PrimaryAmber,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun RatingsSection(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ChezzAppTheme.DarkBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Chezz Ratings",
                color = ChezzAppTheme.TextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RatingItem(
                    title = "Bullet",
                    rating = user.ratings.bulletRating,
                    color = ChezzAppTheme.redColor,
                    imgDrawable = R.drawable.ic_bullet,
                    modifier = Modifier.weight(1f)
                )

//                Spacer(modifier = Modifier.width(8.dp))

                RatingItem(
                    title = "Blitz",
                    rating = user.ratings.blitzRating,
                    color = ChezzAppTheme.greenColor,
                    imgDrawable = R.drawable.ic_blitz,
                    modifier = Modifier.weight(1f)
                )

//                Spacer(modifier = Modifier.width(8.dp))

                RatingItem(
                    title = "Rapid",
                    rating = user.ratings.rapidRating,
                    color = ChezzAppTheme.blueColor,
                    imgDrawable = R.drawable.ic_rapid,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RatingItem(
    title: String,
    rating: Int,
    imgDrawable: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .background(
                color = ChezzAppTheme.FieldBackground,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(imgDrawable),
            modifier = Modifier
                .size(50.dp)
                .alpha(0.05f),
            contentDescription = null
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                color = ChezzAppTheme.TextColor.copy(alpha = 0.8f),
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rating.toString(),
                color = color,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
fun StatsSection(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ChezzAppTheme.DarkBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Game Statistics",
                color = ChezzAppTheme.TextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar for win rate
            val winRate = user.totalWins.toFloat() / user.totalGames.toFloat()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.width(80.dp),
                    text = "Win Rate",
                    color = ChezzAppTheme.TextColor,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .background(
                            color = ChezzAppTheme.FieldBackground,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(winRate)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        ChezzAppTheme.PrimaryAmber,
                                        ChezzAppTheme.AccentBrown
                                    )
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }

                Text(
                    text = "${(winRate * 100).toInt()}%",
                    color = ChezzAppTheme.PrimaryAmber,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Game stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    title = "Games",
                    value = user.totalGames.toString(),
                    bgColor = ChezzAppTheme.FieldBackground,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                StatItem(
                    title = "Wins",
                    value = user.totalWins.toString(),
                    bgColor = ChezzAppTheme.greenColor,
                    bgAlpha = 0.65f,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                StatItem(
                    title = "Losses",
                    value = user.totalLosses.toString(),
                    bgColor = ChezzAppTheme.redColor,
                    bgAlpha = 0.65f,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                StatItem(
                    title = "Draws",
                    value = user.totalDraws.toString(),
                    bgColor = ChezzAppTheme.blueColor,
                    bgAlpha = 0.65f,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    bgColor: Color,
    bgAlpha: Float = 1f,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(
                color = bgColor.copy(alpha = bgAlpha),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(
            text = title,
            color = ChezzAppTheme.TextColor.copy(alpha = 0.8f),
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            color = ChezzAppTheme.TextColor,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
fun ProfilePreview() {
    val sampleUser = User(
        username = "GrandMaster42",
        photoUrl = "https://example.com/profile.jpg",
        createdAt = Date(),
        ratings = Ratings(),
        id = "1",
        email = "aff@as.com",
        totalGames = 11,
        totalWins = 6,
        totalLosses = 2,
        totalDraws = 3,
        totalTimePlayed = 50,
    )

    ProfileScreen(
        context = ProfileActivity(),
        modifier = Modifier,
        user = sampleUser
    )
}