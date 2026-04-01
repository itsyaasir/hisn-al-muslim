package com.yasir.hisnalmuslim.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yasir.hisnalmuslim.core.designsystem.HisnulMuslimTheme
import com.yasir.hisnalmuslim.navigation.HisnulMuslimNavHost
import com.yasir.hisnalmuslim.notifications.NotificationOpenTarget

@Composable
fun HisnulMuslimRoot(
    appViewModel: AppViewModel = hiltViewModel(),
    pendingNotificationTarget: NotificationOpenTarget? = null,
    onPendingNotificationTargetConsumed: () -> Unit = {},
) {
    val uiState by appViewModel.uiState.collectAsStateWithLifecycle()

    HisnulMuslimTheme(
        settings = uiState.settings,
    ) {
        Surface {
            HisnulMuslimNavHost(
                pendingNotificationTarget = pendingNotificationTarget,
                onPendingNotificationTargetConsumed = onPendingNotificationTargetConsumed,
            )
        }
    }
}
