package com.vinodk.launcher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinodk.launcher.data.model.LauncherSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ThemeMode {
    PAPER_WHITE,
    SEPIA,
    CHARCOAL,
}

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.PAPER_WHITE,
    val isDarkMode: Boolean = false,
    val enableFocusGate: Boolean = true,
    val enableUsageLimits: Boolean = true,
    val enableScheduling: Boolean = true,
    val enableReadingMode: Boolean = true,
    val batchNotificationsMinutes: Int = 60,
    val defaultDailyLimitMinutes: Int = 120,
    val blockedAppsCount: Int = 0,
    val showDeveloperSettings: Boolean = false,
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setThemeMode(theme: ThemeMode) {
        _uiState.value = _uiState.value.copy(themeMode = theme)
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkMode = enabled)
    }

    fun toggleFocusGate(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableFocusGate = enabled)
    }

    fun toggleUsageLimits(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableUsageLimits = enabled)
    }

    fun toggleScheduling(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableScheduling = enabled)
    }

    fun toggleReadingMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableReadingMode = enabled)
    }

    fun setBatchNotificationsMinutes(minutes: Int) {
        _uiState.value = _uiState.value.copy(batchNotificationsMinutes = minutes)
    }

    fun setDefaultDailyLimit(minutes: Int) {
        _uiState.value = _uiState.value.copy(defaultDailyLimitMinutes = minutes)
    }

    fun toggleDeveloperSettings(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(showDeveloperSettings = enabled)
    }

    fun exportSettings(): String {
        val state = _uiState.value
        return """
        {
          "theme": "${state.themeMode.name}",
          "darkMode": ${state.isDarkMode},
          "focusGate": ${state.enableFocusGate},
          "usageLimits": ${state.enableUsageLimits},
          "scheduling": ${state.enableScheduling},
          "readingMode": ${state.enableReadingMode},
          "notificationBatch": ${state.batchNotificationsMinutes},
          "defaultDailyLimit": ${state.defaultDailyLimitMinutes}
        }
        """.trimIndent()
    }

    fun resetToDefaults() {
        _uiState.value = SettingsUiState()
    }
}
