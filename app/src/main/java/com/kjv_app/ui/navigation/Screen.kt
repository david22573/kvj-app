package com.kjv_app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object BookList : Screen("book_list/{testament}") {
        fun createRoute(testament: String) = "book_list/$testament"
    }

    object ChapterSelection : Screen("chapter_selection/{bookId}") {
        fun createRoute(bookId: Int) = "chapter_selection/$bookId"
    }

    object Reader : Screen("reader/{bookId}/{chapter}") {
        fun createRoute(bookId: Int, chapter: Int) = "reader/$bookId/$chapter"
    }

    object Search : Screen("search")

    object Settings : Screen("settings")
}