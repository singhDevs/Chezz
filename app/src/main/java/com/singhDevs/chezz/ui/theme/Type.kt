package com.singhDevs.chezz.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val ChezzTypography = Typography(
    /**
     * For Light Weight Headings,
     * HomeActivity Recent Games Title,
     * HomeActivity Overall Performance,
     * [ProfileScreen username](FontWeight.Bold),
     * GameHistoryComposable Recent Games Title
     */
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 26.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.5.sp
    ),

    /**
     * Profile username,
     * HomeProfileComposable username,
     * Play button, [Play Button time](FontWeight.Light),
     * ProfileScreen RatingItem Rating
     * ProfileScreen StatItem Value,
     * [TimeDurationTypesComposable How much time](FontWeight.Light)
     * [GameModeComposable Rated or Casual?](FontWeight.Light)
     */
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),

    /**
     * [Profile subtext joined on](FontWeight.Light),
     * HomeProfileComposable rating,
     * [currently online](FontFamily.Monospace, FontWeight.Normal),
     * PlayerDisplayTab rating,
     * RatingDisplay Rating title,
     * ProfileScreen on Chezz since,
     * ProfileScreen RatingItem Title,
     * ProfileScreen StatItem Title
     * [ProfileScreen Win Rate](FontWeight.Normal),
     * [ProfileScreen Win Rate %](FontWeight.Black),
     * [Rating History Chart - Play games to see your rating chart](FontWeight.Normal)
     */
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),

    /**
     * ResultDialog You Won!
     */
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
    ),

    /**
     * GameActivity Finding opponent,
     * HomeActivity Signing out,
     * ProfileActivity Loading Player profile
     */
    headlineMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    ),

    /**
     * [ProfileActivity TopAppBar Profile text](FontWeight.Bold)
     * [GameHistoryActivity TopAppBar Recent Games text](FontWeight.Bold)
     * GameHistoryComposable RecentGames username
     * Rating History Chart - No data available
     */
    displayMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = FontFamily.Default
    ),

    /**
     * [ProfileDialog Sign Out](FontWeight.Bold),
     * [ProfileDialog Options text](FontWeight.Normal),
     * [HomeActivity Recent games username](FontWeight.Normal),
     * GameActivity Moves
     * GameActivity Game moves are shown here,
     * [PlayerDisplayTab username](FontWeight.Normal),
     * TimerComposable
     * [MovesListComposable Index](FontWeight.Normal)
     * [MovesListComposable Move text](FontWeight.Normal)
     * GameActivity Resign
     * GameActivity Draw
     * ResultDialog by Resignation
     * [ResultDialog username](FontWeight.Normal),
     * [ResultDialog rating change](FontWeight.SemiBold),
     * [ResultDialog all buttons](FontWeight.Normal),
     * [ResignDialog message](FontWeight.Normal)
     * DrawDialog Draw?,
     * [TimeDurationTypesComposable All time durations](FontWeight.Bold)
     * [GameModeComposable All game modes](FontWeight.Bold)
     */
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    ),
)