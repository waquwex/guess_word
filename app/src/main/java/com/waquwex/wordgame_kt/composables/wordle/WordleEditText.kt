package com.waquwex.wordgame_kt.composables.wordle

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

// Regex for only English characters
val ENGLISH_CHARS_REGEX = Regex("^[a-zA-Z]+$")

val MAX_ANIM_OFFSET_X = 7f

@Composable
fun WordleEditText(
    index: Int,
    enabled: Boolean,
    onSubmit: (value: String) -> Unit,
    hiddenWord: String,
    modifier: Modifier = Modifier,
    word: String,
    invalidGuessCount: Int
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val textMeasurer = rememberTextMeasurer()
    val spacingBetweenLetters = 8.dp
    val focusRequester = remember { FocusRequester() }
    val letterBoxBorderSize = 3.dp
    // width for letter boxes drawn in canvas
    var calculatedWidth by remember { mutableStateOf(20.dp) }
    val incorrectGuessAnimOffsetX = remember { Animatable(0f) }

    // store this to check if it is changed
    var previousHiddenWord: String? = null

    // New game is restarted, reset text
    LaunchedEffect(hiddenWord) {
        if (previousHiddenWord != hiddenWord) {
            text = text.copy(text = "")
        }

        previousHiddenWord = hiddenWord
    }

    // If there is saved word state set it
    if (word.isNotEmpty()) {
        text = text.copy(text = word)
    }


    LaunchedEffect(invalidGuessCount) {
        if (enabled && invalidGuessCount != 0) {
            incorrectGuessAnimOffsetX.animateTo(MAX_ANIM_OFFSET_X, animationSpec = (tween(25,
                easing = LinearEasing)))
            incorrectGuessAnimOffsetX.animateTo(0f, animationSpec = (tween(25,
                easing = LinearEasing)))
            incorrectGuessAnimOffsetX.animateTo(-MAX_ANIM_OFFSET_X, animationSpec = (tween(25,
                easing = LinearEasing)))
            incorrectGuessAnimOffsetX.animateTo(0f, animationSpec = (tween(25,
                easing = LinearEasing)))
        }
    }

    // Adjust letter colors if this is FINALIZED
    val letterColors = Array(5) { Color.White }
    if (text.text.length == 5 && !enabled) {
        for (i in 0 until 5) {
            if (hiddenWord[i] == text.text[i]) {
                letterColors[i] = getLetterColor(LetterCodes.SAME_POS)
            } else if (hiddenWord.contains(text.text[i])) {
                letterColors[i] = getLetterColor(LetterCodes.EXISTS)
            } else {
                letterColors[i] = getLetterColor(LetterCodes.NOT_EXISTS)
            }
        }
    }

    // Request focus if switched this Composable
    LaunchedEffect(enabled) {
        if (enabled) {
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        enabled = enabled,
        value = text,
        onValueChange = {
            if ((it.text.isEmpty() || ENGLISH_CHARS_REGEX.matches(it.text)) && it.text.length <= 5) {
                val newText = it.text.uppercase(Locale.US)
                text = it.copy(text = newText)
            }
        },
        singleLine = true,
        decorationBox = {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var boxSize = Size(size.height, size.height)
                var requiredWidth = (spacingBetweenLetters.toPx() * 4) + boxSize.width * 5
                calculatedWidth = requiredWidth.toDp()

                if (requiredWidth > size.width) {
                    val blockSize = (size.width - spacingBetweenLetters.toPx() * 4) / 5
                    boxSize = Size(blockSize, blockSize)
                    requiredWidth = size.width
                }

                val textStyle = TextStyle(
                    fontSize = (boxSize.height - 6).toSp(), letterSpacing = 0.sp,
                    baselineShift = BaselineShift.None, color = Color.White
                )

                for (i in 0..<5) {
                    val offsetX =
                        (size.width - requiredWidth) / 2 + (i * (requiredWidth - boxSize.width) / 4f)
                    val offsetY = (size.height - boxSize.height) / 2
                    val letterColor = if (i == text.text.length - 1) Color.Cyan else Color.Blue

                    // Enabled status indicator letter borders
                    if (enabled) {
                        drawRoundRect(
                            letterColor, Offset(
                                offsetX - letterBoxBorderSize.toPx() / 2,
                                offsetY - letterBoxBorderSize.toPx() / 2
                            ), with(boxSize) {
                                Size(
                                    width = width + letterBoxBorderSize.toPx(),
                                    height = height + letterBoxBorderSize.toPx()
                                )
                            },
                            CornerRadius(5f, 5f)
                        )
                    }

                    drawRoundRect(
                        Color.Black, Offset(offsetX, offsetY), boxSize,
                        CornerRadius(4f, 4f)
                    )

                    if (i < text.text.length) {
                        val letter = text.text[i].toString()
                        val letterSize = textMeasurer.measure(letter, textStyle)
                        val letterOffsetX = (boxSize.width - letterSize.size.width) / 2f
                        // there is some padding on top letterSize.size.height,
                        // fonts have top descend bottom ascend properties
                        // manually adjusting it with magic value to center it vertically
                        val letterOffsetY = ((boxSize.height - letterSize.size.height) / 2) - 2

                        drawText(
                            textMeasurer, text.text[i].toString(), Offset(
                                offsetX + letterOffsetX, offsetY + letterOffsetY
                            ), textStyle.copy(color = letterColors[i])
                        )
                    }
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            if (text.text.length == 5) {
                onSubmit(text.text)
            }
        }),
        modifier = modifier
            .padding(5.dp)
            .focusRequester(focusRequester)
            .width(calculatedWidth)
            .offset(Dp(incorrectGuessAnimOffsetX.value), Dp(0f))
    )
}