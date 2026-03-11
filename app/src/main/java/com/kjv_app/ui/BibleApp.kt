package com.kjv_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import com.kjv_app.ui.navigation.Screen
import com.kjv_app.ui.home.DashboardScreen
import com.kjv_app.ui.home.HomeViewModel
import com.kjv_app.ui.books.BookListScreen
import com.kjv_app.ui.books.BookListViewModel
import com.kjv_app.ui.reader.ChapterGridScreen
import com.kjv_app.ui.reader.ReaderScreen
import com.kjv_app.ui.reader.ReaderViewModel
import com.kjv_app.ui.search.SearchScreen
import com.kjv_app.ui.search.SearchViewModel
import com.kjv_app.ui.settings.SettingsScreen
import com.kjv_app.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val searchViewModel: SearchViewModel = hiltViewModel()
    val searchUiState by searchViewModel.uiState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "KJV Study Bible",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = currentRoute == Screen.Home.route,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Search") },
                    selected = currentRoute == Screen.Search.route,
                    onClick = {
                        if (currentRoute != Screen.Search.route) {
                            navController.navigate(Screen.Search.route)
                        }
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = currentRoute == Screen.Settings.route,
                    onClick = {
                        if (currentRoute != Screen.Settings.route) {
                            navController.navigate(Screen.Settings.route)
                        }
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (currentRoute == Screen.Search.route) {
                            TextField(
                                value = searchUiState.query,
                                onValueChange = { searchViewModel.updateQuery(it) },
                                placeholder = { Text("Search verses...") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(getScreenTitle(currentRoute))
                        }
                    },
                    navigationIcon = {
                        if (currentRoute == Screen.Home.route) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        } else {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    actions = {
                        if (currentRoute == Screen.Search.route) {
                            if (searchUiState.query.isNotEmpty()) {
                                IconButton(onClick = { searchViewModel.clearQuery() }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear text")
                                }
                            }
                        } else if (
                            currentRoute == Screen.Home.route ||
                            currentRoute?.startsWith("book_list") == true
                        ) {
                            IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        val homeViewModel: HomeViewModel = hiltViewModel()
                        DashboardScreen(
                            viewModel = homeViewModel,
                            onTestamentSelected = { testament ->
                                navController.navigate(Screen.BookList.createRoute(testament))
                            },
                            onContinueReading = { bookId, chapter ->
                                navController.navigate(Screen.Reader.createRoute(bookId, chapter))
                            }
                        )
                    }

                    composable(
                        route = Screen.BookList.route,
                        arguments = listOf(
                            navArgument("testament") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val testament =
                            backStackEntry.arguments?.getString("testament") ?: "Old"
                        val bookListViewModel: BookListViewModel = hiltViewModel()
                        BookListScreen(
                            viewModel = bookListViewModel,
                            testament = testament,
                            onBookSelected = { bookId ->
                                navController.navigate(
                                    Screen.ChapterSelection.createRoute(bookId)
                                )
                            }
                        )
                    }

                    composable(
                        route = Screen.ChapterSelection.route,
                        arguments = listOf(
                            navArgument("bookId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val bookId =
                            backStackEntry.arguments?.getInt("bookId") ?: return@composable
                        val readerViewModel: ReaderViewModel = hiltViewModel()
                        ChapterGridScreen(
                            viewModel = readerViewModel,
                            bookId = bookId,
                            onChapterSelected = { selectedBookId, chapter ->
                                navController.navigate(
                                    Screen.Reader.createRoute(selectedBookId, chapter)
                                )
                            }
                        )
                    }

                    composable(
                        route = Screen.Reader.route,
                        arguments = listOf(
                            navArgument("bookId") { type = NavType.IntType },
                            navArgument("chapter") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val bookId =
                            backStackEntry.arguments?.getInt("bookId") ?: return@composable
                        val chapter =
                            backStackEntry.arguments?.getInt("chapter") ?: return@composable
                        val readerViewModel: ReaderViewModel = hiltViewModel()
                        ReaderScreen(
                            viewModel = readerViewModel,
                            bookId = bookId,
                            chapter = chapter,
                            onNavigateChapter = { targetBookId, targetChapter ->
                                navController.navigate(
                                    Screen.Reader.createRoute(targetBookId, targetChapter)
                                ) {
                                    // ← Fix: use the route pattern, not a filled-in string
                                    popUpTo(Screen.ChapterSelection.route)
                                }
                            }
                        )
                    }

                    composable(Screen.Search.route) {
                        SearchScreen(
                            viewModel = searchViewModel,
                            onVerseSelected = { bookId, chapter ->
                                navController.navigate(Screen.Reader.createRoute(bookId, chapter))
                            }
                        )
                    }

                    composable(Screen.Settings.route) {
                        val settingsViewModel: SettingsViewModel = hiltViewModel()
                        SettingsScreen(viewModel = settingsViewModel)
                    }
                }
            }
        }
    }
}

fun getScreenTitle(currentRoute: String?): String {
    return when {
        currentRoute == Screen.Home.route -> "KJV Bible"
        currentRoute?.startsWith("book_list") == true -> "Books"
        currentRoute?.startsWith("chapter_selection") == true -> "Chapters"
        currentRoute?.startsWith("reader") == true -> "Reader"
        currentRoute == Screen.Search.route -> "Search"
        currentRoute == Screen.Settings.route -> "Settings"
        else -> "KJV Bible"
    }
}