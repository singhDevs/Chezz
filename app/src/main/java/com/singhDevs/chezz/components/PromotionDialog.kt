package com.singhDevs.chezz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.move.Move
import com.singhDevs.chezz.utils.Constants
import java.util.Locale

@Composable
fun PromotionDialog(
    modifier: Modifier = Modifier,
    promotionMoves: List<Move>,
    onClick: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.Center) {
        promotionMoves.forEach { move ->

            IconButton(
                modifier = Modifier
                    .background(Color.White)
                    .padding(5.dp),
                onClick = {
                    onClick(move.promotion.fenSymbol.lowercase(Locale.getDefault()))
                }
            ) {
                when (move.promotion.fenSymbol.lowercase(Locale.getDefault())) {
                    "q" -> {
                        Icon(
                            painter = painterResource(Constants.blackPieceToSymbol['q']!!),
                            contentDescription = null
                        )
                    }

                    "r" -> {
                        Icon(
                            painter = painterResource(Constants.blackPieceToSymbol['r']!!),
                            contentDescription = null
                        )
                    }

                    "b" -> {
                        Icon(
                            painter = painterResource(Constants.blackPieceToSymbol['b']!!),
                            contentDescription = null
                        )
                    }

                    "n" -> {
                        Icon(
                            painter = painterResource(Constants.blackPieceToSymbol['n']!!),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}