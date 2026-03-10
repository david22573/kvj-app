package com.kjv_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kjv_app.data.repository.BibleRepository
import com.kjv_app.domain.model.Book
import com.kjv_app.domain.model.Commentary
import com.kjv_app.domain.model.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BibleUiState(
    val books: List<Book> = emptyList(),
    val selectedBook: Book? = null,
    val chapterCount: Int = 0,
    val selectedChapter: Int? = null,
    val verses: List<Verse> = emptyList(),
    val commentaries: Map<Long, List<Commentary>> = emptyMap(), // Indexed by VerseId
    val isLoading: Boolean = true
)

class BibleViewModel(private val repository: BibleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(BibleUiState())
    val uiState: StateFlow<BibleUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            repository.getAllBooks().collect { books ->
                _uiState.update { it.copy(books = books, isLoading = false) }
            }
        }
    }

    fun selectBook(book: Book) {
        viewModelScope.launch {
            val count = repository.getChapterCount(book.id)
            _uiState.update {
                it.copy(
                    selectedBook = book,
                    chapterCount = count,
                    selectedChapter = null,
                    verses = emptyList(),
                    commentaries = emptyMap()
                )
            }
        }
    }

    fun selectChapter(chapter: Int) {
        val book = _uiState.value.selectedBook ?: return
        _uiState.update { it.copy(selectedChapter = chapter, isLoading = true) }

        viewModelScope.launch {
            // 1. Fetch and index commentaries for the entire chapter
            val commentariesList = repository.getCommentariesForChapter(book.id, chapter)
            val commentariesMap = commentariesList.groupBy { it.verseId }
            _uiState.update { it.copy(commentaries = commentariesMap) }

            // 2. Collect verses and render
            repository.getVerses(book.id, chapter).collect { verses ->
                _uiState.update { it.copy(verses = verses, isLoading = false) }
            }
        }
    }

    fun navigateBack() {
        if (_uiState.value.selectedChapter != null) {
            _uiState.update { it.copy(selectedChapter = null, verses = emptyList(), commentaries = emptyMap()) }
        } else if (_uiState.value.selectedBook != null) {
            _uiState.update { it.copy(selectedBook = null, chapterCount = 0) }
        }
    }
}

class BibleViewModelFactory(private val repository: BibleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BibleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BibleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}