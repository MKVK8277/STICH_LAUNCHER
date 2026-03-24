package com.vinodk.launcher.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.vinodk.launcher.data.db.AppInfoDao
import com.vinodk.launcher.data.model.AppInfo
import com.vinodk.launcher.data.model.AppPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(
    private val context: Context,
    private val appInfoDao: AppInfoDao
) {
    fun getAllAppsFlow(): Flow<List<AppInfo>> = appInfoDao.getAllAppsFlow()

    fun getEssentialAppsFlow(): Flow<List<AppInfo>> = appInfoDao.getEssentialAppsFlow()

    fun getLowPriorityAppsFlow(): Flow<List<AppInfo>> = appInfoDao.getLowPriorityAppsFlow()

    fun getPinnedAppsFlow(): Flow<List<AppInfo>> = appInfoDao.getPinnedAppsFlow()

    suspend fun refreshInstalledApps() {
        withContext(Dispatchers.Default) {
            val pm = context.packageManager
            val installedApps = try {
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                    .mapNotNull { appInfo ->
                        try {
                            val label = pm.getApplicationLabel(appInfo).toString()
                            AppInfo(
                                packageName = appInfo.packageName,
                                label = label,
                                priority = categorizeApp(appInfo.packageName)
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
            } catch (e: Exception) {
                emptyList()
            }

            if (installedApps.isNotEmpty()) {
                appInfoDao.insertApps(installedApps)
            }
        }
    }

    suspend fun updateAppPriority(packageName: String, priority: AppPriority) {
        withContext(Dispatchers.IO) {
            val allApps = try {
                getAllAppsFlow().collect { list ->
                    list.find { it.packageName == packageName }?.let {
                        appInfoDao.updateApp(it.copy(priority = priority))
                    }
                }
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    suspend fun pinApp(packageName: String) {
        withContext(Dispatchers.IO) {
            try {
                getAllAppsFlow().collect { list ->
                    list.find { it.packageName == packageName }?.let {
                        appInfoDao.updateApp(it.copy(isPinned = true))
                    }
                }
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    private fun categorizeApp(packageName: String): AppPriority {
        return when {
            packageName in ESSENTIAL_APPS -> AppPriority.ESSENTIAL
            packageName in SOCIAL_APPS -> AppPriority.LOW_PRIORITY
            else -> AppPriority.NORMAL
        }
    }

    companion object {
        private val ESSENTIAL_APPS = setOf(
            "com.android.phone",
            "com.android.messaging",
            "com.android.settings",
            "com.google.android.dialer",
            "com.android.contacts",
            "com.android.vending", // Play Store
        )

        private val SOCIAL_APPS = setOf(
            "com.facebook.katana",
            "com.twitter.android",
            "com.instagram.android",
            "com.tiktok.android",
            "com.reddit.frontpage",
            "com.snapchat.android",
            "com.whatsapp",
            "com.telegram.messenger",
        )
    }
}
