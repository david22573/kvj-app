package com.kjv_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
//import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kjv_app.data.local.BibleDatabase
import com.kjv_app.data.repository.BibleRepository
import com.kjv_app.ui.theme.KjvappTheme
import com.kjv_app.ui.viewmodel.BibleViewModel
import com.kjv_app.ui.viewmodel.BibleViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = BibleDatabase.getDatabase(applicationContext)
        val repository = BibleRepository(database.bibleDao())
        val factory = BibleViewModelFactory(repository)

        setContent {
            KjvappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bibleViewModel: BibleViewModel = viewModel(factory = factory)
                    BibleApp(viewModel = bibleViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleApp(viewModel: BibleViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    DisposableEffect(uiState.selectedBook, uiState.selectedChapter) {
        val callback = object : OnBackPressedCallback(
            uiState.selectedBook != null || uiState.selectedChapter != null
        ) {
            override fun handleOnBackPressed() {
                viewModel.navigateBack()
            }
        }
        backDispatcher?.addCallback(callback)
        onDispose { callback.remove() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when {
                        uiState.selectedChapter != null -> "${uiState.selectedBook?.name} ${uiState.selectedChapter}"
                        uiState.selectedBook != null -> uiState.selectedBook?.name ?: "Select Chapter"
                        else -> "KJV Bible"
                    }
                    Text(title)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading && uiState.books.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.selectedChapter != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(uiState.verses) { verse ->
                            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                                Text(
                                    text = "${verse.verseNumber}. ${verse.text}",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                // Render commentaries if they exist for this verse
                                val verseCommentaries = uiState.commentaries[verse.id]
                                if (!verseCommentaries.isNullOrEmpty()) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp, start = 16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            verseCommentaries.forEach { commentary ->
                                                Text(
                                                    text = "${commentary.author}:",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                                Text(
                                                    text = commentary.text,
                                                    fontStyle = FontStyle.Italic,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                uiState.selectedBook != null -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 64.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items((1..uiState.chapterCount).toList()) { chapter ->
                            Button(onClick = { viewModel.selectChapter(chapter) }) {
                                Text(chapter.toString())
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.books) { book ->
                            ListItem(
                                headlineContent = { Text(book.name, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text(book.testament) },
                                modifier = Modifier.clickable { viewModel.selectBook(book) }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}