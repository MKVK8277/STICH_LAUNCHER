package com.vinodk.launcher.ui.timers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinodk.launcher.data.db.UsageLogDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

data class AppTimerInfo(
    val packageName: String,
    val label: String,
    val usedMinutesToday: Int = 0,
    val dailyLimitMinutes: Int = 60,
    val isActive: Boolean = false,
    val sessionStartTimeMs: Long = 0,
)

data class UsageTimerUiState(
    val activeTimers: List<AppTimerInfo> = emptyList(),
    val totalUsedToday: Int = 0,
    val todayStartMs: Long = getTodayStartMs(),
    val timerAlerts: List<TimerAlert> = emptyList(),
)

data class TimerAlert(
    val packageName: String,
    val message: String,
    val severity: AlertSeverity,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AlertSeverity {
    INFO,      // Gentle info
    WARNING,   // Approaching limit
    CRITICAL   // Limit exceeded
}

class UsageTimerViewModel(
    private val usageLogDao: UsageLogDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsageTimerUiState())
    val uiState: StateFlow<UsageTimerUiState> = _uiState.asStateFlow()

    private val _activeApps = MutableMapOf<String, AppTimerInfo>()

    init {
        loadTodayUsage()
    }

    private fun loadTodayUsage() {
        viewModelScope.launch(Dispatchers.IO) {
            val todayStart = getTodayStartMs()
            usageLogDao.getTotalDurationSeconds("all_apps", todayStart).collect { totalSeconds ->
                val totalMinutes = totalSeconds / 60
                _uiState.value = _uiState.value.copy(totalUsedToday = totalMinutes)
            }
        }
    }

    fun startAppSession(packageName: String, label: String, dailyLimit: Int = 60) {
        val timerInfo = AppTimerInfo(
            packageName = packageName,
            label = label,
            dailyLimitMinutes = dailyLimit,
            isActive = true,
            sessionStartTimeMs = System.currentTimeMillis()
        )
        _activeApps[packageName] = timerInfo
        updateTimers()
    }

    fun endAppSession(packageName: String) {
        val timer = _activeApps[packageName]
        if (timer != null) {
            val sessionDurationMs = System.currentTimeMillis() - timer.sessionStartTimeMs
            val sessionDurationMinutes = (sessionDurationMs / 1000 / 60).toInt()

            // Update cumulative usage
            val updated = timer.copy(
                isActive = false,
                usedMinutesToday = timer.usedMinutesToday + sessionDurationMinutes
            )

            // Check if limit exceeded
            if (updated.usedMinutesToday > updated.dailyLimitMinutes) {
                addAlert(
                    packageName,
                    "Daily limit exceeded for ${timer.label}",
                    AlertSeverity.CRITICAL
                )
            }

            _activeApps[packageName] = updated
        }
        updateTimers()
    }

    fun setDailyLimit(packageName: String, limitMinutes: Int) {
        val timer = _activeApps[packageName]
        if (timer != null) {
            _activeApps[packageName] = timer.copy(dailyLimitMinutes = limitMinutes)
            updateTimers()
        }
    }

    fun getRemainingTime(packageName: String): Int {
        val timer = _activeApps[packageName] ?: return -1
        val remaining = timer.dailyLimitMinutes - timer.usedMinutesToday
        return if (remaining > 0) remaining else 0
    }

    fun formatDuration(minutes: Int): String {
        return when {
            minutes < 60 -> "${minutes}m"
            else -> {
                val hours = minutes / 60
                val mins = minutes % 60
                if (mins == 0) "${hours}h" else "${hours}h ${mins}m"
            }
        }
    }

    fun formatSessionTime(sessionMs: Long): String {
        val seconds = (sessionMs / 1000) % 60
        val minutes = (sessionMs / 1000 / 60) % 60
        val hours = (sessionMs / 1000 / 60 / 60)

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun getUsagePercentage(packageName: String): Float {
        val timer = _activeApps[packageName] ?: return 0f
        return timer.usedMinutesToday.toFloat() / timer.dailyLimitMinutes.coerceAtLeast(1)
    }

    private fun addAlert(packageName: String, message: String, severity: AlertSeverity) {
        val alert = TimerAlert(packageName, message, severity)
        val alerts = _uiState.value.timerAlerts.toMutableList()
        alerts.add(alert)
        // Keep only last 10 alerts
        if (alerts.size > 10) alerts.removeAt(0)
        _uiState.value = _uiState.value.copy(timerAlerts = alerts)
    }

    private fun updateTimers() {
        _uiState.value = _uiState.value.copy(
            activeTimers = _activeApps.values.toList()
        )
    }

    companion object {
        private fun getTodayStartMs(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            return calendar.timeInMillis
        }
    }
}

// Extension for easy formatting
fun Int.formatMinutes(): String {
    return when {
        this < 60 -> "${this}m"
        else -> {
            val hours = this / 60
            val mins = this % 60
            if (mins == 0) "${hours}h" else "${hours}h ${mins}m"
        }
    }
}
