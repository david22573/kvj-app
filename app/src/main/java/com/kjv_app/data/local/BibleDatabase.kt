package com.kjv_app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//import com.kjv_app.data.local.BibleDao
import com.kjv_app.domain.model.Book
import com.kjv_app.domain.model.Commentary
import com.kjv_app.domain.model.Verse

@Database(
    entities = [Book::class, Verse::class, Commentary::class],
    version = 1,
    exportSchema = false
)
abstract class BibleDatabase : RoomDatabase() {

    abstract fun bibleDao(): BibleDao

    companion object {
        @Volatile
        private var INSTANCE: BibleDatabase? = null

        fun getDatabase(context: Context): BibleDatabase {
            // Multiple threads could ask for the database at the same time,
            // so we synchronize to ensure only one instance is ever created.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BibleDatabase::class.java,
                    "kjv_bible_database"
                )
                    // For a pre-populated Bible database, you would use createFromAsset() here
                    .createFromAsset("database/kjv_bible.db")
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}