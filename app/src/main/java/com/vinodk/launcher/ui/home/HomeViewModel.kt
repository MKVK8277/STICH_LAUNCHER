package com.vinodk.launcher.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinodk.launcher.data.model.AppInfo
import com.vinodk.launcher.data.model.AppPriority
import com.vinodk.launcher.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeUiState(
    val pinnedApps: List<AppInfo> = emptyList(),
    val essentialApps: List<AppInfo> = emptyList(),
    val normalApps: List<AppInfo> = emptyList(),
    val lowPriorityApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val filteredApps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class HomeViewModel(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            appRepository.refreshInstalledApps()
        }

        viewModelScope.launch {
            combine(
                appRepository.getPinnedAppsFlow(),
                appRepository.getEssentialAppsFlow(),
                appRepository.getAllAppsFlow(),
                appRepository.getLowPriorityAppsFlow(),
                _searchQuery
            ) { pinned, essential, all, lowPriority, query ->
                val normal = all.filter { 
                    it.priority == AppPriority.NORMAL && !it.isPinned 
                }
                
                val filtered = if (query.isBlank()) {
                    emptyList()
                } else {
                    all.filter { 
                        it.label.contains(query, ignoreCase = true) ||
                        it.packageName.contains(query, ignoreCase = true)
                    }
                }

                HomeUiState(
                    pinnedApps = pinned,
                    essentialApps = essential,
                    normalApps = normal,
                    lowPriorityApps = lowPriority,
                    searchQuery = query,
                    filteredApps = filtered,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun pinApp(packageName: String) {
        viewModelScope.launch {
            appRepository.pinApp(packageName)
        }
    }

    fun updateAppPriority(packageName: String, priority: AppPriority) {
        viewModelScope.launch {
            appRepository.updateAppPriority(packageName, priority)
        }
    }
}
