package com.kjv_app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kjv_app.data.repository.ThemePreference

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val prefs by viewModel.userPreferences.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )
        }

        item {
            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = {
                    // Segmented button row — System / Light / Dark
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        ThemePreference.entries.forEachIndexed { index, preference ->
                            SegmentedButton(
                                selected = (prefs?.themePreference ?: ThemePreference.SYSTEM)
                                        == preference,
                                onClick = { viewModel.updateThemePreference(preference) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = ThemePreference.entries.size
                                ),
                                label = { Text(preference.label) }
                            )
                        }
                    }
                },
                leadingContent = {
                    Icon(Icons.Default.Contrast, contentDescription = null)
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        item {
            ListItem(
                headlineContent = { Text("Text Size") },
                supportingContent = { Text("Adjust reading font size") },
                leadingContent = {
                    Icon(Icons.Default.FormatSize, contentDescription = null)
                },
                modifier = Modifier.clickable { /* Future enhancement */ },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        item {
            Text(
                text = "Data & History",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )
        }

        item {
            ListItem(
                headlineContent = { Text("Clear Reading History") },
                supportingContent = { Text("Removes your last read location") },
                leadingContent = {
                    Icon(Icons.Default.History, contentDescription = null)
                },
                modifier = Modifier.clickable { viewModel.clearHistory() },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        item {
            Text(
                text = "About",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )
        }

        item {
            ListItem(
                headlineContent = { Text("Version") },
                supportingContent = { Text("1.0.0") },
                leadingContent = {
                    Icon(Icons.Default.Info, contentDescription = null)
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }
}