package com.vinodk.launcher.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.vinodk.launcher.data.db.LauncherDatabase
import com.vinodk.launcher.data.repository.AppRepository
import com.vinodk.launcher.ui.home.HomeScreen
import com.vinodk.launcher.ui.home.HomeViewModel
import com.vinodk.launcher.ui.reading.ReadingModeScreen
import com.vinodk.launcher.ui.reading.ReadingModeViewModel
import com.vinodk.launcher.ui.settings.SettingsScreen
import com.vinodk.launcher.ui.settings.SettingsViewModel
import com.vinodk.launcher.ui.timers.UsageTimerScreen
import com.vinodk.launcher.ui.timers.UsageTimerViewModel
import android.content.Context

sealed class LauncherRoute {
    object Home : LauncherRoute()
    object ReadingMode : LauncherRoute()
    object UsageTimers : LauncherRoute()
    object Settings : LauncherRoute()
}

data class NavigationState(
    val currentRoute: LauncherRoute = LauncherRoute.Home,
)

class LauncherNavigationViewModel : ViewModel() {
    private val _navigationState = androidx.compose.runtime.mutableStateOf(NavigationState())
    val navigationState: androidx.compose.runtime.State<NavigationState> = _navigationState

    fun navigateTo(route: LauncherRoute) {
        _navigationState.value = _navigationState.value.copy(currentRoute = route)
    }

    fun navigateHome() {
        navigateTo(LauncherRoute.Home)
    }
}

@Composable
fun LauncherNavigation(
    context: Context,
    modifier: Modifier = Modifier
) {
    // Initialize database and repository
    val database = remember {
        Room.databaseBuilder(
            context,
            LauncherDatabase::class.java,
            LauncherDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    val appRepository = remember {
        AppRepository(context, database.appInfoDao())
    }

    // ViewModels
    val homeViewModel = remember {
        HomeViewModelFactory(appRepository).create(HomeViewModel::class.java)
    }

    val readingModeViewModel = remember { ReadingModeViewModel() }
    val usageTimerViewModel = remember {
        UsageTimerViewModelFactory(database.usageLogDao()).create(UsageTimerViewModel::class.java)
    }
    val settingsViewModel = remember { SettingsViewModel() }
    val navigationViewModel = remember { LauncherNavigationViewModel() }

    // UI States
    val homeUiState = homeViewModel.uiState.collectAsState()
    val readingModeUiState = readingModeViewModel.uiState.collectAsState()
    val usageTimerUiState = usageTimerViewModel.uiState.collectAsState()
    val settingsUiState = settingsViewModel.uiState.collectAsState()

    val currentRoute = navigationViewModel.navigationState.value.currentRoute

    // Route Navigation
    when (currentRoute) {
        LauncherRoute.Home -> {
            HomeScreen(
                uiState = homeUiState.value,
                onSearchQueryChange = homeViewModel::updateSearchQuery,
                onAppLaunch = { app ->
                    // Launch app here
                },
                onAppLongPress = { app ->
                    // Show context menu
                },
                modifier = modifier
            )
        }

        LauncherRoute.ReadingMode -> {
            ReadingModeScreen(
                uiState = readingModeUiState.value,
                onToggleReadingMode = readingModeViewModel::toggleReadingMode,
                onSelectBook = readingModeViewModel::selectBook,
                onUpdateProgress = { bookId, page ->
                    readingModeViewModel.updateBookProgress(bookId, page)
                },
                modifier = modifier
            )
        }

        LauncherRoute.UsageTimers -> {
            UsageTimerScreen(
                uiState = usageTimerUiState.value,
                onUpdateLimit = { packageName, limit ->
                    usageTimerViewModel.setDailyLimit(packageName, limit)
                },
                modifier = modifier
            )
        }

        LauncherRoute.Settings -> {
            SettingsScreen(
                uiState = settingsUiState.value,
                onThemeChange = settingsViewModel::setThemeMode,
                onToggleDarkMode = settingsViewModel::toggleDarkMode,
                onToggleFocusGate = settingsViewModel::toggleFocusGate,
                onToggleUsageLimits = settingsViewModel::toggleUsageLimits,
                onToggleScheduling = settingsViewModel::toggleScheduling,
                onToggleReadingMode = settingsViewModel::toggleReadingMode,
                onBatchNotificationsChange = settingsViewModel::setBatchNotificationsMinutes,
                onDefaultDailyLimitChange = settingsViewModel::setDefaultDailyLimit,
                onResetDefaults = settingsViewModel::resetToDefaults,
                onExportSettings = {
                    val json = settingsViewModel.exportSettings()
                    println("Settings exported: $json")
                },
                modifier = modifier
            )
        }
    }
}

class UsageTimerViewModelFactory(
    private val usageLogDao: com.vinodk.launcher.data.db.UsageLogDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsageTimerViewModel::class.java)) {
            return UsageTimerViewModel(usageLogDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
