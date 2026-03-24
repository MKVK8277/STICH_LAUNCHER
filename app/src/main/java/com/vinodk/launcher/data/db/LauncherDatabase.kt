package com.vinodk.launcher.data.db

import androidx.room.*
import com.vinodk.launcher.data.model.AppInfo
import com.vinodk.launcher.data.model.FocusSchedule
import com.vinodk.launcher.data.model.UsageLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AppInfoDao {
    @Query("SELECT * FROM apps ORDER BY isPinned DESC, label ASC")
    fun getAllAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM apps WHERE priority = 'ESSENTIAL' ORDER BY isPinned DESC, label ASC")
    fun getEssentialAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM apps WHERE priority = 'LOW_PRIORITY' ORDER BY label ASC")
    fun getLowPriorityAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM apps WHERE isPinned = 1 ORDER BY label ASC LIMIT 5")
    fun getPinnedAppsFlow(): Flow<List<AppInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppInfo>)

    @Update
    suspend fun updateApp(app: AppInfo)

    @Delete
    suspend fun deleteApp(app: AppInfo)

    @Query("DELETE FROM apps")
    suspend fun clearApps()
}

@Dao
interface FocusScheduleDao {
    @Query("SELECT * FROM focus_schedules WHERE packageName = :packageName")
    fun getSchedulesForApp(packageName: String): Flow<List<FocusSchedule>>

    @Query("SELECT * FROM focus_schedules")
    fun getAllSchedules(): Flow<List<FocusSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: FocusSchedule)

    @Update
    suspend fun updateSchedule(schedule: FocusSchedule)

    @Delete
    suspend fun deleteSchedule(schedule: FocusSchedule)
}

@Dao
interface UsageLogDao {
    @Query("SELECT * FROM usage_logs WHERE packageName = :packageName AND timestamp > :sinceMs ORDER BY timestamp DESC")
    fun getLogsForApp(packageName: String, sinceMs: Long): Flow<List<UsageLog>>

    @Query("SELECT COALESCE(SUM(durationSeconds), 0) FROM usage_logs WHERE packageName = :packageName AND timestamp > :sinceMs")
    fun getTotalDurationSeconds(packageName: String, sinceMs: Long): Flow<Int>

    @Insert
    suspend fun insertLog(log: UsageLog)

    @Query("DELETE FROM usage_logs WHERE timestamp < :beforeMs")
    suspend fun deleteOldLogs(beforeMs: Long)
}

@Database(
    entities = [AppInfo::class, FocusSchedule::class, UsageLog::class],
    version = 1,
    exportSchema = false
)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun appInfoDao(): AppInfoDao
    abstract fun focusScheduleDao(): FocusScheduleDao
    abstract fun usageLogDao(): UsageLogDao

    companion object {
        const val DATABASE_NAME = "launcher.db"
    }
}
