package com.yasir.hisnalmuslim.app

import androidx.compose.runtime.Immutable
import com.yasir.hisnalmuslim.core.model.AppSettings

@Immutable
data class AppUiState(
    val isReady: Boolean = false,
    val settings: AppSettings = AppSettings(),
    val shouldPromptNotificationPermission: Boolean = false,
)
