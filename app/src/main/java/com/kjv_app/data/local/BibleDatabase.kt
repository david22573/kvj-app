package com.kjv_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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
}