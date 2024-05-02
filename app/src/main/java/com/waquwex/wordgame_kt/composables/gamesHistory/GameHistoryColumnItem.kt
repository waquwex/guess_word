package com.waquwex.wordgame_kt.composables.gamesHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun GameHistoryColumnItem(text0: String, text1: String, text2: String,
                          backgroundColor: Color = Color.DarkGray
) {
    Row(modifier = Modifier
        .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .width(30.dp)
                .background(backgroundColor)
        ) {
            Text(text = text0, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Box(
            modifier = Modifier
                .width(120.dp)
                .background(lerp(backgroundColor, Color.Black, 0.25f))
        ) {
            Text(text = text1, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Box(
            modifier = Modifier
                .weight(1.5f)
                .background(backgroundColor)
        ) {
            Text(text = text2, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}