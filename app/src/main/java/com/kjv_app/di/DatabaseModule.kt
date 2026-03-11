package com.kjv_app.di

import android.content.Context
import androidx.room.Room
import com.kjv_app.data.local.BibleDao
import com.kjv_app.data.local.BibleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBibleDatabase(@ApplicationContext context: Context): BibleDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            BibleDatabase::class.java,
            "kjv_bible_database"
        )
            .createFromAsset("database/kjv_bible.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideBibleDao(database: BibleDatabase): BibleDao {
        return database.bibleDao()
    }
}