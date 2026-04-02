package com.yasir.hisnalmuslim.feature.settings

import android.Manifest
import android.app.LocaleConfig
import android.app.LocaleManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.LocaleList
import android.provider.Settings
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yasir.hisnalmuslim.R
import com.yasir.hisnalmuslim.core.designsystem.appTopBarContainerColor
import com.yasir.hisnalmuslim.core.designsystem.appTopBarColors
import com.yasir.hisnalmuslim.core.designsystem.detailTopBarContainerColor
import com.yasir.hisnalmuslim.core.designsystem.detailTopBarColors
import com.yasir.hisnalmuslim.core.designsystem.groupedTileContainerColor
import com.yasir.hisnalmuslim.core.designsystem.LocalAppFonts
import com.yasir.hisnalmuslim.core.model.ArabicFontFamily
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.CollectionTitleLanguage
import com.yasir.hisnalmuslim.core.model.DefaultThemeSeedColor
import com.yasir.hisnalmuslim.core.model.RepeatableReminderPattern
import com.yasir.hisnalmuslim.core.model.ReminderKind
import com.yasir.hisnalmuslim.core.model.ThemeMode
import com.yasir.hisnalmuslim.core.designsystem.mergePaddingValues
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

private enum class SettingsPage {
    Main,
    Appearance,
    Reading,
    Notifications,
    About,
}

