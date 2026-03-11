package com.kjv_app.ui.books

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun BookListScreen(
    viewModel: BookListViewModel,
    testament: String,
    onBookSelected: (Int) -> Unit
) {
    LaunchedEffect(testament) {
        viewModel.loadBooks(testament)
    }

    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.books) { book ->
                ListItem(
                    headlineContent = { Text(book.name, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.clickable { onBookSelected(book.id) },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}