package com.singhDevs.chezz.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.singhDevs.chezz.R
import com.singhDevs.chezz.UserRatingsOuterClass.UserRatings
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.network.User

@Composable
fun HomeProfileComposable(
    user: User,
    ratings: UserRatings,
    ratingToShow: GameType,
    shouldShowRating: Boolean
) {
    val primaryAmber = Color(0xFFBF8F3F)       // Gold/amber color from knight's accents
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .background(colorResource(R.color.background)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(percent = 20)),
            model = user.photoUrl,
            error = painterResource(R.drawable.pfp_unavailable),
            contentDescription = null
        )
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = user.username,
                style = MaterialTheme.typography.titleMedium,
                color = primaryAmber
            )
            AnimatedContent(
                modifier = Modifier.padding(top = 3.dp),
                targetState = shouldShowRating,
                transitionSpec = {
                    val slideIn = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeIn(animationSpec = tween(durationMillis = 300))

                    val slideOut = slideOutVertically(
                        targetOffsetY = { fullHeight -> -fullHeight },
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeOut(animationSpec = tween(durationMillis = 300))

                    slideIn togetherWith slideOut
                },
                label = ""
            ) { shouldShowRating ->
                if (shouldShowRating) {
                    AnimatedContent(
                        targetState = ratingToShow,
                        transitionSpec = {
                            val slideIn = slideInVertically(
                                initialOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeIn(animationSpec = tween(durationMillis = 300))

                            val slideOut = slideOutVertically(
                                targetOffsetY = { fullHeight -> -fullHeight },
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeOut(animationSpec = tween(durationMillis = 300))

                            slideIn togetherWith slideOut
                        },
                        label = ""
                    ) { ratingToShow ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            when (ratingToShow) {
                                GameType.BULLET -> {
                                    Image(
                                        modifier = Modifier.size(18.dp),
                                        painter = painterResource(id = R.drawable.ic_bullet),
                                        contentDescription = null
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 5.dp),
                                        text = "Bullet rating: ${ratings.bulletRating}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.LightGray
                                    )
                                }
                                GameType.BLITZ -> {
                                    Image(
                                        modifier = Modifier.size(18.dp),
                                        painter = painterResource(id = R.drawable.ic_blitz),
                                        contentDescription = null
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 5.dp),
                                        text = "Blitz rating: ${ratings.blitzRating}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.LightGray
                                    )
                                }
                                GameType.RAPID -> {
                                    Image(
                                        modifier = Modifier.size(18.dp),
                                        painter = painterResource(id = R.drawable.ic_rapid),
                                        contentDescription = null
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 5.dp),
                                        text = "Rapid rating: ${ratings.rapidRating}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Rating is hidden, play casually 😉",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

/*
@Preview(showSystemUi = true)
@Composable
private fun HomeProfileComposablePreview() {
    HomeProfileComposable(
        user = User(
            "1301",
            "hey@ama.com",
            "pluta",
            "",
            Ratings()
        ),
        ratingToShow = GameType.BLITZ,
        shouldShowRating = true
    )
}*/
