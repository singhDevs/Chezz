package com.singhDevs.chezz.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.singhDevs.chezz.R
import com.singhDevs.chezz.utils.Constants

private const val TAG = "MovesListComposable"

@Composable
fun ReplayMovesListComposable(
    movesList: List<String>,
    lazyListState: LazyListState,
    currIndex: Int,
    onMoveClicked: (index: Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        state = lazyListState,
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(movesList) { index, move ->
            if (move.isEmpty()) {
                Log.w(TAG, "Skipping null or empty move at index $index")
                return@itemsIndexed
            }

            Row(
                modifier = Modifier.padding(horizontal = 10.dp)
                    .clickable { onMoveClicked(index) }
                    .clip(RoundedCornerShape(percent = 20))
                    .background(if(currIndex == index) colorResource(R.color.primary_amber) else colorResource(R.color.see_more_btn)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if ((index + 1) % 2 != 0) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                        text = "${index / 2 + 1}.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                        color = colorResource(R.color.replay_text),
                    )
                }

                when {
                    move.startsWith('O') -> {
                        // Castling move (e.g., "O-O" or "O-O-O")
                        Text(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                            text = move,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                            color = Color.White
                        )
                    }

                    move.length == 6 -> {
                        Log.d(TAG, "Move is a promotion")
                        val to = "${move[3]}${move[4]}"
                        val color = if (index % 2 == 0) "w" else "b"
                        val promotion = move[5]

                        Text(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                            text = "$to=",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                            color = Color.White
                        )
                        Image(
                            modifier = Modifier
                                .padding(end = 3.dp, top = 8.dp, bottom = 8.dp)
                                .size(25.dp),
                            painter = painterResource(
                                if (color == "w")
                                    Constants.whitePieceToSymbol[promotion]!!
                                else
                                    Constants.blackPieceToSymbol[promotion]!!
                            ),
                            contentDescription = null
                        )

                    }

                    else -> {
                        if (index % 2 == 0) {
                            Log.d(TAG, "white's move")
                            val piece = move[0]
                            val from = "${move[1]}${move[2]}"
                            val to = "${move[3]}${move[4]}"

                            Log.d(TAG, "piece: $piece")

                            Image(
                                modifier = Modifier
                                    .padding(end = 3.dp, top = 8.dp, bottom = 8.dp)
                                    .size(25.dp),
                                painter = painterResource(Constants.whitePieceToSymbol[piece]!!),
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                                text = "$from$to",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                color = Color.White
                            )
                        }
                        else {
                            Log.d(TAG, "Black's move")
                            val piece = move[0]
                            val from = "${move[1]}${move[2]}"
                            val to = "${move[3]}${move[4]}"

                            Log.d(TAG, "piece: $piece")

                            Image(
                                modifier = Modifier
                                    .padding(end = 3.dp, top = 8.dp, bottom = 8.dp)
                                    .size(25.dp),
                                painter = painterResource(Constants.blackPieceToSymbol[piece]!!),
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 5.dp),
                                text = "$from$to",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
    ReplayListScrollToEnd(movesList, lazyListState)
}

@Composable
fun ReplayListScrollToEnd(movesList: List<String?>, lazyListState: LazyListState) {
    Log.d(TAG, "ScrollToEnd called")
    LaunchedEffect(movesList.size) {
        if (movesList.isNotEmpty()) {
            lazyListState.animateScrollToItem(movesList.size - 1)
        }
    }
}