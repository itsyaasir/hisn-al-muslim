package com.yasir.hisnulmuslim.app

import androidx.compose.runtime.Immutable
import com.yasir.hisnulmuslim.core.model.AppSettings

@Immutable
data class AppUiState(
    val isReady: Boolean = false,
    val settings: AppSettings = AppSettings(),
)
