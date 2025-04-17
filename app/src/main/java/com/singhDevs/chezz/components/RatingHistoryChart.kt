package com.singhDevs.chezz.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.singhDevs.chezz.R
import com.singhDevs.chezz.models.GameType
import com.singhDevs.chezz.network.RatingHistoryItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round

// Define modern color scheme
private val bulletColor = Color(0xFFF5712A)
private val blitzColor = Color(0xFFFFE825)
private val rapidColor = Color(0xFF2CE133)
private val backgroundColor = Color(0xFF121212) // Dark gray
private val surfaceColor = Color(0xFF1E1E1E) // Chart surface
private val textColor = Color.White
private val gridColor = Color.White.copy(alpha = 0.1f)

@Composable
fun RatingHistoryChart(
    modifier: Modifier = Modifier,
    onModeChange: (GameType) -> Unit = {},
    bulletData: List<RatingHistoryItem> = emptyList(),
    blitzData: List<RatingHistoryItem> = emptyList(),
    rapidData: List<RatingHistoryItem> = emptyList()
) {
    var selectedMode by remember { mutableStateOf(GameType.BULLET) }
    val currentData = when (selectedMode) {
        GameType.BULLET -> bulletData
        GameType.BLITZ -> blitzData
        GameType.RAPID -> rapidData
    }

    val currentColor = when (selectedMode) {
        GameType.BULLET -> bulletColor
        GameType.BLITZ -> blitzColor
        GameType.RAPID -> rapidColor
    }

    // Using an Animatable for smoother animation
    val progressAnimatable = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    var selectedDataPoint by remember { mutableStateOf<Pair<RatingHistoryItem, Offset>?>(null) }

    // Reset and restart animation when mode changes
    LaunchedEffect(selectedMode) {
        progressAnimatable.snapTo(0f) // Reset immediately
        // Short delay to ensure UI updates before animation starts
        kotlinx.coroutines.delay(100)
        // Animate from beginning to end
        progressAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = EaseOutCubic)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(0.dp)
    ) {
        // Mode selector with modern typography and colors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(surfaceColor),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameType.entries.forEach { mode ->
                val isSelected = selectedMode == mode
                Text(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = {
                                if (selectedMode != mode) {
                                    selectedMode = mode
                                    onModeChange(mode)
                                    selectedDataPoint = null
                                    coroutineScope.launch {
                                        progressAnimatable.snapTo(0f)
                                    }
                                }
                            }
                        )
                        .padding(vertical = 15.dp),
                    text = mode.name,
                    color = if (isSelected) when(mode){
                        GameType.BULLET -> bulletColor
                        GameType.BLITZ -> blitzColor
                        GameType.RAPID -> rapidColor
                    }
                    else textColor.copy(alpha = 0.6f),
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Chart container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(surfaceColor)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentData.isEmpty()) {
                // Enhanced empty state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.sleeping_lottie)
                    )
                    LottieAnimation(
                        modifier = Modifier
                            .size(150.dp)
                            .background(surfaceColor),
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )
                    Text(
                        text = "No data available",
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 0.dp)
                    )
                    Text(
                        text = "Play games to see your ${selectedMode.name.lowercase(Locale.ROOT)} rating history",
                        color = textColor.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                ChartWithAnimation(
                    data = currentData,
                    color = currentColor,
                    progress = progressAnimatable.value,
                    selectedDataPoint = selectedDataPoint,
                    onDataPointSelected = { point, offset ->
                        selectedDataPoint = if (point != null && offset != null) {
                            Pair(point, offset)
                        } else {
                            null
                        }
                    }
                )

                // Only show tooltip when we have valid hover data
                selectedDataPoint?.let { (dataPoint, position) ->
                    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
                    val formattedDate = dateFormat.format(dataPoint.createdAt)

                    TooltipOverlay(
                        position = position,
                        date = formattedDate,
                        rating = dataPoint.rating,
                        color = currentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stats row
        if (currentData.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    title = "Current",
                    value = currentData.lastOrNull()?.rating?.toString() ?: "-",
                    color = currentColor
                )
                val peak = currentData.maxByOrNull { it.rating }?.rating
                StatItem(
                    title = "Peak",
                    value = peak?.toString() ?: "-",
                    color = currentColor
                )
                val initialRating = currentData.firstOrNull()?.rating ?: 0
                val currentRating = currentData.lastOrNull()?.rating ?: 0
                val change = currentRating - initialRating
                val changeText = when {
                    change > 0 -> "+$change"
                    change < 0 -> "$change"
                    else -> "0"
                }
                StatItem(
                    title = "Change",
                    value = changeText,
                    color = when {
                        change > 0 -> Color(0xFF4CAF50)
                        change < 0 -> Color(0xFFF44336)
                        else -> textColor.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}

@Composable
fun ChartWithAnimation(
    data: List<RatingHistoryItem>,
    color: Color,
    progress: Float,
    selectedDataPoint: Pair<RatingHistoryItem, Offset>?,
    onDataPointSelected: (RatingHistoryItem?, Offset?) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()

    // Define chart margins
    val leftMargin = 50f
    val rightMargin = 20f
    val topMargin = 30f
    val bottomMargin = 50f

    // Calculate min and max ratings with padding
    val minRating = data.minByOrNull { it.rating }?.rating?.toFloat() ?: 800f
    val maxRating = data.maxByOrNull { it.rating }?.rating?.toFloat() ?: 1200f
    val ratingRange = maxRating - minRating
    val paddedMin = minRating - (ratingRange * 0.1f).coerceAtLeast(50f)
    val paddedMax = maxRating + (ratingRange * 0.1f).coerceAtLeast(50f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectDragGestures(
                    onDragStart = { offset ->
                        findNearestDataPoint(
                            touchX = offset.x,
                            data = data,
                            chartWidth = size.width - leftMargin - rightMargin,
                            leftMargin = leftMargin,
                            chartHeight = size.height - topMargin - bottomMargin,
                            topMargin = topMargin,
                            paddedMin = paddedMin,
                            paddedMax = paddedMax
                        )?.let { (item, point) ->
                            onDataPointSelected(item, point)
                        } ?: onDataPointSelected(null, null)
                    },
                    onDrag = { change, _ ->
                        findNearestDataPoint(
                            touchX = change.position.x,
                            data = data,
                            chartWidth = size.width - leftMargin - rightMargin,
                            leftMargin = leftMargin,
                            chartHeight = size.height - topMargin - bottomMargin,
                            topMargin = topMargin,
                            paddedMin = paddedMin,
                            paddedMax = paddedMax
                        )?.let { (item, point) ->
                            onDataPointSelected(item, point)
                        } ?: onDataPointSelected(null, null)
                    },
                    onDragEnd = { /* Keep the last selected point */ },
                    onDragCancel = { /* Keep the last selected point */ }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val chartWidth = width - leftMargin - rightMargin
            val chartHeight = height - topMargin - bottomMargin

            // Draw horizontal grid lines and y-axis labels
            val horizontalLinesCount = 5
            for (i in 0..horizontalLinesCount) {
                val y = topMargin + chartHeight - (chartHeight * i / horizontalLinesCount)

                // Draw grid line
                drawLine(
                    color = gridColor,
                    start = Offset(leftMargin, y),
                    end = Offset(width - rightMargin, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                )

                // Draw y-axis label
                val ratingValue = paddedMin + (paddedMax - paddedMin) * i / horizontalLinesCount
                drawText(
                    textMeasurer = textMeasurer,
                    text = round(ratingValue).toInt().toString(),
                    topLeft = Offset(12f, y - 10f),
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                )
            }

            // Draw vertical grid lines
            val verticalLinesCount = min(6, data.size - 1)
            if (verticalLinesCount > 0) {
                for (i in 0..verticalLinesCount) {
                    val x = leftMargin + (chartWidth * i / verticalLinesCount)
                    drawLine(
                        color = gridColor,
                        start = Offset(x, topMargin),
                        end = Offset(x, height - bottomMargin),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                    )
                }
            }

            if (data.size > 1) {
                // Calculate visible points based on progress
                val visiblePointCount = (progress * data.size).toInt().coerceAtLeast(1)
                val visibleData = data.take(visiblePointCount)

                if (visibleData.isNotEmpty()) {
                    // Create paths for line and fill
                    val linePath = Path()
                    val fillPath = Path()
                    val points = mutableListOf<Offset>()

                    // Process visible points
                    visibleData.forEachIndexed { index, point ->
                        val normalizedX = index.toFloat() / (data.size - 1)
                        val x = leftMargin + normalizedX * chartWidth
                        val normalizedY = (point.rating - paddedMin) / (paddedMax - paddedMin)
                        val y = topMargin + chartHeight - (normalizedY * chartHeight)
                        val currentPoint = Offset(x, y)
                        points.add(currentPoint)

                        if (index == 0) {
                            linePath.moveTo(x, y)
                            fillPath.moveTo(x, height - bottomMargin)
                            fillPath.lineTo(x, y)
                        } else {
                            linePath.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }
                    }

                    // Handle partial animation progress
                    if (progress < 1f && visiblePointCount < data.size) {
                        val lastIndex = visiblePointCount - 1
                        val nextIndex = lastIndex + 1

                        if (nextIndex < data.size) {
                            val lastPoint = points.last()
                            val progressInSegment = progress * data.size - lastIndex

                            val nextNormalizedX = nextIndex.toFloat() / (data.size - 1)
                            val nextX = leftMargin + nextNormalizedX * chartWidth
                            val nextNormalizedY = (data[nextIndex].rating - paddedMin) / (paddedMax - paddedMin)
                            val nextY = topMargin + chartHeight - (nextNormalizedY * chartHeight)

                            // Interpolate between last point and next point
                            val partialX = lastPoint.x + (nextX - lastPoint.x) * progressInSegment
                            val partialY = lastPoint.y + (nextY - lastPoint.y) * progressInSegment

                            linePath.lineTo(partialX, partialY)
                            fillPath.lineTo(partialX, partialY)
                            points.add(Offset(partialX, partialY))
                        }
                    }

                    // Close the fill path
                    fillPath.lineTo(points.last().x, height - bottomMargin)
                    fillPath.close()

                    // Draw fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.2f),
                                color.copy(alpha = 0.0f)
                            ),
                            startY = topMargin,
                            endY = height - bottomMargin
                        )
                    )

                    // Draw line
                    drawPath(
                        path = linePath,
                        color = color,
                        style = Stroke(
                            width = 3f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Draw data points and labels
                    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    points.forEachIndexed { index, point ->
                        if (index < visibleData.size) {
                            // Draw point circle
                            drawCircle(
                                color = Color.Black,
                                radius = 5f,
                                center = point
                            )
                            drawCircle(
                                color = color,
                                radius = 3.5f,
                                center = point
                            )

                            // Draw labels at appropriate intervals
                            if (index == 0 || index == visibleData.size - 1 ||
                                index % (maxOf(1, visibleData.size / 5)) == 0) {
                                val formattedDate = dateFormat.format(visibleData[index].createdAt)
                                val textLayoutResult = textMeasurer.measure(
                                    text = formattedDate,
                                    style = TextStyle(fontSize = 10.sp)
                                )
                                val textWidth = textLayoutResult.size.width.toFloat()
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = formattedDate,
                                    topLeft = Offset(point.x - textWidth / 2, height - 30f),
                                    style = TextStyle(
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Draw hover highlight
            selectedDataPoint?.let { (_, position) ->
                // Draw vertical guide line
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(position.x, topMargin),
                    end = Offset(position.x, height - bottomMargin),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                )

                // Highlight selected point
                drawCircle(
                    color = color,
                    radius = 8f,
                    center = position,
                    style = Stroke(width = 2f)
                )
                drawCircle(
                    color = color,
                    radius = 4f,
                    center = position
                )
            }
        }
    }
}

@Composable
fun TooltipOverlay(
    position: Offset,
    date: String,
    rating: Int,
    color: Color
) {
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .offset(
                x = (position.x / density.density - 120).dp,
                y = (position.y / density.density - 100).dp
            )
            .zIndex(10f)
            .background(Color(0xFF333333), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = rating.toString(),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = date,
                color = textColor.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatItem(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = textColor.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// Helper function to find the nearest data point to a touch position
private fun findNearestDataPoint(
    touchX: Float,
    data: List<RatingHistoryItem>,
    chartWidth: Float,
    leftMargin: Float,
    chartHeight: Float,
    topMargin: Float,
    paddedMin: Float,
    paddedMax: Float
): Pair<RatingHistoryItem, Offset>? {
    if (data.isEmpty()) return null

    // Find the data point closest to touch X position
    var nearestItem: RatingHistoryItem? = null
    var nearestDistance = Float.MAX_VALUE
    var nearestOffset: Offset? = null

    data.forEachIndexed { index, item ->
        val normalizedX = index.toFloat() / (data.size - 1)
        val x = leftMargin + normalizedX * chartWidth
        val distance = abs(x - touchX)

        if (distance < nearestDistance) {
            nearestDistance = distance
            nearestItem = item

            // Calculate corresponding Y position
            val normalizedY = (item.rating - paddedMin) / (paddedMax - paddedMin)
            val y = topMargin + chartHeight - (normalizedY * chartHeight)
            nearestOffset = Offset(x, y)
        }
    }

    return if (nearestItem != null) {
        Pair(nearestItem!!, nearestOffset!!)
    } else null
}

@Preview
@Composable
fun ChessRatingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "Your Performance",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        RatingHistoryChart(modifier = Modifier.fillMaxWidth())
    }
}