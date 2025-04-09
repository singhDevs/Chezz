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
    movesList: List<String>,
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
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if((index + 1) % 2 != 0){
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = "${index/2 + 1}.",
                        fontSize = 20.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
                if(move[0] == 'O'){
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = move,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.replay_text)
                    )
                }
                else{
                    Image(
                        modifier = Modifier
                            .padding(end = 3.dp)
                            .size(25.dp),
                        painter = painterResource(
                            if (move[0] == 'w')
                                Constants.whitePieceToSymbol[move[1]]!!
                            else
                                Constants.blackPieceToSymbol[move[1]]!!
                        ),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = move.slice(2..<move.length),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.replay_text)
                    )
                }
                ScrollToEnd(movesList, lazyListState)
            }
        }
    }
}

@Composable
fun ScrollToEnd(movesList: List<String>, lazyListState: LazyListState) {
    Log.d(TAG, "ScrollToEnd called")
    LaunchedEffect(movesList.size) {
        if (movesList.isNotEmpty()) {
            lazyListState.animateScrollToItem(movesList.size - 1)
        }
    }
}