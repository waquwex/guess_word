package com.waquwex.wordgame_kt.composables.wordle

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

enum class LetterCodes(val value: Int) {
    NOT_TOUCHED(0),
    NOT_EXISTS(1),
    EXISTS(2),
    SAME_POS(3);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}

fun getLetterColor(lc: LetterCodes): Color {
    return when (lc) {
        LetterCodes.NOT_TOUCHED -> Color.White
        LetterCodes.NOT_EXISTS -> Color.DarkGray
        LetterCodes.EXISTS -> Color(0xFFff6a00)
        LetterCodes.SAME_POS -> Color.Green
    }
}

val MAX_SCALE_FOR_ANIM = 1.05f

@Composable
fun Wordle(
    modifier: Modifier = Modifier, enteredWords: Array<String>, hiddenWord: String,
    onSubmit: (word: String) -> Unit, activeIndex: Int, gameOver: Boolean, invalidGuessCount: Int
) {
    Column(
        modifier = modifier
    ) {
        for (i in 0 until 6) {
            val enabled = !gameOver && activeIndex == i
            val enabledAnimatedScale = remember { Animatable(1f) }

            LaunchedEffect(enabled) {
                if (enabled) {
                    enabledAnimatedScale.animateTo(MAX_SCALE_FOR_ANIM, animationSpec = (tween(750)))
                } else {
                    enabledAnimatedScale.animateTo(1f, animationSpec = (tween(400)))
                }
            }

            WordleEditText(
                invalidGuessCount = invalidGuessCount,
                word = enteredWords[i],
                hiddenWord = hiddenWord,
                index = i,
                enabled = enabled,
                onSubmit = {
                    onSubmit(it)
                },
                modifier = Modifier
                    .weight(1.0f)
                    .scale(enabledAnimatedScale.value)
                    .offset(Dp(0f), Dp(0f))
            )
        }
    }
}