private val TopGroupRadius = 28.dp
private val InnerGroupRadius = 8.dp
private val BottomGroupRadius = 28.dp
private val PaneMaxWidth = 600.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val settings by viewModel.uiState.collectAsStateWithLifecycle()
    var currentPage by rememberSaveable { mutableStateOf(SettingsPage.Main) }
    var showLocaleSheet by rememberSaveable { mutableStateOf(false) }
    val localeSelectorEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun navigateTo(page: SettingsPage) {
        currentPage = page
    }

    fun showLanguageSheet() {
        showLocaleSheet = true
    }

    fun hideLanguageSheet() {
        showLocaleSheet = false
    }

    val localeLabel = remember(configuration) {
        val locale = configuration.locales[0]
        locale.displayName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }

    val packageInfo = remember(context) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0),
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
        }.getOrNull()
    }
    val versionLabel = packageInfo?.versionName ?: "1.0"
    val versionCodeLabel = remember(packageInfo) {
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo?.longVersionCode ?: 1L
        } else {
            @Suppress("DEPRECATION")
            packageInfo?.versionCode?.toLong() ?: 1L
        }
        versionCode.toString()
    }

    if (showLocaleSheet && localeSelectorEnabled) {
        SettingsLocaleBottomSheet(
            onDismiss = ::hideLanguageSheet,
        )
    }

    when (currentPage) {
        SettingsPage.Main -> SettingsMainPage(
            contentPadding = contentPadding,
            localeLabel = localeLabel.takeIf { localeSelectorEnabled },
            versionLabel = versionLabel,
            onOpenAppearance = { navigateTo(SettingsPage.Appearance) },
            onOpenReading = { navigateTo(SettingsPage.Reading) },
            onOpenNotifications = { navigateTo(SettingsPage.Notifications) },
            onOpenLanguage = ::showLanguageSheet,
            onOpenAbout = { navigateTo(SettingsPage.About) },
            onResetFavorites = viewModel::clearFavorites,
        )

        SettingsPage.Appearance -> SettingsAppearancePage(
            contentPadding = contentPadding,
            settings = settings,
            onBack = { navigateTo(SettingsPage.Main) },
            onFontScaleChange = viewModel::setFontScale,
            onThemeModeChange = viewModel::setThemeMode,
            onDynamicColorChange = viewModel::setDynamicColor,
            onPureBlackThemeChange = viewModel::setPureBlackTheme,
            onThemeSeedColorChange = viewModel::setThemeSeedColor,
        )

        SettingsPage.Reading -> SettingsReadingPage(
            contentPadding = contentPadding,
            settings = settings,
            onBack = { navigateTo(SettingsPage.Main) },
            onCollectionTitleLanguageChange = viewModel::setCollectionTitleLanguage,
            onArabicFontFamilyChange = viewModel::setArabicFontFamily,
            onArabicFontScaleChange = viewModel::setArabicFontScale,
            onTransliterationFontScaleChange = viewModel::setTransliterationFontScale,
            onTranslationFontScaleChange = viewModel::setTranslationFontScale,
            onShowTransliterationChange = viewModel::setShowTransliteration,
            onShowTranslationChange = viewModel::setShowTranslation,
            onShowReferenceChange = viewModel::setShowReference,
        )

        SettingsPage.Notifications -> SettingsNotificationsPage(
            contentPadding = contentPadding,
            settings = settings,
            onBack = { navigateTo(SettingsPage.Main) },
            onNotificationsEnabledChange = viewModel::setNotificationsEnabled,
            onReminderEnabledChange = viewModel::setReminderEnabled,
            onReminderMinutesChange = viewModel::setReminderMinutes,
            onReminderRingtoneUriChange = viewModel::setReminderRingtoneUri,
            onRepeatableReminderPatternChange = viewModel::setRepeatableReminderPattern,
        )

        SettingsPage.About -> SettingsAboutPage(
            contentPadding = contentPadding,
            versionLabel = versionLabel,
            versionCodeLabel = versionCodeLabel,
            onBack = { navigateTo(SettingsPage.Main) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsMainPage(
    contentPadding: PaddingValues,
    localeLabel: String?,
    versionLabel: String,
    onOpenAppearance: () -> Unit,
    onOpenReading: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenAbout: () -> Unit,
    onResetFavorites: () -> Unit,
) {
    var showResetFavoritesDialog by rememberSaveable { mutableStateOf(false) }
    val hasLanguageTile = localeLabel != null
    val appGroupCount = if (hasLanguageTile) 2 else 1

    fun openResetFavoritesDialog() {
        showResetFavoritesDialog = true
    }

    fun dismissResetFavoritesDialog() {
        showResetFavoritesDialog = false
    }

    fun confirmResetFavorites() {
        dismissResetFavoritesDialog()
        onResetFavorites()
    }

    SettingsPageScaffold(
        contentPadding = contentPadding,
        title = "Settings",
        rootPage = true,
    ) {
        item { Spacer(Modifier.height(14.dp)) }

        item {
            SettingsGroup {
                SettingsNavigationTile(
                    shape = settingsGroupShape(0, 3),
                    icon = { SettingsIcon(Icons.Outlined.Palette) },
                    title = "Appearance",
                    subtitle = "Theme, color scheme, black theme",
                    onClick = onOpenAppearance,
                )
                SettingsNavigationTile(
                    shape = settingsGroupShape(1, 3),
                    icon = { SettingsIcon(Icons.Outlined.TextFields) },
                    title = "Reading",
                    subtitle = "Fonts, transliteration, translation, reference",
                    onClick = onOpenReading,
                )
                SettingsNavigationTile(
                    shape = settingsGroupShape(2, 3),
                    icon = { SettingsIcon(Icons.Outlined.NotificationsNone) },
                    title = "Reminders",
                    subtitle = "Reminder preferences",
                    onClick = onOpenNotifications,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsGroup {
                if (hasLanguageTile) {
                    SettingsNavigationTile(
                        shape = settingsGroupShape(0, 2),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.language),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp),
                            )
                        },
                        title = "Language",
                        subtitle = localeLabel,
                        onClick = onOpenLanguage,
                    )
                }
                SettingsNavigationTile(
                    shape = settingsGroupShape(appGroupCount - 1, appGroupCount),
                    icon = { SettingsIcon(Icons.Outlined.Info) },
                    title = "About",
                    subtitle = "Version $versionLabel",
                    onClick = onOpenAbout,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                TextButton(onClick = ::openResetFavoritesDialog) {
                    Text("Reset Favorites")
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }

    if (showResetFavoritesDialog) {
        AlertDialog(
            onDismissRequest = ::dismissResetFavoritesDialog,
            title = { Text("Reset Favorites") },
            text = {
                Text("Are you sure you want to clear all saved favorites?")
            },
            confirmButton = {
                TextButton(onClick = ::confirmResetFavorites) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = ::dismissResetFavoritesDialog) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun SettingsNotificationsPage(
    contentPadding: PaddingValues,
    settings: AppSettings,
    onBack: () -> Unit,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onReminderEnabledChange: (ReminderKind, Boolean) -> Unit,
    onReminderMinutesChange: (ReminderKind, Int) -> Unit,
    onReminderRingtoneUriChange: (ReminderKind, String?) -> Unit,
    onRepeatableReminderPatternChange: (RepeatableReminderPattern) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val notificationsEnabled = settings.notificationsEnabled
    var systemNotificationsEnabled by remember(context) {
        mutableStateOf(areSystemNotificationsEnabled(context))
    }
    var ringtoneTarget by remember { mutableStateOf<ReminderKind?>(null) }
    var showRepeatablePatternSheet by rememberSaveable { mutableStateOf(false) }
    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val pickedUri = if (result.resultCode == android.app.Activity.RESULT_OK) {
            @Suppress("DEPRECATION")
            result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        } else {
            null
        }
        ringtoneTarget?.let { target ->
            onReminderRingtoneUriChange(target, pickedUri?.toString())
        }
        ringtoneTarget = null
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            systemNotificationsEnabled = true
            onNotificationsEnabledChange(true)
        } else {
            systemNotificationsEnabled = areSystemNotificationsEnabled(context)
        }
    }

    DisposableEffect(context, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                systemNotificationsEnabled = areSystemNotificationsEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    fun requestNotificationsEnabled(enabled: Boolean) {
        if (!enabled) {
            onNotificationsEnabledChange(false)
            return
        }
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            systemNotificationsEnabled = true
            onNotificationsEnabledChange(true)
        } else {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun openNotificationSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
        context.startActivity(intent)
    }

    fun openRepeatablePatternSheet() {
        showRepeatablePatternSheet = true
    }

    fun dismissRepeatablePatternSheet() {
        showRepeatablePatternSheet = false
    }

    fun pickReminderTime(currentMinutes: Int, onMinutesSelected: (Int) -> Unit) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                onMinutesSelected((hourOfDay * 60) + minute)
            },
            currentMinutes / 60,
            currentMinutes % 60,
            DateFormat.is24HourFormat(context),
        ).show()
    }

    fun pickReminderRingtone(kind: ReminderKind, existingUri: String?) {
        ringtoneTarget = kind
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI)
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                existingUri?.let(Uri::parse) ?: Settings.System.DEFAULT_NOTIFICATION_URI,
            )
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
        }
        ringtonePickerLauncher.launch(intent)
    }

    if (showRepeatablePatternSheet) {
        SettingsRepeatablePatternBottomSheet(
            selectedPattern = settings.repeatableReminderPattern,
            onPatternSelected = onRepeatableReminderPatternChange,
            onDismiss = ::dismissRepeatablePatternSheet,
        )
    }

    SettingsPageScaffold(
        contentPadding = contentPadding,
        title = "Reminders",
        subtitle = "Settings",
        onBack = onBack,
    ) {
        item { Spacer(Modifier.height(14.dp)) }

        if (!systemNotificationsEnabled) {
            item {
                SettingsReminderPermissionNotice(
                    onOpenSettings = ::openNotificationSettings,
                )
            }

            item { Spacer(Modifier.height(12.dp)) }
        }

        item {
            SettingsGroup {
                SettingsSwitchTile(
                    shape = settingsGroupShape(0, 1),
                    icon = { SettingsIcon(Icons.Outlined.NotificationsActive) },
                    title = "Enable reminders",
                    checked = notificationsEnabled,
                    onCheckedChange = ::requestNotificationsEnabled,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item { SettingsSectionLabel("Morning") }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsReminderGroup(
                title = "Morning reminders",
                description = "Start the day with remembrance.",
                enabledIcon = { SettingsIcon(Icons.Outlined.WbSunny) },
                notificationsEnabled = notificationsEnabled,
                enabled = settings.morningReminderEnabled,
                detailLabel = formatReminderTime(context, settings.morningReminderMinutes),
                ringtoneLabel = reminderRingtoneLabel(context, settings.morningReminderRingtoneUri),
                onEnabledChange = { onReminderEnabledChange(ReminderKind.MORNING, it) },
                onPickDetail = {
                    pickReminderTime(settings.morningReminderMinutes) {
                        onReminderMinutesChange(ReminderKind.MORNING, it)
                    }
                },
                onPickRingtone = {
                    pickReminderRingtone(ReminderKind.MORNING, settings.morningReminderRingtoneUri)
                },
            )
        }

        item { Spacer(Modifier.height(12.dp)) }

        item { SettingsSectionLabel("Evening") }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsReminderGroup(
                title = "Evening reminders",
                description = "Wind down with remembrance.",
                enabledIcon = { SettingsIcon(Icons.Outlined.DarkMode) },
                notificationsEnabled = notificationsEnabled,
                enabled = settings.eveningReminderEnabled,
                detailLabel = formatReminderTime(context, settings.eveningReminderMinutes),
                ringtoneLabel = reminderRingtoneLabel(context, settings.eveningReminderRingtoneUri),
                onEnabledChange = { onReminderEnabledChange(ReminderKind.EVENING, it) },
                onPickDetail = {
                    pickReminderTime(settings.eveningReminderMinutes) {
                        onReminderMinutesChange(ReminderKind.EVENING, it)
                    }
                },
                onPickRingtone = {
                    pickReminderRingtone(ReminderKind.EVENING, settings.eveningReminderRingtoneUri)
                },
            )
        }

        item { Spacer(Modifier.height(12.dp)) }

        item { SettingsSectionLabel("Sleeping") }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsReminderGroup(
                title = "Sleeping reminders",
                description = "A calm reminder before sleep.",
                enabledIcon = { SettingsIcon(Icons.Outlined.Hotel) },
                notificationsEnabled = notificationsEnabled,
                enabled = settings.sleepingReminderEnabled,
                detailLabel = formatReminderTime(context, settings.sleepingReminderMinutes),
                ringtoneLabel = reminderRingtoneLabel(context, settings.sleepingReminderRingtoneUri),
                onEnabledChange = { onReminderEnabledChange(ReminderKind.SLEEPING, it) },
                onPickDetail = {
                    pickReminderTime(settings.sleepingReminderMinutes) {
                        onReminderMinutesChange(ReminderKind.SLEEPING, it)
                    }
                },
                onPickRingtone = {
                    pickReminderRingtone(ReminderKind.SLEEPING, settings.sleepingReminderRingtoneUri)
                },
            )
        }

        item { Spacer(Modifier.height(12.dp)) }

        item { SettingsSectionLabel("Repeatable reminders") }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsRepeatableReminderGroup(
                enabledIcon = { SettingsIcon(Icons.Outlined.Autorenew) },
                notificationsEnabled = notificationsEnabled,
                enabled = settings.repeatableReminderEnabled,
                patternLabel = repeatableReminderPatternLabel(settings.repeatableReminderPattern),
                ringtoneLabel = reminderRingtoneLabel(context, settings.repeatableReminderRingtoneUri),
                onEnabledChange = { onReminderEnabledChange(ReminderKind.REPEATABLE, it) },
                onPickPattern = ::openRepeatablePatternSheet,
                onPickRingtone = {
                    pickReminderRingtone(
                        ReminderKind.REPEATABLE,
                        settings.repeatableReminderRingtoneUri,
                    )
                },
            )
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun SettingsReminderPermissionNotice(
    onOpenSettings: () -> Unit,
) {
    SettingsStaticTile(shape = settingsGroupShape(0, 1)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsNone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Notifications are off in system settings",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Turn them back on for Hisn Al-Muslim to receive reminders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onOpenSettings) {
                Text("Open settings")
            }
        }
    }
}

@Composable
private fun SettingsReminderGroup(
    title: String,
    description: String,
    enabledIcon: @Composable () -> Unit,
    notificationsEnabled: Boolean,
    enabled: Boolean,
    detailLabel: String,
    ringtoneLabel: String,
    onEnabledChange: (Boolean) -> Unit,
    onPickDetail: () -> Unit,
    onPickRingtone: () -> Unit,
) {
    val controlsEnabled = notificationsEnabled && enabled
    SettingsReminderCard(
        title = title,
        description = description,
        enabledIcon = enabledIcon,
        notificationsEnabled = notificationsEnabled,
        enabled = enabled,
        onEnabledChange = onEnabledChange,
        primaryDetailTitle = "Time",
        primaryDetailLabel = detailLabel,
        onPickPrimaryDetail = onPickDetail,
        secondaryDetailLabel = ringtoneLabel,
        onPickSecondaryDetail = onPickRingtone,
        controlsEnabled = controlsEnabled,
    )
}

@Composable
private fun SettingsRepeatableReminderGroup(
    enabledIcon: @Composable () -> Unit,
    notificationsEnabled: Boolean,
    enabled: Boolean,
    patternLabel: String,
    ringtoneLabel: String,
    onEnabledChange: (Boolean) -> Unit,
    onPickPattern: () -> Unit,
    onPickRingtone: () -> Unit,
) {
    val controlsEnabled = notificationsEnabled && enabled
    SettingsReminderCard(
        title = "Repeatable reminders",
        description = "Gentle nudges throughout the day.",
        enabledIcon = enabledIcon,
        notificationsEnabled = notificationsEnabled,
        enabled = enabled,
        onEnabledChange = onEnabledChange,
        primaryDetailTitle = "Time pattern",
        primaryDetailLabel = patternLabel,
        onPickPrimaryDetail = onPickPattern,
        secondaryDetailLabel = ringtoneLabel,
        onPickSecondaryDetail = onPickRingtone,
        controlsEnabled = controlsEnabled,
    )
}

@Composable
private fun SettingsReminderCard(
    title: String,
    description: String,
    enabledIcon: @Composable () -> Unit,
    notificationsEnabled: Boolean,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    primaryDetailTitle: String,
    primaryDetailLabel: String,
    onPickPrimaryDetail: () -> Unit,
    secondaryDetailLabel: String,
    onPickSecondaryDetail: () -> Unit,
    controlsEnabled: Boolean,
) {
    SettingsStaticTile(shape = settingsGroupShape(0, 1)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.size(52.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        enabledIcon()
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = enabled,
                    enabled = notificationsEnabled,
                    onCheckedChange = onEnabledChange,
                    thumbContent = {
                        if (enabled) {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    },
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            )

            SettingsCardNavigationRow(
                icon = Icons.Outlined.Schedule,
                title = primaryDetailTitle,
                subtitle = primaryDetailLabel,
                enabled = controlsEnabled,
                onClick = onPickPrimaryDetail,
            )
            SettingsCardNavigationRow(
                icon = Icons.Outlined.MusicNote,
                title = "Ringtone",
                subtitle = secondaryDetailLabel,
                enabled = controlsEnabled,
                onClick = onPickSecondaryDetail,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsAppearancePage(
    contentPadding: PaddingValues,
    settings: AppSettings,
    onBack: () -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onPureBlackThemeChange: (Boolean) -> Unit,
    onThemeSeedColorChange: (Long) -> Unit,
) {
    SettingsPageScaffold(
        contentPadding = contentPadding,
        title = "Appearance",
        subtitle = "Settings",
        onBack = onBack,
    ) {
        item { Spacer(Modifier.height(14.dp)) }

        item {
            SettingsGroup {
                SettingsAppearanceModeTile(
                    shape = settingsGroupShape(0, 1),
                    selectedMode = settings.themeMode,
                    onModeSelected = onThemeModeChange,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsGroup {
                SettingsSwitchTile(
                    shape = settingsGroupShape(0, 2),
                    icon = { SettingsIcon(Icons.Outlined.Palette) },
                    title = "Dynamic color",
                    subtitle = "Match theme colors from your wallpaper.",
                    checked = settings.dynamicColorEnabled,
                    onCheckedChange = onDynamicColorChange,
                )
                SettingsSwitchTile(
                    shape = settingsGroupShape(1, 2),
                    icon = { SettingsIcon(Icons.Outlined.DarkMode) },
                    title = "Black theme",
                    subtitle = "Use pure black in dark theme.",
                    checked = settings.pureBlackThemeEnabled,
                    onCheckedChange = onPureBlackThemeChange,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsGroup {
                SettingsSliderTile(
                    shape = settingsGroupShape(0, 1),
                    icon = { SettingsIcon(Icons.Outlined.TextFields) },
                    title = "Interface size",
                    subtitle = "Adjust app text and UI size.",
                    value = settings.fontScale,
                    valueRange = 0.85f..1.35f,
                    valueLabel = percentageLabel(settings.fontScale),
                    onValueChange = onFontScaleChange,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsGroup {
                SettingsThemeColorTile(
                    shape = settingsGroupShape(0, 1),
                    selectedColor = settings.themeSeedColor,
                    onColorSelected = onThemeSeedColorChange,
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsReadingPage(
    contentPadding: PaddingValues,
    settings: AppSettings,
    onBack: () -> Unit,
    onCollectionTitleLanguageChange: (CollectionTitleLanguage) -> Unit,
    onArabicFontFamilyChange: (ArabicFontFamily) -> Unit,
    onArabicFontScaleChange: (Float) -> Unit,
    onTransliterationFontScaleChange: (Float) -> Unit,
    onTranslationFontScaleChange: (Float) -> Unit,
    onShowTransliterationChange: (Boolean) -> Unit,
    onShowTranslationChange: (Boolean) -> Unit,
    onShowReferenceChange: (Boolean) -> Unit,
) {
    var showArabicFontSheet by rememberSaveable { mutableStateOf(false) }

    fun openArabicFontSheet() {
        showArabicFontSheet = true
    }

    fun dismissArabicFontSheet() {
        showArabicFontSheet = false
    }

    if (showArabicFontSheet) {
        SettingsArabicFontBottomSheet(
            selectedFont = settings.arabicFontFamily,
            onFontSelected = onArabicFontFamilyChange,
            onDismiss = ::dismissArabicFontSheet,
        )
    }

    SettingsPageScaffold(
        contentPadding = contentPadding,
        title = "Reading",
        subtitle = "Settings",
        onBack = onBack,
    ) {
        item { Spacer(Modifier.height(14.dp)) }

        item {
            SettingsSectionLabel("Collections")
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsGroup {
                SettingsCollectionTitleLanguageTile(
                    shape = settingsGroupShape(0, 1),
                    selectedLanguage = settings.collectionTitleLanguage,
                    onLanguageSelected = onCollectionTitleLanguageChange,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsSectionLabel("Arabic")
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsGroup {
                SettingsNavigationTile(
                    shape = settingsGroupShape(0, 2),
                    icon = { SettingsIcon(Icons.AutoMirrored.Outlined.MenuBook) },
                    title = "Arabic font",
                    subtitle = settings.arabicFontFamily.label(),
                    onClick = ::openArabicFontSheet,
                )
                SettingsSliderTile(
                    shape = settingsGroupShape(1, 2),
                    icon = { SettingsIcon(Icons.Outlined.TextFields) },
                    title = "Arabic text size",
                    subtitle = "Adjust Arabic reading size.",
                    value = settings.arabicFontScale,
                    valueRange = 1.0f..1.6f,
                    valueLabel = multiplierLabel(settings.arabicFontScale),
                    onValueChange = onArabicFontScaleChange,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsSectionLabel("Transliteration")
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsGroup {
                SettingsSwitchTile(
                    shape = settingsGroupShape(0, 2),
                    icon = { SettingsIcon(Icons.Outlined.Translate) },
                    title = "Transliteration",
                    subtitle = "Show pronunciation guidance.",
                    checked = settings.showTransliteration,
                    onCheckedChange = onShowTransliterationChange,
                )
                SettingsSliderTile(
                    shape = settingsGroupShape(1, 2),
                    icon = { SettingsIcon(Icons.Outlined.Translate) },
                    title = "Transliteration size",
                    subtitle = "Adjust transliteration size.",
                    value = settings.transliterationFontScale,
                    valueRange = 0.9f..1.4f,
                    valueLabel = multiplierLabel(settings.transliterationFontScale),
                    enabled = settings.showTransliteration,
                    onValueChange = onTransliterationFontScaleChange,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsSectionLabel("Translation")
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsGroup {
                SettingsSwitchTile(
                    shape = settingsGroupShape(0, 2),
                    icon = { SettingsIcon(Icons.Outlined.Translate) },
                    title = "Translation",
                    subtitle = "Show translated meaning.",
                    checked = settings.showTranslation,
                    onCheckedChange = onShowTranslationChange,
                )
                SettingsSliderTile(
                    shape = settingsGroupShape(1, 2),
                    icon = { SettingsIcon(Icons.Outlined.Translate) },
                    title = "Translation size",
                    subtitle = "Adjust translation size.",
                    value = settings.translationFontScale,
                    valueRange = 0.9f..1.4f,
                    valueLabel = multiplierLabel(settings.translationFontScale),
                    enabled = settings.showTranslation,
                    onValueChange = onTranslationFontScaleChange,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsSectionLabel("Reference")
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsGroup {
                SettingsSwitchTile(
                    shape = settingsGroupShape(0, 1),
                    icon = { SettingsIcon(Icons.Outlined.Visibility) },
                    title = "Reference",
                    subtitle = "Show source and notes.",
                    checked = settings.showReference,
                    onCheckedChange = onShowReferenceChange,
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

private fun formatReminderTime(
    context: Context,
    minutes: Int,
): String {
    val hour = minutes / 60
    val minute = minutes % 60
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    return DateFormat.getTimeFormat(context).format(calendar.time)
}

private fun reminderRingtoneLabel(
    context: Context,
    ringtoneUri: String?,
): String {
    val uri = ringtoneUri?.let(Uri::parse) ?: Settings.System.DEFAULT_NOTIFICATION_URI
    val ringtone = runCatching { RingtoneManager.getRingtone(context, uri) }.getOrNull()
    return ringtone?.getTitle(context)?.takeIf { it.isNotBlank() } ?: "Default notification sound"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsArabicFontBottomSheet(
    selectedFont: ArabicFontFamily,
    onFontSelected: (ArabicFontFamily) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    val fontItemColors = ListItemDefaults.colors()
    val fontItemShapes = ListItemDefaults.shapes()

    fun selectFont(fontFamily: ArabicFontFamily) {
        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            onFontSelected(fontFamily)
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Choose Arabic font",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(MaterialTheme.shapes.large),
            ) {
                itemsIndexed(
                    ArabicFontFamily.entries,
                    key = { _, item -> item.name },
                ) { _, item ->
                    val selected = item == selectedFont

                    SegmentedListItem(
                        onClick = { selectFont(item) },
                        content = { Text(item.label()) },
                        trailingContent = {
                            if (selected) {
                                Icon(
                                    painter = painterResource(R.drawable.check),
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                        selected = selected,
                        shapes = fontItemShapes,
                        colors = fontItemColors,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsAboutPage(
    contentPadding: PaddingValues,
    versionLabel: String,
    versionCodeLabel: String,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val licenseText = remember(context) {
        context.resources.openRawResource(R.raw.gpl_v3).bufferedReader().use { it.readText() }
    }
    var showLicenseSheet by rememberSaveable { mutableStateOf(false) }
    fun openLicenseDialog() {
        showLicenseSheet = true
    }

    fun dismissLicenseDialog() {
        showLicenseSheet = false
    }

    val socialLinks = remember {
        listOf(
            AboutQuickLink(R.drawable.github, "GitHub", "https://github.com/itsyaasir"),
            AboutQuickLink(R.drawable.x, "X", "https://x.com/itsyaasir"),
            AboutQuickLink(R.drawable.globe, "Website", "https://shariff.dev"),
            AboutQuickLink(R.drawable.email, "Email", "mailto:contact@shariff.dev"),
        )
    }

    SettingsPageScaffold(
        contentPadding = contentPadding,
        title = "About",
        subtitle = "Hisnul Muslim",
        onBack = onBack,
    ) {
        item { Spacer(Modifier.height(14.dp)) }

        item {
            SettingsGroup {
                SettingsAboutAppCard(
                    shape = settingsGroupShape(0, 2),
                    versionLabel = versionLabel,
                    versionCodeLabel = versionCodeLabel,
                    onOpenRepository = { uriHandler.openUri("https://github.com/itsyaasir/hisn-al-muslim") },
                )
                SettingsAboutDeveloperCard(
                    shape = settingsGroupShape(1, 2),
                    socialLinks = socialLinks,
                    onOpenLink = uriHandler::openUri,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsGroup {
                SettingsExternalLinkTile(
                    shape = settingsGroupShape(0, 1),
                    icon = {
                        SettingsPainterIcon(
                            drawableRes = R.drawable.bmc,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    title = "Buy me a coffee",
                    subtitle = "Support the project with a small donation",
                    onClick = { uriHandler.openUri("https://example.com/buy-me-a-coffee") },
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SettingsGroup {
                SettingsNavigationTile(
                    shape = settingsGroupShape(0, 1),
                    icon = {
                        SettingsPainterIcon(
                            drawableRes = R.drawable.gavel,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    title = "License",
                    subtitle = "GNU General Public License Version 3",
                    onClick = ::openLicenseDialog,
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }

    if (showLicenseSheet) {
        SettingsLicenseBottomSheet(
            licenseText = licenseText,
            onDismiss = ::dismissLicenseDialog,
        )
    }
}

private data class SettingsAppLocale(
    val locale: Locale,
    val name: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsLicenseBottomSheet(
    licenseText: String,
    onDismiss: () -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
    ) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Hisn Al-Muslim",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Copyright (C) 2026 Yasir Shariff",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Licensed under the GNU General Public License v3.0 or later.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item {
                Text(
                    text = licenseText,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsRepeatablePatternBottomSheet(
    selectedPattern: RepeatableReminderPattern,
    onPatternSelected: (RepeatableReminderPattern) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()
    val itemColors = ListItemDefaults.colors()
    val itemShapes = ListItemDefaults.shapes()

    fun selectPattern(pattern: RepeatableReminderPattern) {
        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            onPatternSelected(pattern)
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Choose time pattern",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(MaterialTheme.shapes.large),
            ) {
                itemsIndexed(
                    RepeatableReminderPattern.entries,
                    key = { _, item -> item.name },
                ) { _, item ->
                    val selected = item == selectedPattern

                    SegmentedListItem(
                        onClick = { selectPattern(item) },
                        content = { Text(repeatableReminderPatternLabel(item)) },
                        trailingContent = {
                            if (selected) {
                                Icon(
                                    painter = painterResource(R.drawable.check),
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                        selected = selected,
                        shapes = itemShapes,
                        colors = itemColors,
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsLocaleBottomSheet(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()
    val localeManager = context.getSystemService(LocaleManager::class.java)
    val currentLocales = localeManager.applicationLocales
    val supportedLocales = remember(context) {
        LocaleConfig(context).supportedLocales ?: LocaleList()
    }
    val localeItemColors = ListItemDefaults.colors()
    val localeItemShapes = ListItemDefaults.shapes()

    val supportedLocalesList = remember(supportedLocales) {
        buildList {
            for (index in 0 until supportedLocales.size()) {
                val locale = supportedLocales[index]
                add(
                    SettingsAppLocale(
                        locale = locale,
                        name = locale.displayName.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                        },
                    ),
                )
            }
        }.sortedBy { it.name }
    }

    fun selectLocale(localeList: LocaleList) {
        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            localeManager.applicationLocales = localeList
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Choose language",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(MaterialTheme.shapes.large),
            ) {
                item {
                    SegmentedListItem(
                        onClick = { selectLocale(LocaleList()) },
                        content = { Text("System default") },
                        trailingContent = {
                            if (currentLocales.isEmpty) {
                                Icon(
                                    painter = painterResource(R.drawable.check),
                                    contentDescription = "Selected",
                                )
                            }
                        },
                        colors = localeItemColors,
                        selected = currentLocales.isEmpty,
                        shapes = localeItemShapes,
                    )
                }

                item { Spacer(Modifier.height(12.dp)) }

                itemsIndexed(
                    supportedLocalesList,
                    key = { _, item -> item.name },
                ) { _, item ->
                    val selected = !currentLocales.isEmpty && item.locale == currentLocales[0]

                    SegmentedListItem(
                        onClick = { selectLocale(LocaleList(item.locale)) },
                        content = { Text(item.name) },
                        trailingContent = {
                            if (selected) {
                                Icon(
                                    painter = painterResource(R.drawable.check),
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                        selected = selected,
                        shapes = localeItemShapes,
                        colors = localeItemColors,
                    )
                }
            }
        }
    }
}

private fun repeatableReminderPatternLabel(pattern: RepeatableReminderPattern): String {
    return when (pattern) {
        RepeatableReminderPattern.EVERY_HOUR -> "Every hour"
        RepeatableReminderPattern.EVERY_2_HOURS -> "Every 2 hours"
        RepeatableReminderPattern.EVERY_4_HOURS -> "Every 4 hours"
        RepeatableReminderPattern.EVERY_6_HOURS -> "Every 6 hours"
        RepeatableReminderPattern.EVERY_8_HOURS -> "Every 8 hours"
        RepeatableReminderPattern.EVERY_12_HOURS -> "Every 12 hours"
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsPageScaffold(
    contentPadding: PaddingValues,
    title: String,
    rootPage: Boolean = false,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val topBarContainer = if (rootPage) appTopBarContainerColor() else detailTopBarContainerColor()
    val layoutDirection = LocalLayoutDirection.current
    val topBarTitleFont = LocalAppFonts.current.topBarTitle

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(topBarContainer),
        contentAlignment = Alignment.TopCenter,
    ) {
        Scaffold(
            containerColor = topBarContainer,
            topBar = {
                if (rootPage) {
                    TopAppBar(
                        title = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 32.sp,
                                    lineHeight = 32.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = topBarTitleFont,
                                ),
                                modifier = Modifier
                                    .padding(top = contentPadding.calculateTopPadding())
                                    .padding(vertical = 14.dp),
                            )
                        },
                        subtitle = {},
                        titleHorizontalAlignment = Alignment.CenterHorizontally,
                        colors = appTopBarColors(),
                        scrollBehavior = scrollBehavior,
                        windowInsets = WindowInsets(),
                    )
                } else {
                    LargeTopAppBar(
                        title = {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = topBarTitleFont,
                                    ),
                                )
                                if (subtitle != null) {
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            if (onBack != null) {
                                FilledTonalIconButton(
                                    onClick = onBack,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = groupedTileContainerColor(),
                                    ),
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                        contentDescription = "Back",
                                    )
                                }
                            }
                        },
                        colors = detailTopBarColors(),
                        scrollBehavior = scrollBehavior,
                    )
                }
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { innerPadding ->
            val listInsets = mergePaddingValues(innerPadding, contentPadding, layoutDirection)
            val listContentPadding = mergePaddingValues(
                listInsets,
                PaddingValues(horizontal = 16.dp),
                layoutDirection,
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = PaneMaxWidth)
                        .background(topBarContainer),
                    contentPadding = listContentPadding,
                    content = content,
                )
            }
        }
    }
}

@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp), content = content)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsNavigationTile(
    shape: RoundedCornerShape,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    SettingsSegmentedTile(
        enabled = enabled,
        onClick = onClick,
        shape = shape,
        icon = icon,
        title = title,
        subtitle = subtitle,
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsExternalLinkTile(
    shape: RoundedCornerShape,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    SettingsSegmentedTile(
        enabled = enabled,
        onClick = onClick,
        shape = shape,
        icon = icon,
        title = title,
        subtitle = subtitle,
        trailingContent = {
            Icon(
                painter = painterResource(R.drawable.open_in_browser),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsSwitchTile(
    shape: RoundedCornerShape,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingsSegmentedTile(
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        shape = shape,
        icon = icon,
        title = title,
        subtitle = subtitle,
        trailingContent = {
            Switch(
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange,
                thumbContent = {
                    if (checked) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                },
            )
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsSegmentedTile(
    enabled: Boolean = true,
    onClick: () -> Unit,
    shape: RoundedCornerShape,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
    trailingContent: @Composable () -> Unit,
) {
    SegmentedListItem(
        enabled = enabled,
        onClick = onClick,
        leadingContent = { icon() },
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        },
        supportingContent = subtitle?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        trailingContent = trailingContent,
        colors = settingsListItemColors(),
        shapes = settingsSegmentedListItemShapes(shape),
    )
}

@Composable
private fun SettingsCardNavigationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .alpha(if (enabled) 1f else 0.56f),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.size(42.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun SettingsSliderTile(
    shape: RoundedCornerShape,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueLabel: String,
    enabled: Boolean = true,
    onValueChange: (Float) -> Unit,
) {
    SettingsStaticTile(shape = shape) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (enabled) 1f else 0.56f),
        ) {
            Row(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon()
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = valueLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.width(40.dp))
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    valueRange = valueRange,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SettingsAppearanceModeTile(
    shape: RoundedCornerShape,
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
) {
    SettingsStaticTile(shape = shape) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsIcon(Icons.Outlined.Palette)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "Choose theme.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            ConnectedChoiceRow(
                options = ThemeMode.entries,
                selected = selectedMode,
                labelFor = { it.label() },
                onSelected = onModeSelected,
            )
        }
    }
}

@Composable
private fun SettingsCollectionTitleLanguageTile(
    shape: RoundedCornerShape,
    selectedLanguage: CollectionTitleLanguage,
    onLanguageSelected: (CollectionTitleLanguage) -> Unit,
) {
    SettingsStaticTile(shape = shape) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsIcon(Icons.AutoMirrored.Outlined.MenuBook)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Collection titles",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "Choose whether collection names appear in English or Arabic.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            ConnectedChoiceRow(
                options = CollectionTitleLanguage.entries,
                selected = selectedLanguage,
                labelFor = { it.label() },
                onSelected = onLanguageSelected,
            )
        }
    }
}

private val ThemeSeedOptions = listOf(
    0xFFFFFFFF,
    0xFFFEB4A7,
    0xFFFFB3C0,
    0xFFFCAAFF,
    0xFFB9C3FF,
    0xFF62D3FF,
    0xFF44D9F1,
    0xFF52DBC9,
    0xFF78DD77,
    0xFF9FD75C,
    0xFFC1D02D,
    0xFFFABD00,
    0xFFFFB86E,
)

@Composable
private fun SettingsThemeColorTile(
    shape: RoundedCornerShape,
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
) {
    SettingsStaticTile(shape = shape) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsIcon(Icons.Outlined.Palette)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Color scheme",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "Choose color scheme.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ThemeSeedOptions.forEach { colorValue ->
                    item {
                        ThemeSeedSwatch(
                            colorValue = colorValue,
                            selected = colorValue == selectedColor,
                            onClick = { onColorSelected(colorValue) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeSeedSwatch(
    colorValue: Long,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val swatchColor = if (colorValue == DefaultThemeSeedColor) {
        groupedTileContainerColor()
    } else {
        Color(colorValue)
    }
    val indicatorColor = if (colorValue == DefaultThemeSeedColor) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Black.copy(alpha = 0.72f)
    }

    Surface(
        color = swatchColor,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                shape = RoundedCornerShape(18.dp),
            )
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(indicatorColor, RoundedCornerShape(999.dp)),
                )
            } else if (colorValue == DefaultThemeSeedColor) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(999.dp),
                        ),
                )
            }
        }
    }
}

@Composable
private fun SettingsAboutAppCard(
    shape: RoundedCornerShape,
    versionLabel: String,
    versionCodeLabel: String,
    onOpenRepository: () -> Unit,
) {
    SettingsStaticTile(shape = shape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(22.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Hisn Al-Muslim",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "$versionLabel ($versionCodeLabel)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                FilledTonalIconButton(
                    onClick = onOpenRepository,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.github),
                        contentDescription = "GitHub",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsAboutDeveloperCard(
    shape: RoundedCornerShape,
    socialLinks: List<AboutQuickLink>,
    onOpenLink: (String) -> Unit,
) {
    SettingsStaticTile(shape = shape) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.pfp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Yasir Shariff",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = " Software Engineer",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
                Spacer(Modifier.weight(1f))
            }
            Row {
                Spacer(Modifier.width((64 + 16).dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    socialLinks.forEach { link ->
                        FilledTonalIconButton(
                            onClick = { onOpenLink(link.url) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier.width(52.dp),
                        ) {
                            Icon(
                                painter = painterResource(link.drawableRes),
                                contentDescription = link.label,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class AboutQuickLink(
    val drawableRes: Int,
    val label: String,
    val url: String,
)

@Composable
private fun SettingsPainterIcon(
    drawableRes: Int,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(18.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(drawableRes),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun <T> ConnectedChoiceRow(
    options: List<T>,
    selected: T,
    labelFor: (T) -> String,
    onSelected: (T) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = RoundedCornerShape(22.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = option == selected
                val optionShape = connectedChoiceShape(index, options.size)
                Surface(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Transparent
                    },
                    shape = optionShape,
                    modifier = Modifier
                        .weight(1f)
                        .clip(optionShape)
                        .clickable { onSelected(option) },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = labelFor(option),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun settingsListItemColors() = ListItemDefaults.colors(
    containerColor = groupedTileContainerColor(),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun settingsSegmentedListItemShapes(
    shape: RoundedCornerShape,
) = ListItemDefaults.shapes(
    shape = shape,
    selectedShape = shape,
    pressedShape = shape,
    focusedShape = shape,
    hoveredShape = shape,
    draggedShape = shape,
)

@Composable
private fun SettingsStaticTile(
    shape: RoundedCornerShape,
    content: @Composable () -> Unit,
) {
    Surface(
        color = groupedTileContainerColor(),
        shape = shape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        content()
    }
}

@Composable
private fun SettingsIcon(imageVector: androidx.compose.ui.graphics.vector.ImageVector) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(24.dp),
    )
}

private fun settingsGroupShape(index: Int, count: Int): RoundedCornerShape {
    return when {
        count == 1 -> RoundedCornerShape(TopGroupRadius)
        index == 0 -> RoundedCornerShape(
            topStart = TopGroupRadius,
            topEnd = TopGroupRadius,
            bottomStart = InnerGroupRadius,
            bottomEnd = InnerGroupRadius,
        )

        index == count - 1 -> RoundedCornerShape(
            topStart = InnerGroupRadius,
            topEnd = InnerGroupRadius,
            bottomStart = BottomGroupRadius,
            bottomEnd = BottomGroupRadius,
        )

        else -> RoundedCornerShape(InnerGroupRadius)
    }
}

private fun connectedChoiceShape(index: Int, count: Int): RoundedCornerShape {
    return when {
        count == 1 -> RoundedCornerShape(18.dp)
        index == 0 -> RoundedCornerShape(
            topStart = 18.dp,
            bottomStart = 18.dp,
            topEnd = 10.dp,
            bottomEnd = 10.dp,
        )

        index == count - 1 -> RoundedCornerShape(
            topStart = 10.dp,
            bottomStart = 10.dp,
            topEnd = 18.dp,
            bottomEnd = 18.dp,
        )

        else -> RoundedCornerShape(10.dp)
    }
}

private fun areSystemNotificationsEnabled(context: Context): Boolean {
    val permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    return permissionGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
}

private fun percentageLabel(value: Float): String {
    return "${(value * 100).toInt()}%"
}

private fun multiplierLabel(value: Float): String {
    return String.format(Locale.US, "%.2fx", value)
}

private fun ThemeMode.label(): String {
    return name.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

private fun ArabicFontFamily.label(): String {
    return when (this) {
        ArabicFontFamily.AMIRI -> "Amiri"
        ArabicFontFamily.NOTO_NASKH -> "Noto Naskh"
        ArabicFontFamily.SCHEHERAZADE -> "Scheherazade"
    }
}

private fun CollectionTitleLanguage.label(): String {
    return when (this) {
        CollectionTitleLanguage.ENGLISH -> "English"
        CollectionTitleLanguage.ARABIC -> "Arabic"
    }
}
