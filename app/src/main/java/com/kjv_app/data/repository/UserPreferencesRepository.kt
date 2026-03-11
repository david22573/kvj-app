package com.kjv_app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

enum class ThemePreference(val value: Int) {
    SYSTEM(0), LIGHT(1), DARK(2);

    val label: String get() = when (this) {
        SYSTEM -> "System"
        LIGHT  -> "Light"
        DARK   -> "Dark"
    }

    companion object {
        fun fromValue(value: Int) =
            entries.firstOrNull { it.value == value } ?: SYSTEM
    }
}

data class UserPreferences(
    val lastReadBookId: Int,
    val lastReadChapter: Int,
    val themePreference: ThemePreference,
    val fontSize: Int
)

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val LAST_READ_BOOK_ID  = intPreferencesKey("last_read_book_id")
        val LAST_READ_CHAPTER  = intPreferencesKey("last_read_chapter")
        val THEME_PREFERENCE   = intPreferencesKey("theme_preference")
        val FONT_SIZE          = intPreferencesKey("font_size")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            UserPreferences(
                lastReadBookId  = preferences[LAST_READ_BOOK_ID] ?: -1,
                lastReadChapter = preferences[LAST_READ_CHAPTER] ?: -1,
                themePreference = ThemePreference.fromValue(
                    preferences[THEME_PREFERENCE] ?: ThemePreference.SYSTEM.value
                ),
                fontSize        = preferences[FONT_SIZE] ?: 16
            )
        }

    suspend fun updateLastRead(bookId: Int, chapter: Int) {
        dataStore.edit { preferences ->
            preferences[LAST_READ_BOOK_ID] = bookId
            preferences[LAST_READ_CHAPTER] = chapter
        }
    }

    suspend fun clearLastRead() {
        dataStore.edit { preferences ->
            preferences.remove(LAST_READ_BOOK_ID)
            preferences.remove(LAST_READ_CHAPTER)
        }
    }

    suspend fun updateThemePreference(preference: ThemePreference) {
        dataStore.edit { preferences ->
            preferences[THEME_PREFERENCE] = preference.value
        }
    }
}