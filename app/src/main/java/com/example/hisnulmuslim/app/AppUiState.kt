package com.example.hisnulmuslim.app

import com.example.hisnulmuslim.core.model.AppSettings

data class AppUiState(
    val isReady: Boolean = false,
    val settings: AppSettings = AppSettings(),
)
