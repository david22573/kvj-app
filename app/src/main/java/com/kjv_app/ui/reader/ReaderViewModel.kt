package com.kjv_app.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kjv_app.data.repository.BibleRepository
import com.kjv_app.data.repository.UserPreferencesRepository
import com.kjv_app.domain.model.Book
import com.kjv_app.domain.model.Commentary
import com.kjv_app.domain.model.Verse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReaderUiState {
    object Loading : ReaderUiState

    data class ChapterSelection(
        val book: Book,
        val chapterCount: Int
    ) : ReaderUiState

    data class Reading(
        val book: Book,
        val chapter: Int,
        val chapterCount: Int,
        val verses: List<Verse>,
        val commentaries: Map<Long, List<Commentary>>
    ) : ReaderUiState

    data class Error(val message: String) : ReaderUiState
}

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repository: BibleRepository,
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ReaderUiState>(ReaderUiState.Loading)
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    fun loadBookForChapterSelection(bookId: Int) {
        viewModelScope.launch {
            _uiState.value = ReaderUiState.Loading
            val books = repository.getAllBooks().first()
            val book = books.find { it.id == bookId }

            if (book != null) {
                val count = repository.getChapterCount(bookId)
                _uiState.value = ReaderUiState.ChapterSelection(book, count)
            } else {
                _uiState.value = ReaderUiState.Error("Book not found")
            }
        }
    }

    fun loadChapter(bookId: Int, chapter: Int) {
        viewModelScope.launch {
            _uiState.value = ReaderUiState.Loading
            val books = repository.getAllBooks().first()
            val book = books.find { it.id == bookId }

            if (book != null) {
                // Async save via DataStore
                userPrefs.updateLastRead(bookId, chapter)

                val count = repository.getChapterCount(bookId)
                val verses = repository.getVerses(bookId, chapter).first()
                val commentariesList = repository.getCommentariesForChapter(bookId, chapter)

                _uiState.value = ReaderUiState.Reading(
                    book = book,
                    chapter = chapter,
                    chapterCount = count,
                    verses = verses,
                    commentaries = commentariesList.groupBy { it.verseId }
                )
            } else {
                _uiState.value = ReaderUiState.Error("Failed to load chapter")
            }
        }
    }
}