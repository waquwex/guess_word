package com.waquwex.wordgame_kt

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.waquwex.wordgame_kt.composables.NavBar
import com.waquwex.wordgame_kt.screens.Game
import com.waquwex.wordgame_kt.screens.GamesHistory
import com.waquwex.wordgame_kt.ui.theme.WordgamektTheme

val ENGLISH_LETTERS = arrayOf(
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val imeTop = WindowInsets.ime.getTop(LocalDensity.current)
        //val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
        setContent {
            val navController = rememberNavController()
            var hiddenWord by remember { mutableStateOf("") }
            var historyPoints = remember { Array(ENGLISH_LETTERS.size) { 0 } }
            var activeIndex by remember { mutableIntStateOf(0) }
            val dbHelper = WordGameDbHelper.getInstance(applicationContext)
            var enteredWords = remember { Array(6) { "" } }
            var gameOver by remember { mutableStateOf(false) }
            var invalidGuessCount by remember { mutableIntStateOf(0) }

            fun handleGameOver(success: Boolean, hiddenWord: String) {
                gameOver = true
                if (success) {
                    dbHelper.putHiddenWord(hiddenWord)
                    Toast.makeText(applicationContext, R.string.game_won, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, getString(R.string.game_lost, hiddenWord),
                        Toast.LENGTH_LONG).show()
                }
            }

            LaunchedEffect(dbHelper) {
                hiddenWord = dbHelper.getRandomWordFromDB()
            }

            LaunchedEffect(hiddenWord) {
                historyPoints = Array(ENGLISH_LETTERS.size) { 0 }
                activeIndex = 0
                enteredWords = Array(6) { "" }
                gameOver = false
            }

            fun handleRestartGame() {
                hiddenWord = dbHelper.getRandomWordFromDB()
            }

            fun updateHistoryPoints(word: String) {
                // Update history points
                val cloneHistoryPoints = historyPoints.copyOf()
                for (c in 0 until 5) {
                    val currentLetter = word[c]
                    val currentLetterCharIndex: Int =
                        ENGLISH_LETTERS.indexOf(currentLetter)
                    if (word[c] == hiddenWord[c]) {
                        cloneHistoryPoints[currentLetterCharIndex] = 3
                    } else if (hiddenWord.contains(currentLetter)) {
                        if (cloneHistoryPoints[currentLetterCharIndex] != 3) {
                            cloneHistoryPoints[currentLetterCharIndex] = 2
                        }
                    } else {
                        if (cloneHistoryPoints[currentLetterCharIndex] == 0) {
                            cloneHistoryPoints[currentLetterCharIndex] = 1
                        }
                    }
                }
                historyPoints = cloneHistoryPoints
            }

            WordgamektTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF333333)
                ) {
                    Column {
                        val navRoutes = arrayOf("game", "gamesHistory")
                        NavHost(
                            navController,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            startDestination = navRoutes[0]
                        ) {
                            composable(
                                navRoutes[0]
                            ) {
                                Game(
                                    gameOver = gameOver,
                                    hiddenWord = hiddenWord,
                                    historyPoints = historyPoints,
                                    activeIndex = activeIndex,
                                    enteredWords = enteredWords,
                                    onRestartGame = {
                                        handleRestartGame()
                                    },
                                    invalidGuessCount = invalidGuessCount,
                                    onSubmit = { word ->
                                        if (dbHelper.isWordExists(word)) {
                                            // Add submitted word to enteredWords
                                            val copyEnteredWords = enteredWords.copyOf()
                                            copyEnteredWords[activeIndex] = word
                                            enteredWords = copyEnteredWords

                                            updateHistoryPoints(word)

                                            if (word == hiddenWord) { // Check if hiddenWord is found
                                                handleGameOver(true, hiddenWord)
                                            } else {
                                                if (activeIndex == 5) { // No input left: game is over
                                                    handleGameOver(false, hiddenWord)
                                                } else {
                                                    activeIndex++
                                                }
                                            }
                                        } else {
                                            // incorrect guess
                                            invalidGuessCount++
                                        }
                                    })
                            }
                            composable(navRoutes[1]) {
                                GamesHistory(context = applicationContext)
                            }
                        }

                        Column {
                            NavBar(navRoutes, navController)
                        }
                    }
                }
            }
        }
    }
}