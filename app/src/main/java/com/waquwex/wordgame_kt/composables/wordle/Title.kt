package com.waquwex.wordgame_kt.composables.wordle

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.waquwex.wordgame_kt.ui.theme.caveatFamily

// Game title
@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        color = Color.White,
        fontFamily = caveatFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        fontSize = 24.sp,
        modifier = modifier.fillMaxWidth()
    )
}