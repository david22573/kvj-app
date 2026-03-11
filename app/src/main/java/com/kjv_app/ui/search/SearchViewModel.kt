package com.kjv_app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kjv_app.data.repository.BibleRepository
import com.kjv_app.domain.model.Book
import com.kjv_app.domain.model.Verse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Pair<Verse, Book>> = emptyList(),
    val isSearching: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BibleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var allBooks: List<Book> = emptyList()

    init {
        viewModelScope.launch {
            allBooks = repository.getAllBooks().first()
        }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query, isSearching = query.length >= 3) }
        searchJob?.cancel()

        if (query.length < 3) {
            _uiState.update { it.copy(results = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            repository.searchVerses(query).collect { verses ->
                val pairedResults = verses.mapNotNull { verse ->
                    val book = allBooks.find { it.id == verse.bookId }
                    if (book != null) verse to book else null
                }
                _uiState.update { it.copy(results = pairedResults, isSearching = false) }
            }
        }
    }

    fun clearQuery() {
        _uiState.update { it.copy(query = "", results = emptyList(), isSearching = false) }
    }
}