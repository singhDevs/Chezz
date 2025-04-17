package com.singhDevs.chezz.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Piece
import com.singhDevs.chezz.R
import com.singhDevs.chezz.utils.Constants

@Composable
fun PromotionOptionsComposable(color: Char, onPromotionPieceSelected: (Piece) -> Unit) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = colorResource(R.color.primary_amber),
                shape = RoundedCornerShape(percent = 20)
            )
            .background(colorResource(R.color.see_more_btn)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(color == 'w'){
            PromotionOptionItem('q', color, onClick = { onPromotionPieceSelected(Piece.WHITE_QUEEN) })
            PromotionOptionItem('n', color, onClick = { onPromotionPieceSelected(Piece.WHITE_KNIGHT) })
            PromotionOptionItem('b', color, onClick = { onPromotionPieceSelected(Piece.WHITE_BISHOP) })
            PromotionOptionItem('r', color, onClick = { onPromotionPieceSelected(Piece.WHITE_ROOK) })
        }
        else{
            PromotionOptionItem('q', color, onClick = { onPromotionPieceSelected(Piece.BLACK_QUEEN) })
            PromotionOptionItem('n', color, onClick = { onPromotionPieceSelected(Piece.BLACK_KNIGHT) })
            PromotionOptionItem('b', color, onClick = { onPromotionPieceSelected(Piece.BLACK_BISHOP) })
            PromotionOptionItem('r', color, onClick = { onPromotionPieceSelected(Piece.BLACK_ROOK) })
        }
    }
}

@Composable
fun PromotionOptionItem(piece: Char, color: Char, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable(onClick = onClick)
            .background(colorResource(R.color.see_more_btn)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(40.dp).padding(3.dp),
            painter = painterResource(
                if (color == 'w') Constants.whitePieceToSymbol[piece]!!
                else Constants.blackPieceToSymbol[piece]!!
            ),
            contentDescription = null
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PromotionOptionsComposablePreview() {
    PromotionOptionsComposable(
        'w',
        {}
    )
}