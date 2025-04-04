package com.singhDevs.chezz.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.round

data class RatingPoint(
    val date: Date,
    val rating: Int
)

enum class ChessTimeControl {
    BULLET, BLITZ, RAPID
}

@Composable
fun RatingHistoryChart(
    modifier: Modifier = Modifier,
    onModeChange: (ChessTimeControl) -> Unit = {}
) {
    // Sample data - replace with your actual data source
    val bulletData = remember { getSampleBulletData() }
    val blitzData = remember { getSampleBlitzData() }
    val rapidData = remember { getSampleRapidData() }

    var selectedMode by remember { mutableStateOf(ChessTimeControl.RAPID) }
    val currentData = when (selectedMode) {
        ChessTimeControl.BULLET -> bulletData
        ChessTimeControl.BLITZ -> blitzData
        ChessTimeControl.RAPID -> rapidData
    }

    // Colors for different modes
    val bulletColor = Color(0xFFE53935)  // Red
    val blitzColor = Color(0xFF7E57C2)   // Purple
    val rapidColor = Color(0xFF2196F3)   // Blue

    val currentColor = when (selectedMode) {
        ChessTimeControl.BULLET -> bulletColor
        ChessTimeControl.BLITZ -> blitzColor
        ChessTimeControl.RAPID -> rapidColor
    }

    // Animations
    val coroutineScope = rememberCoroutineScope()
    var animationProgress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(1200, easing = EaseOutCubic)
    )

    // Track hover position
    var hoverPoint by remember { mutableStateOf<Offset?>(null) }
    var selectedDataPoint by remember { mutableStateOf<RatingPoint?>(null) }
    var isHovering by remember { mutableStateOf(false) }

    // Reset and trigger animation when mode changes
    LaunchedEffect(selectedMode) {
        animationProgress = 0f
        delay(300)
        animationProgress = 1f
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        /*Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF333333)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {*/
        ChessTimeControl.values().forEach { mode ->
            val isSelected = selectedMode == mode
            val buttonColor = when (mode) {
                ChessTimeControl.BULLET -> if (isSelected) Color(0xFFE53935) else Color.Transparent
                ChessTimeControl.BLITZ -> if (isSelected) Color(0xFF7E57C2) else Color.Transparent
                ChessTimeControl.RAPID -> if (isSelected) Color(0xFF2196F3) else Color.Transparent
            }

            Text(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = {
                        selectedMode = mode
                        onModeChange(mode)
                        // Reset hover state when changing modes
                        isHovering = false
                        selectedDataPoint = null
                        hoverPoint = null
                    }
                ),
                text = mode.name,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
    // Mode selector
    /* ModeToggleButtons(
         modifier = Modifier,
         selectedMode = selectedMode,
         onModeSelected = { mode ->
             selectedMode = mode
             onModeChange(mode)
             // Reset hover state when changing modes
             isHovering = false
             selectedDataPoint = null
             hoverPoint = null
         }
     )*/
//    }

    Spacer(modifier = Modifier.height(24.dp))

    // Chart container with proper padding for labels
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp) // Increased height to accommodate labels
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2A2A2A))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (currentData.isEmpty()) {
            // No data state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(8.dp)
            ) {
                // Empty grid lines for visual appeal
                EmptyChartGrid()
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Play games to generate chart",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Your ${selectedMode.name.lowercase(Locale.ROOT)} rating history will appear here",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            // Actual chart with data
            ChartWithAnimation(
                data = currentData,
                color = currentColor,
                progress = animatedProgress,
                onHoverPointChanged = { point, dataPoint ->
                    hoverPoint = point
                    selectedDataPoint = dataPoint
                    isHovering = point != null
                }
            )

            // Show tooltip when hovering
            if (isHovering && selectedDataPoint != null && hoverPoint != null) {
                val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
                val formattedDate = dateFormat.format(selectedDataPoint!!.date)

                TooltipOverlay(
                    hoverPoint = hoverPoint!!,
                    date = formattedDate,
                    rating = selectedDataPoint!!.rating,
                    color = currentColor
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Chart legend/stats
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
                    else -> Color.White.copy(alpha = 0.7f)
                }
            )
        }
    }
}


