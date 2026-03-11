package com.kjv_app.ui.reader

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    bookId: Int,
    chapter: Int,
    onNavigateChapter: (Int, Int) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(bookId, chapter) {
        viewModel.loadChapter(bookId, chapter)
        listState.scrollToItem(0)
    }

    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ReaderUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ReaderUiState.Reading -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.verses) { verse ->
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "${verse.verseNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(top = 4.dp, end = 12.dp)
                                    .width(24.dp)
                            )
                            Text(
                                text = verse.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        val verseCommentaries = state.commentaries[verse.id]
                        if (!verseCommentaries.isNullOrEmpty()) {
                            val accentColor = MaterialTheme.colorScheme.primary
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, start = 36.dp)
                                    .drawBehind {
                                        drawRect(
                                            color = accentColor,
                                            size = Size(3.dp.toPx(), size.height)
                                        )
                                    }
                                    .padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
                            ) {
                                verseCommentaries.forEach { commentary ->
                                    Text(
                                        text = "${commentary.author}:",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = commentary.text,
                                        fontStyle = FontStyle.Italic,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(bottom = 4.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { onNavigateChapter(bookId, state.chapter - 1) },
                            enabled = state.chapter > 1
                        ) {
                            Text("← Previous")
                        }

                        TextButton(
                            onClick = { onNavigateChapter(bookId, state.chapter + 1) },
                            enabled = state.chapter < state.chapterCount
                        ) {
                            Text("Next →")
                        }
                    }
                }
            }
        }
        is ReaderUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        else -> {}
    }
}