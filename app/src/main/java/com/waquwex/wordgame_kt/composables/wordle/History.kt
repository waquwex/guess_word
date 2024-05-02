package com.waquwex.wordgame_kt.composables.wordle

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.waquwex.wordgame_kt.ENGLISH_LETTERS
import kotlin.math.ceil

// Word game current history
// Colorize letters depending on game state
@Composable
fun History(
    historyPoints: Array<Int>,
    modifier: Modifier = Modifier
) {
    val historyTexts = mutableListOf<AnnotatedString>()
    val rowLetterSize = ceil(historyPoints.size / 3.0).toInt()
    for (i in 0 until 3) {
        val anno = buildAnnotatedString {
            for (k in 0 until rowLetterSize) {
                val letterIndex = i * rowLetterSize + k
                if (letterIndex > historyPoints.size - 1)
                    break

                val letterColor =
                    getLetterColor(LetterCodes.fromInt(historyPoints[letterIndex]))
                withStyle(style = SpanStyle(letterColor)) {
                    append("${ENGLISH_LETTERS[letterIndex]}")
                    if ((k != rowLetterSize - 1) && (letterIndex != historyPoints.size - 1)) {
                        append('\n')
                    }
                }
            }
        }
        historyTexts.add(anno)
    }

    Row(modifier = modifier.border(1.dp, Color(0f, 0f, 0f, 0.2f))) {
        for (i in 0 until historyTexts.size) {
            Text(
                text = historyTexts[i],
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(20.dp)
            )
        }
    }
}