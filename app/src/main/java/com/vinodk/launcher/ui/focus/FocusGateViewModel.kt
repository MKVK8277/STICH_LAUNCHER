package com.vinodk.launcher.ui.focus

import androidx.lifecycle.ViewModel
import com.vinodk.launcher.data.model.AccessReason
import com.vinodk.launcher.data.model.AppAccessRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

data class FocusGateUiState(
    val accessRequest: AppAccessRequest? = null,
    val showGate: Boolean = false,
    val remainingUnlockSeconds: Int = 0,
    val allowance: String = "",
)

class FocusGateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FocusGateUiState())
    val uiState: StateFlow<FocusGateUiState> = _uiState.asStateFlow()

    fun requestAppAccess(request: AppAccessRequest) {
        val allowance = when (request.reason) {
            AccessReason.DAILY_LIMIT_EXCEEDED -> "Daily limit reached. Open in 15 min?"
            AccessReason.NOT_IN_SCHEDULE -> "Not in allowed time window (8am-5pm). Unlock?"
            AccessReason.GATED_BY_FOCUS -> "This app is in low priority. Confirm access?"
        }

        _uiState.value = FocusGateUiState(
            accessRequest = request,
            showGate = true,
            allowance = allowance
        )
    }

    fun allowAccess() {
        _uiState.value = _uiState.value.copy(showGate = false)
    }

    fun denyAccess() {
        _uiState.value = _uiState.value.copy(showGate = false)
    }

    fun delayAccess(minutes: Int) {
        _uiState.value = _uiState.value.copy(
            remainingUnlockSeconds = (minutes * 60),
            showGate = false
        )
    }

    fun clearRequest() {
        _uiState.value = FocusGateUiState()
    }
}
