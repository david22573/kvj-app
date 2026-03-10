package com.kjv_app.data.repository

import com.kjv_app.data.local.BibleDao
import com.kjv_app.domain.model.Book
import com.kjv_app.domain.model.Verse
import com.kjv_app.domain.model.Commentary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BibleRepository(
    private val bibleDao: BibleDao
) {
    fun getAllBooks(): Flow<List<Book>> = bibleDao.getAllBooks()

    suspend fun getChapterCount(bookId: Int): Int = withContext(Dispatchers.IO) {
        bibleDao.getChapterCount(bookId)
    }

    fun getVerses(bookId: Int, chapter: Int): Flow<List<Verse>> =
        bibleDao.getVersesByChapter(bookId, chapter)

    suspend fun getCommentariesForVerse(verseId: Long): List<Commentary> = withContext(Dispatchers.IO) {
        bibleDao.getCommentaryForVerse(verseId)
    }

    // New bulk fetch method
    suspend fun getCommentariesForChapter(bookId: Int, chapter: Int): List<Commentary> = withContext(Dispatchers.IO) {
        bibleDao.getCommentariesForChapter(bookId, chapter)
    }
}