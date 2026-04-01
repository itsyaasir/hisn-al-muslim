package com.yasir.hisnalmuslim.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasir.hisnalmuslim.data.local.seed.SeedImporter
import com.yasir.hisnalmuslim.data.repository.DhikrRepository
import com.yasir.hisnalmuslim.data.repository.SettingsRepository
import com.yasir.hisnalmuslim.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dhikrRepository: DhikrRepository,
    seedImporter: SeedImporter,
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val isReady = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            seedImporter.importIfNeeded()
            dhikrRepository.observeCollections().first()
            dhikrRepository.observeAllDhikrOrdered().first()
            launch {
                settingsRepository.observeSettings().collectLatest { settings ->
                    reminderScheduler.syncAll(settings)
                }
            }
            isReady.value = true
        }
    }

    val uiState: StateFlow<AppUiState> = combine(
        isReady,
        settingsRepository.observeSettings(),
        settingsRepository.observeNotificationPermissionPrompted(),
    ) { isReady, settings, notificationPermissionPrompted ->
        AppUiState(
            isReady = isReady,
            settings = settings,
            shouldPromptNotificationPermission = isReady && !notificationPermissionPrompted,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppUiState(),
    )

    fun markNotificationPermissionPrompted() {
        viewModelScope.launch { settingsRepository.markNotificationPermissionPrompted() }
    }
}
