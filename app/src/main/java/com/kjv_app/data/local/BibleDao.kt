package com.kjv_app.data.local

import androidx.room.Dao
import androidx.room.Query
import com.kjv_app.domain.model.Book
import com.kjv_app.domain.model.Commentary
import com.kjv_app.domain.model.Verse
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<Book>>

    @Query(
        """
        SELECT * FROM verses 
        WHERE bookId = :bookId AND chapter = :chapter 
        ORDER BY verseNumber ASC
        """
    )
    fun getVersesByChapter(bookId: Int, chapter: Int): Flow<List<Verse>>

    @Query(
        """
        SELECT * FROM commentaries 
        WHERE verseId IN (
            SELECT id FROM verses 
            WHERE bookId = :bookId AND chapter = :chapter
        )
        """
    )
    suspend fun getCommentariesForChapter(bookId: Int, chapter: Int): List<Commentary>

    @Query("SELECT MAX(chapter) FROM verses WHERE bookId = :bookId")
    suspend fun getChapterCount(bookId: Int): Int

    @Query("SELECT * FROM verses WHERE text LIKE '%' || :searchQuery || '%' LIMIT 200")
    fun searchVerses(searchQuery: String): Flow<List<Verse>>
}