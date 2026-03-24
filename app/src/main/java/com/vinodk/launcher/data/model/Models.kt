package com.vinodk.launcher.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.graphics.drawable.Drawable
import java.time.LocalTime

@Entity(tableName = "apps")
data class AppInfo(
    @PrimaryKey
    val packageName: String,
    val label: String,
    val priority: AppPriority = AppPriority.NORMAL,
    val isPinned: Boolean = false,
    val usageMinutesToday: Int = 0,
    val lastOpenedTime: Long = 0L,
    val dailyLimitMinutes: Int = 0,
)

enum class AppPriority {
    ESSENTIAL,    // Always accessible, no friction
    NORMAL,       // Standard access, no gates
    LOW_PRIORITY  // Requires confirmation gate, can be scheduled
}

data class AppDisplayItem(
    val appInfo: AppInfo,
    val icon: Drawable?,
)

@Entity(tableName = "focus_schedules")
data class FocusSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packageName: String,
    val startTime: String,  // HH:mm format
    val endTime: String,    // HH:mm format
    val daysOfWeek: String, // Bitmask: "0111110" = Mon-Fri
)

@Entity(tableName = "usage_logs")
data class UsageLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packageName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
)

data class AppAccessRequest(
    val packageName: String,
    val label: String,
    val reason: AccessReason,
)

enum class AccessReason {
    DAILY_LIMIT_EXCEEDED,
    NOT_IN_SCHEDULE,
    GATED_BY_FOCUS,
}

data class LauncherSettings(
    val isDarkMode: Boolean = false,
    val usePaperWhiteTheme: Boolean = true,
    val useSepiaTheme: Boolean = false,
    val useCharcoalTheme: Boolean = false,
    val batchNotificationsMinutes: Int = 60,
    val enableUsageLimits: Boolean = true,
    val enableScheduling: Boolean = true,
    val readingModeEnabled: Boolean = false,
)
