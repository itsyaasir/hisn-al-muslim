package com.yasir.hisnalmuslim.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            !appViewModel.uiState.value.isReady
        }

        setContent {
            val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
            NotificationPermissionEffect(
                shouldRequestPermission = uiState.shouldPromptNotificationPermission,
                onPromptHandled = appViewModel::markNotificationPermissionPrompted,
            )
            HisnulMuslimRoot(appViewModel = appViewModel)
        }
    }
}

@Composable
private fun NotificationPermissionEffect(
    shouldRequestPermission: Boolean,
    onPromptHandled: () -> Unit,
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        onPromptHandled()
    }

    LaunchedEffect(shouldRequestPermission) {
        if (!shouldRequestPermission) return@LaunchedEffect
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            onPromptHandled()
            return@LaunchedEffect
        }

        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            onPromptHandled()
        } else {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
