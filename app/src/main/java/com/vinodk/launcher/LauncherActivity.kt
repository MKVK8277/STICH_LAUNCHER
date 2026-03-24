package com.vinodk.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.vinodk.launcher.data.db.LauncherDatabase
import com.vinodk.launcher.data.repository.AppRepository
import com.vinodk.launcher.ui.focus.FocusGateOverlay
import com.vinodk.launcher.ui.focus.FocusGateViewModel
import com.vinodk.launcher.ui.home.HomeScreen
import com.vinodk.launcher.ui.home.HomeViewModel
import com.vinodk.launcher.ui.theme.LauncherTheme

class LauncherActivity : ComponentActivity() {

    private lateinit var database: LauncherDatabase
    private lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and repository
        database = Room.databaseBuilder(
            applicationContext,
            LauncherDatabase::class.java,
            LauncherDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

        appRepository = AppRepository(applicationContext, database.appInfoDao())

        setContent {
            LauncherTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val homeViewModel = remember {
                        HomeViewModelFactory(appRepository).create(HomeViewModel::class.java)
                    }
                    val focusGateViewModel = remember {
                        FocusGateViewModel()
                    }

                    val homeUiState by homeViewModel.uiState.collectAsState()
                    val focusGateState by focusGateViewModel.uiState.collectAsState()

                    HomeScreen(
                        uiState = homeUiState,
                        onSearchQueryChange = homeViewModel::updateSearchQuery,
                        onAppLaunch = { app ->
                            launchApp(app.packageName)
                        },
                        onAppLongPress = { app ->
                            // TODO: Show app context menu
                        }
                    )

                    if (focusGateState.showGate && focusGateState.accessRequest != null) {
                        FocusGateOverlay(
                            request = focusGateState.accessRequest,
                            onAllow = {
                                focusGateViewModel.allowAccess()
                                focusGateState.accessRequest?.let {
                                    launchApp(it.packageName)
                                }
                            },
                            onDeny = focusGateViewModel::denyAccess,
                            onDelay5Min = {
                                focusGateViewModel.delayAccess(5)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun launchApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}

class HomeViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(appRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
