package com.singhDevs.chezz.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singhDevs.chezz.R
import com.singhDevs.chezz.utils.Constants

private const val TAG = "MovesListComposable"

@Composable
fun MovesListComposable(
    movesList: List<String?>,
    lazyListState: LazyListState
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        state = lazyListState,
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(movesList) { index, move ->
            if (move.isNullOrEmpty()) {
                Log.w(TAG, "Skipping null or empty move at index $index")
                return@itemsIndexed
            }

            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if ((index + 1) % 2 != 0) {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = "${index / 2 + 1}.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                        color = colorResource(R.color.replay_text),
                    )
                }

                when {
                    move.startsWith('O') -> {
                        // Castling move (e.g., "O-O" or "O-O-O")
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = move,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                            color = Color.White
                        )
                    }

                    move.startsWith('p') -> {
                        Log.d(TAG, "Move starts with p")
                        val parts = move.split(':')
                        if (parts.size >= 4) {
                            val to = parts[1]
                            val color = parts[2]
                            val promotion = parts[3]

                            if(promotion.isEmpty()){
                                Log.d(TAG, "promotion string is empty. Not printing this move.")
                            }
                            else {
                                Text(
                                    modifier = Modifier.padding(start = 5.dp),
                                    text = "$to=",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                    color = Color.White
                                )
                                Image(
                                    modifier = Modifier
                                        .padding(end = 3.dp)
                                        .size(25.dp),
                                    painter = painterResource(
                                        if (color == "w")
                                            Constants.whitePieceToSymbol[promotion[0]]!!
                                        else
                                            Constants.blackPieceToSymbol[promotion[0]]!!
                                    ),
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    move.startsWith('w') -> {
                        Log.d(TAG, "Move starts with w")
                        val parts = move.split(':')
                        if (parts.size >= 4) {
                            val color = parts[0]
                            val piece = parts[1]
                            val from = parts[2]
                            val to = parts[3]

                            Log.d(TAG, "piece: $piece")

                            Image(
                                modifier = Modifier
                                    .padding(end = 3.dp)
                                    .size(25.dp),
                                painter = painterResource(
                                    if (color == "w")
                                        Constants.whitePieceToSymbol[piece[0]]!!
                                    else
                                        Constants.blackPieceToSymbol[piece[0]]!!
                                ),
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.padding(start = 5.dp),
                                text = "$from$to",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                color = Color.White
                            )
                        }
                        else{
                            Log.e(TAG, "parts size not >=4, Invalid move format: $move")
                        }
                    }

                    move.startsWith('b') -> {
                        Log.d(TAG, "Move starts with b")
                        val parts = move.split(':')
                        if (parts.size >= 4) {
                            val color = parts[0]
                            val piece = parts[1]
                            val from = parts[2]
                            val to = parts[3]

                            Log.d(TAG, "move starts with b, piece: $piece")

                            Image(
                                modifier = Modifier
                                    .padding(end = 3.dp)
                                    .size(25.dp),
                                painter = painterResource(
                                    if (color == "w")
                                        Constants.whitePieceToSymbol[piece[0]]!!
                                    else
                                        Constants.blackPieceToSymbol[piece[0]]!!
                                ),
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.padding(start = 5.dp),
                                text = "$from$to",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                                color = Color.White
                            )
                        }
                        else{
                            Log.e(TAG, "parts size not >=4, Invalid move format: $move")
                        }
                    }

                    else -> {
                        Log.e(TAG, "Neither of the O, p, w, b: Invalid move format: $move")
                    }
                }
            }
        }
    }
    ScrollToEnd(movesList, lazyListState)
}

@Composable
fun ScrollToEnd(movesList: List<String?>, lazyListState: LazyListState) {
    Log.d(TAG, "ScrollToEnd called")
    LaunchedEffect(movesList.size) {
        if (movesList.isNotEmpty()) {
            lazyListState.animateScrollToItem(movesList.size - 1)
        }
    }
}