package com.singhDevs.chezz.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.utils.Constants
import kotlinx.coroutines.delay

@Composable
fun RatingDisplay(
    gameType: GameType,
    newRating: Int,
    oldRating: Int,
    modifier: Modifier = Modifier
) {
    val ratingDifference = newRating - oldRating
    val isRatingIncreased = ratingDifference > 0

    // Colors
    val positiveColor = Color(0xFF4CAF50)
    val negativeColor = Color(0xFFF44336)
    val neutralColor = Color(0xFFFFAB00)

    // Animation states
    var showRatingChange by remember { mutableStateOf(false) }
    var pulseAnimationStarted by remember { mutableStateOf(false) }

    // Rating glow animation
    val pulseAnimation by animateFloatAsState(
        targetValue = if (pulseAnimationStarted) 1f else 0f,
        // Using a simple ease-in-out instead of complex curves
        animationSpec = tween(750, easing = EaseInOutCubic)
    )

    // Start animations after composition
    LaunchedEffect(key1 = true) {
        delay(300)
        pulseAnimationStarted = true
        delay(200)
        showRatingChange = true
    }

    // Rating display with animation
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isRatingIncreased -> positiveColor.copy(alpha = 0.08f + (0.12f * pulseAnimation))
                    ratingDifference < 0 -> negativeColor.copy(alpha = 0.08f + (0.12f * pulseAnimation))
                    else -> neutralColor.copy(alpha = 0.08f)
                }
            )
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (gameType) {
                    GameType.RAPID -> "RAPID RATING"
                    GameType.BLITZ -> "BLITZ RATING"
                    GameType.BULLET -> "BULLET RATING"
                },
                style = MaterialTheme.typography.titleSmall,
                color = colorResource(R.color.text_secondary),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Rating number
                Text(
                    text = newRating.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    color =
                    if(ratingDifference != 0){
                        if(isRatingIncreased) colorResource(R.color.bg_casual)
                        else colorResource(R.color.bg_rated)
                    }
                    else Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.animateContentSize(
                        // Use standard ease instead of custom curve
                        animationSpec = tween(300, easing = EaseOutCubic)
                    )
                )

                // Rating change
                AnimatedVisibility(
                    visible = showRatingChange,
                    // Simple fade + slide without custom easing
                    enter = fadeIn(tween(500)) + slideInHorizontally(
                        animationSpec = tween(500, easing = EaseOutCubic)
                    ) { it / 2 }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .offset(y = (-2).dp)
                    ) {
                        if (ratingDifference != 0) {
                            Icon(
                                imageVector = if (isRatingIncreased) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (isRatingIncreased) "Increased" else "Decreased",
                                tint = if (isRatingIncreased) positiveColor else negativeColor,
                                modifier = Modifier.size(25.dp)
                            )

                            Text(
                                text = "${if (isRatingIncreased) "+" else ""}$ratingDifference",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isRatingIncreased) positiveColor else negativeColor
                            )
                        }
                    }
                }
            }
        }
    }
}

// Usage example:
@Preview
@Composable
fun GameOverScreen() {
    val gameType = GameType.RAPID
    val newRating = when (gameType) {
        GameType.BULLET -> Constants.user.ratings.bulletRating
        GameType.BLITZ -> Constants.user.ratings.blitzRating
        GameType.RAPID -> Constants.user.ratings.rapidRating
    }

    // Retrieve old rating from your data source
    val oldRating = remember {
        // This should be retrieved from where you store the previous rating
        // For example from SharedPreferences or your backend
        when (gameType) {
            GameType.BULLET -> 1500
            GameType.BLITZ -> 1500
            GameType.RAPID -> 1500
        }
    }

    RatingDisplay(
        gameType = gameType,
        newRating = newRating,
        oldRating = oldRating,
        modifier = Modifier.fillMaxWidth()
    )
}