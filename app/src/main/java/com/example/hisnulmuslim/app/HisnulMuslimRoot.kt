package com.example.hisnulmuslim.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hisnulmuslim.core.designsystem.HisnulMuslimTheme
import com.example.hisnulmuslim.navigation.HisnulMuslimNavHost

@Composable
fun HisnulMuslimRoot(
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val uiState by appViewModel.uiState.collectAsStateWithLifecycle()

    HisnulMuslimTheme(
        settings = uiState.settings,
    ) {
        Surface {
            if (uiState.isReady) {
                HisnulMuslimNavHost()
            } else {
                AppLoadingScreen()
            }
        }
    }
}
