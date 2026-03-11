package com.kjv_app.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kjv_app.data.repository.BibleRepository
import com.kjv_app.domain.model.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookListUiState(
    val books: List<Book> = emptyList(),
    val testament: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val repository: BibleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookListUiState())
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    fun loadBooks(testament: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, testament = testament) }
            val allBooks = repository.getAllBooks().first()
            val filtered = allBooks.filter { it.testament == testament }
            _uiState.update { it.copy(books = filtered, isLoading = false) }
        }
    }
}