package com.kjv_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kjv_app.data.repository.ThemePreference
import com.kjv_app.data.repository.UserPreferencesRepository
import com.kjv_app.ui.BibleApp
import com.kjv_app.ui.theme.KjvappTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before setContent — makes status bar and
        // nav bar transparent so we can control their appearance
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val prefs by userPreferencesRepository.userPreferencesFlow
                .collectAsStateWithLifecycle(
                    initialValue = null,
                    lifecycle = lifecycle
                )

            val systemDark = isSystemInDarkTheme()

            val darkTheme = when (prefs?.themePreference ?: ThemePreference.SYSTEM) {
                ThemePreference.DARK   -> true
                ThemePreference.LIGHT  -> false
                ThemePreference.SYSTEM -> systemDark
            }

            // Sync status bar and nav bar icon appearance with the
            // resolved theme on every recomposition where it changes
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = this@MainActivity.window
                    WindowInsetsControllerCompat(window, view).apply {
                        isAppearanceLightStatusBars     = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                }
            }

            KjvappTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BibleApp()
                }
            }
        }
    }
}