@Composable
fun ModeToggleButtons(
    selectedMode: ChessTimeControl,
    onModeSelected: (ChessTimeControl) -> Unit
) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF333333))
    ) {
        ChessTimeControl.values().forEach { mode ->
            val isSelected = selectedMode == mode
            val buttonColor = when (mode) {
                ChessTimeControl.BULLET -> if (isSelected) Color(0xFFE53935) else Color.Transparent
                ChessTimeControl.BLITZ -> if (isSelected) Color(0xFF7E57C2) else Color.Transparent
                ChessTimeControl.RAPID -> if (isSelected) Color(0xFF2196F3) else Color.Transparent
            }

            Box(
                modifier = Modifier

                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = true),
                        onClick = { onModeSelected(mode) }
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.name.capitalize(),
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun StatItem(title: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun TooltipOverlay(
    hoverPoint: Offset,
    date: String,
    rating: Int,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Draw tooltip
        Box(
            modifier = Modifier
                .offset(
                    x = (hoverPoint.x - 50).dp,
                    y = (hoverPoint.y - 50).dp
                )
                .background(Color(0xFF333333), RoundedCornerShape(8.dp))
                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = rating.toString(),
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = date,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ChartWithAnimation(
    data: List<RatingPoint>,
    color: Color,
    progress: Float,
    onHoverPointChanged: (Offset?, RatingPoint?) -> Unit
) {
    val density = LocalDensity.current

    // Find min and max for scaling
    val minRating = data.minByOrNull { it.rating }?.rating?.toFloat() ?: 800f
    val maxRating = data.maxByOrNull { it.rating }?.rating?.toFloat() ?: 1200f
    val ratingRange = maxRating - minRating
    val paddedMin = minRating - (ratingRange * 0.1f)
    val paddedMax = maxRating + (ratingRange * 0.1f)

    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    // Calculate chart dimensions with proper margins for labels
    val leftMargin = 50f  // Space for y-axis labels
    val rightMargin = 20f
    val topMargin = 30f
    val bottomMargin = 50f // Space for x-axis labels

    // Pre-calculate points for interaction
    val points = remember(data, paddedMin, paddedMax) {
        if (data.size > 1) {
            data.mapIndexed { index, point ->
                val ratio = index.toFloat() / (data.size - 1)
                val normalizedY = (point.rating - paddedMin) / (paddedMax - paddedMin)
                Triple(point, ratio, normalizedY)
            }
        } else emptyList()
    }

    // State for tracking touch interaction
    var interactionState by remember { mutableStateOf<InteractionState?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectDragGestures(
                    onDragStart = { offset ->
                        interactionState = InteractionState.Dragging
                        findNearestPoint(
                            touchX = offset.x,
                            points = points,
                            chartWidth = size.width - leftMargin - rightMargin,
                            leftMargin = leftMargin,
                            chartHeight = size.height - topMargin - bottomMargin,
                            topMargin = topMargin,
                            onPointFound = { point, offset ->
                                onHoverPointChanged(offset, point)
                            }
                        )
                    },
                    onDragEnd = {
                        interactionState = null
                        onHoverPointChanged(null, null)
                    },
                    onDragCancel = {
                        interactionState = null
                        onHoverPointChanged(null, null)
                    },
                    onDrag = { change, _ ->
                        if (interactionState == InteractionState.Dragging) {
                            findNearestPoint(
                                touchX = change.position.x,
                                points = points,
                                chartWidth = size.width - leftMargin - rightMargin,
                                leftMargin = leftMargin,
                                chartHeight = size.height - topMargin - bottomMargin,
                                topMargin = topMargin,
                                onPointFound = { point, offset ->
                                    onHoverPointChanged(offset, point)
                                }
                            )
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val chartWidth = width - leftMargin - rightMargin
            val chartHeight = height - topMargin - bottomMargin

            // Draw grid lines
            val gridColor = Color.White.copy(alpha = 0.1f)
            val gridStroke =
                Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)))

            // Draw horizontal grid lines and y-axis labels
            val horizontalLinesCount = 5
            for (i in 0..horizontalLinesCount) {
                val y = topMargin + chartHeight - (chartHeight * i / horizontalLinesCount)

                // Grid line
                drawLine(
                    color = gridColor,
                    start = Offset(leftMargin, y),
                    end = Offset(width - rightMargin, y),
                    strokeWidth = 1f,
                    pathEffect = gridStroke.pathEffect
                )

                // Y-axis label
                val ratingValue = paddedMin + (paddedMax - paddedMin) * i / horizontalLinesCount
                drawContext.canvas.nativeCanvas.drawText(
                    round(ratingValue).toInt().toString(),
                    12f, // Adjusted position
                    y + 5f,
                    android.graphics.Paint().apply {
                        textSize = with(density) { 10.sp.toPx() }
//                        color = android.graphics.Color.WHITE
                        alpha = 180
                    }
                )
            }

            // Draw vertical grid lines
            val verticalLinesCount = minOf(6, data.size - 1)
            if (verticalLinesCount > 0) {
                for (i in 0..verticalLinesCount) {
                    val x = leftMargin + (chartWidth * i / verticalLinesCount)
                    drawLine(
                        color = gridColor,
                        start = Offset(x, topMargin),
                        end = Offset(x, height - bottomMargin),
                        strokeWidth = 1f,
                        pathEffect = gridStroke.pathEffect
                    )
                }
            }

            // Data points and lines
            if (data.size > 1) {
                // Initialize paths
                val filledPath = Path()
                val linePath = Path()
                val calculatedPoints = mutableListOf<Offset>()

                data.forEachIndexed { index, point ->
                    val ratio = index.toFloat() / (data.size - 1)
                    val x = leftMargin + ratio * chartWidth
                    val normalizedY = (point.rating - paddedMin) / (paddedMax - paddedMin)
                    val y = topMargin + chartHeight - (normalizedY * chartHeight)

                    calculatedPoints.add(Offset(x, y))

                    // Animation starts from left (ratio * progress)
                    if (index == 0) {
                        linePath.moveTo(x, y)
                        filledPath.moveTo(x, y)
                    } else if (ratio <= progress) {
                        linePath.lineTo(x, y)
                        filledPath.lineTo(x, y)
                    }
                }

                // Close the fill path for gradient
                if (calculatedPoints.isNotEmpty()) {
                    val lastVisiblePointIndex = (progress * (data.size - 1)).toInt()
                    if (lastVisiblePointIndex < calculatedPoints.size) {
                        val lastPoint = calculatedPoints[lastVisiblePointIndex]
                        filledPath.lineTo(lastPoint.x, height - bottomMargin)
                        filledPath.lineTo(leftMargin, height - bottomMargin)
                        filledPath.close()
                    }
                }

                // Draw filled gradient
                drawPath(
                    path = filledPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.3f),
                            color.copy(alpha = 0.05f)
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

                // Draw x-axis labels and points
                data.forEachIndexed { index, point ->
                    val ratio = index.toFloat() / (data.size - 1)
                    if (ratio <= progress) {
                        val x = leftMargin + ratio * chartWidth
                        val normalizedY = (point.rating - paddedMin) / (paddedMax - paddedMin)
                        val y = topMargin + chartHeight - (normalizedY * chartHeight)

                        // Draw point
                        drawCircle(
                            color = Color.Black,
                            radius = 5f,
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = color,
                            radius = 3.5f,
                            center = Offset(x, y)
                        )

                        // Draw x-axis labels for first, last and every nth point
                        if (index == 0 || index == data.size - 1 || index % (maxOf(
                                1,
                                data.size / 5
                            )) == 0
                        ) {
                            val formattedDate = dateFormat.format(point.date)
                            drawContext.canvas.nativeCanvas.drawText(
                                formattedDate,
                                x - 15f,
                                height - 15f, // Adjusted position
                                android.graphics.Paint().apply {
                                    textSize = with(density) { 10.sp.toPx() }
                                    textAlign = android.graphics.Paint.Align.CENTER
//                                    color = android.graphics.Color.WHITE
                                    alpha = 180
                                }
                            )
                        }
                    }
                }

                // Draw hover elements
                interactionState?.let { state ->
                    val hoverData = state.hoverData
                    if (hoverData != null) {
                        val (point, offset) = hoverData

                        // Draw vertical dotted line
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = Offset(offset.x, topMargin),
                            end = Offset(offset.x, height - bottomMargin),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                        )

                        // Draw horizontal dotted line
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = Offset(leftMargin, offset.y),
                            end = Offset(width - rightMargin, offset.y),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                        )

                        // Highlight the point
                        drawCircle(
                            color = color,
                            radius = 8f,
                            center = offset,
                            style = Stroke(width = 2f)
                        )
                    }
                }
            }
        }
    }
}

private fun findNearestPoint(
    touchX: Float,
    points: List<Triple<RatingPoint, Float, Float>>,
    chartWidth: Float,
    leftMargin: Float,
    chartHeight: Float,
    topMargin: Float,
    onPointFound: (RatingPoint, Offset) -> Unit
) {
    if (points.isEmpty()) return

    // Find the closest x-coordinate
    var minDistance = Float.MAX_VALUE
    var closestPoint: Triple<RatingPoint, Float, Float>? = null

    points.forEach { pointData ->
        val (_, ratio, _) = pointData
        val x = leftMargin + ratio * chartWidth
        val distance = abs(x - touchX)

        if (distance < minDistance) {
            minDistance = distance
            closestPoint = pointData
        }
    }

    closestPoint?.let { (point, ratio, normalizedY) ->
        val x = leftMargin + ratio * chartWidth
        val y = topMargin + chartHeight - (normalizedY * chartHeight)
        onPointFound(point, Offset(x, y))
    }
}

sealed class InteractionState {
    object Dragging : InteractionState()

    var hoverData: Pair<RatingPoint, Offset>? = null
}

@Composable
fun EmptyChartGrid() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw grid lines
        val gridColor = Color.White.copy(alpha = 0.05f)
        val gridStroke =
            Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)))

        // Horizontal lines
        val horizontalLinesCount = 5
        for (i in 0..horizontalLinesCount) {
            val y = height * i / horizontalLinesCount
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f,
                pathEffect = gridStroke.pathEffect
            )
        }

        // Vertical lines
        val verticalLinesCount = 6
        for (i in 0..verticalLinesCount) {
            val x = width * i / verticalLinesCount
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f,
                pathEffect = gridStroke.pathEffect
            )
        }
    }
}

