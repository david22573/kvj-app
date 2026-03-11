// HomeViewModel.kt
package com.kjv_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kjv_app.data.repository.BibleRepository
import com.kjv_app.data.repository.UserPreferencesRepository
import com.kjv_app.domain.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val lastReadBook: Book? = null,
    val lastReadChapter: Int? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BibleRepository,
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {

    // Books are static — load once, multicast to all collectors.
    // WhileSubscribed(5000) keeps the cache alive for 5s across
    // recompositions/rotations without re-querying the DB.
    private val books: StateFlow<List<Book>> = repository
        .getAllBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<HomeUiState> = combine(
        books,
        userPrefsRepository.userPreferencesFlow
    ) { bookList, prefs ->
        val book = bookList.find { it.id == prefs.lastReadBookId }
        val chapter = prefs.lastReadChapter.takeIf { it != -1 }
        HomeUiState(
            lastReadBook = book,
            lastReadChapter = chapter,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )
}