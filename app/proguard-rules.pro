# Compose rules
-keep class androidx.compose.** { *; }
-keepclasseswithmembers class androidx.compose.** {
    public <methods>;
}

# Room database rules
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class ** { *; }
-keepclasseswithmembers class ** {
    @androidx.room.Dao <methods>;
}

# Keep app models
-keep class com.vinodk.launcher.data.model.** { *; }

# Keep ViewModels
-keep class com.vinodk.launcher.ui.** { *; }

# Keep launcher components
-keep class com.vinodk.launcher.LauncherActivity { *; }

# Keep data repository
-keep class com.vinodk.launcher.data.repository.** { *; }

# Lifecycle
-keep class androidx.lifecycle.** { *; }

# LiveData and Flow
-keep class androidx.compose.runtime.** { *; }
-keep class kotlinx.coroutines.** { *; }
