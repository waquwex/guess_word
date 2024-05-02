package com.waquwex.wordgame_kt

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Date
import java.util.Locale


data class GameHistory(
    val id: Int,
    val hiddenWord: String,
    val date: Date
)

private const val SQL_CREATE_WORDS_TABLE =
    "CREATE TABLE ${WordGameDbHelper.WordTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${WordGameDbHelper.WordTable.COLUMN_NAME_WORD} TEXT)"

private const val SQL_DELETE_WORDS_TABLE = "DROP TABLE IF EXISTS ${WordGameDbHelper.WordTable.TABLE_NAME}"

private const val SQL_CREATE_GAMES_HISTORY_TABLE =
    "CREATE TABLE ${WordGameDbHelper.GamesHistoryTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "${WordGameDbHelper.GamesHistoryTable.COLUMN_NAME_HIDDEN_WORD} TEXT, " +
            "${WordGameDbHelper.GamesHistoryTable.COLUMN_NAME_DATE} INTEGER)"

private const val SQL_DELETE_GAMES_HISTORY =
    "DROP TABLE IF EXISTS ${WordGameDbHelper.GamesHistoryTable.TABLE_NAME}"

class WordGameDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null,
    DATABASE_VERSION) {
    private val appContext: Context = context.applicationContext // Storing application context

    companion object {
        private const val DATABASE_NAME = "wordlegame.db"
        private const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: WordGameDbHelper? = null

        fun getInstance(context: Context): WordGameDbHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = WordGameDbHelper(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    object WordTable : BaseColumns {
        const val TABLE_NAME = "words"
        const val COLUMN_NAME_WORD = "word"
    }

    object GamesHistoryTable : BaseColumns {
        const val TABLE_NAME = "gamesHistory"
        const val COLUMN_NAME_HIDDEN_WORD = "hiddenWord"
        const val COLUMN_NAME_DATE = "date"
    }

    // app first install
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_WORDS_TABLE)
        db.execSQL(SQL_CREATE_GAMES_HISTORY_TABLE)
        populateWords(db)
    }

    // e.g. update columns when there is need
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    // populate words from .txt resource file
    private fun populateWords(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            val words = readWordListResource()
            for (word in words) {
                val values = ContentValues()
                values.put(WordTable.COLUMN_NAME_WORD, word)
                db.insert(WordTable.TABLE_NAME, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getRandomWordFromDB(): String {
        val db = this.readableDatabase
        lateinit var randomWord: String

        val countQuery = "SELECT COUNT(*) FROM ${WordTable.TABLE_NAME}"
        // Get the total count of rows in the table
        val cursor: Cursor = db.rawQuery(countQuery, null)
        cursor.moveToFirst()
        val rowCount: Int = cursor.getInt(0)
        cursor.close()

        if (rowCount > 0) {
            // Generate a random row index
            val randomIndex = (0 until rowCount).random()
            val getRowByRowOrderQuery =
                "SELECT ${WordTable.COLUMN_NAME_WORD} FROM ${WordTable.TABLE_NAME} LIMIT 1 OFFSET ?"
            val randomCursor = db.rawQuery(getRowByRowOrderQuery, arrayOf(randomIndex.toString()))

            if (randomCursor.moveToFirst()) {
                randomWord = randomCursor.getString(0)
                Log.i("RANDOMWORD", randomWord)
            }
            randomCursor.close()
        } else {
            throw Exception("DB doesn't have anything on Words table")
        }

        db.close()
        return randomWord.uppercase(Locale.US)
    }

    fun isWordExists(word: String): Boolean {
        val db = this.readableDatabase
        val word = word.lowercase(Locale.US) // words are stored lower case in DB
        val query = "SELECT * FROM ${WordTable.TABLE_NAME} WHERE ${WordTable.COLUMN_NAME_WORD} = ? COLLATE NOCASE"
        val cursor = db.rawQuery(query, arrayOf(word))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun putHiddenWord(hiddenWord: String) {
        val db = this.writableDatabase
        val query = "INSERT INTO ${GamesHistoryTable.TABLE_NAME} " +
                "(${GamesHistoryTable.COLUMN_NAME_HIDDEN_WORD}, " +
                "${GamesHistoryTable.COLUMN_NAME_DATE}) VALUES (?, ?)"
        db.execSQL(query, arrayOf(hiddenWord, System.currentTimeMillis()))
        db.close()
    }

    // get all game history date
    // it is ordered by date but:
    // date is system dependent and user can alter it
    fun getAllGameHistory(): List<GameHistory> {
        val list = mutableListOf<GameHistory>()

        val db = this.readableDatabase
        val query = "SELECT ROW_NUMBER() OVER (ORDER BY ${GamesHistoryTable.COLUMN_NAME_DATE} DESC) as rowNumber, " +
                "${GamesHistoryTable.COLUMN_NAME_HIDDEN_WORD}, " +
                "${GamesHistoryTable.COLUMN_NAME_DATE} " +
                "FROM ${GamesHistoryTable.TABLE_NAME}"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val hiddenWord = cursor.getString(1)
            val date = Date(cursor.getLong(2))
            list.add(GameHistory(id, hiddenWord, date))
        }

        cursor.close()
        db.close()

        return list
    }

    // Get all words as array from raw resource txt file
    private fun readWordListResource(): Array<String> {
        val words = mutableListOf<String>()
        try {
            val inputStream: InputStream =
                appContext.resources.openRawResource(R.raw.word_list)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line = reader.readLine()
            while (line.isNotEmpty()) {
                words.add(line.trim())
                line = reader.readLine()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return words.toTypedArray()
    }
}