private fun getSampleRapidData(): List<RatingPoint> {
    val calendar = Calendar.getInstance()
    val data = mutableListOf<RatingPoint>()

    calendar.add(Calendar.MONTH, -3)

    val startRating = 1500
    var currentRating = startRating

    for (i in 0..14) {
        val ratingChange = (-20..30).random()
        currentRating += ratingChange

        data.add(RatingPoint(calendar.time, currentRating))
        calendar.add(Calendar.DAY_OF_MONTH, 7) // One week interval
    }

    return data
}

private fun getSampleBlitzData(): List<RatingPoint> {
    val calendar = Calendar.getInstance()
    val data = mutableListOf<RatingPoint>()

    calendar.add(Calendar.MONTH, -2)

    val startRating = 1600
    var currentRating = startRating

    for (i in 0..10) {
        val ratingChange = (-25..25).random()
        currentRating += ratingChange

        data.add(RatingPoint(calendar.time, currentRating))
        calendar.add(Calendar.DAY_OF_MONTH, 5)
    }

    return data
}

private fun getSampleBulletData(): List<RatingPoint> {
    // Return empty list to demonstrate "no data" state
    return emptyList()
}

@Preview
@Composable
fun ChessRatingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Text(
            text = "Your Performance",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RatingHistoryChart(
            modifier = Modifier.fillMaxWidth()
        )
    }
}