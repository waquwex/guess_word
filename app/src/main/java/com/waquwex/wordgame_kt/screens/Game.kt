package com.waquwex.wordgame_kt.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.waquwex.wordgame_kt.R
import com.waquwex.wordgame_kt.composables.wordle.History
import com.waquwex.wordgame_kt.composables.wordle.Title
import com.waquwex.wordgame_kt.composables.wordle.Wordle

// Word game screen
@Composable
fun Game(
    enteredWords: Array<String>,
    hiddenWord: String,
    historyPoints: Array<Int>,
    onSubmit: (word: String) -> Unit,
    activeIndex: Int,
    gameOver: Boolean,
    invalidGuessCount: Int,
    onRestartGame: () -> Unit
) {
    Column(
        modifier = Modifier
    ) {
        Title(stringResource(R.string.app_name))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            History(
                modifier = Modifier.padding(end = 12.dp), historyPoints = historyPoints)
            Wordle(
                invalidGuessCount = invalidGuessCount,
                gameOver = gameOver,
                modifier = Modifier.height(300.dp),
                hiddenWord = hiddenWord,
                enteredWords = enteredWords,
                activeIndex = activeIndex,
                onSubmit = onSubmit
            )
        }
        if (gameOver) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onRestartGame, modifier = Modifier.align(Alignment.Center)) {
                    Text(text = stringResource(R.string.replay))
                }
            }
        }
    }
}