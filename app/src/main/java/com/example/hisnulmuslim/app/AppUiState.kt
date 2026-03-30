package com.example.hisnulmuslim.app

import androidx.compose.runtime.Immutable
import com.example.hisnulmuslim.core.model.AppSettings

@Immutable
data class AppUiState(
    val isReady: Boolean = false,
    val settings: AppSettings = AppSettings(),
)
