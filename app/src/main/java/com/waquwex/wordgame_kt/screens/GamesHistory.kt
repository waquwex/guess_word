package com.waquwex.wordgame_kt.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.waquwex.wordgame_kt.WordGameDbHelper
import com.waquwex.wordgame_kt.composables.gamesHistory.GameHistoryColumnItem
import java.text.SimpleDateFormat
import java.util.Locale

// Display played games which become successful
@Composable
fun GamesHistory(context: Context) {
    val dbHelper = WordGameDbHelper(context.applicationContext)
    val gameHistories = dbHelper.getAllGameHistory()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    // This will not needed but grouping history by columns per 50 db rows with lazy loading is possible

    Column(modifier = Modifier.padding(8.dp).clip(RoundedCornerShape(10.dp))) {
        GameHistoryColumnItem(text0 = "ID", text1 = "Hidden Word", text2 = "Date",
            backgroundColor = Color.Black)
        LazyColumn(modifier = Modifier.weight(1f)) {
            for (gh in gameHistories) {
                item {
                    GameHistoryColumnItem(text0 = gh.id.toString(), text1 = gh.hiddenWord, text2 = dateFormat.format(gh.date))
                }
            }
        }
    }
}