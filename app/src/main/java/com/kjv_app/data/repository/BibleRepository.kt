package com.kjv_app.data.repository

import com.kjv_app.data.local.BibleDao
import com.kjv_app.domain.model.Commentary
import com.kjv_app.domain.model.Verse
import com.kjv_app.domain.model.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepository @Inject constructor(
    private val bibleDao: BibleDao
) {
    fun getAllBooks(): Flow<List<Book>> = bibleDao.getAllBooks()

    // Room runs suspend queries on its own background dispatcher —
    // withContext(Dispatchers.IO) is no longer needed
    suspend fun getChapterCount(bookId: Int): Int =
        bibleDao.getChapterCount(bookId)

    fun getVerses(bookId: Int, chapter: Int): Flow<List<Verse>> =
        bibleDao.getVersesByChapter(bookId, chapter)

    suspend fun getCommentariesForChapter(
        bookId: Int,
        chapter: Int
    ): List<Commentary> = bibleDao.getCommentariesForChapter(bookId, chapter)

    fun searchVerses(query: String): Flow<List<Verse>> =
        bibleDao.searchVerses(query)
}