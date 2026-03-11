package com.kjv_app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kjv_app.data.repository.ThemePreference
import com.kjv_app.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    val userPreferences = userPrefs.userPreferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateThemePreference(preference: ThemePreference) {
        viewModelScope.launch { userPrefs.updateThemePreference(preference) }
    }

    fun clearHistory() {
        viewModelScope.launch { userPrefs.clearLastRead() }
    }
}