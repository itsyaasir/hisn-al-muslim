package com.example.hisnulmuslim.feature.adhkardetail

import android.content.ClipData
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hisnulmuslim.core.designsystem.EmptyStateCard
import com.example.hisnulmuslim.core.designsystem.LocalAppFonts
import com.example.hisnulmuslim.core.designsystem.LocalExpressiveShapes
import com.example.hisnulmuslim.core.designsystem.LocalMotionPreferences
import com.example.hisnulmuslim.core.designsystem.mergePaddingValues
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrDetailScreen(
    dhikrId: Long,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onOpenSibling: (Long) -> Unit,
    viewModel: DhikrDetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(dhikrId) {
        viewModel.bind(dhikrId = dhikrId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val topBarTitleFont = LocalAppFonts.current.topBarTitle
    val layoutDirection = LocalLayoutDirection.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val expressiveShapes = LocalExpressiveShapes.current
    val motionPreferences = LocalMotionPreferences.current
    val handleBack = {
        viewModel.resetCounter()
        onBack()
    }
    val handleOpenSibling: (Long) -> Unit = { siblingDhikrId ->
        viewModel.resetCounter()
        onOpenSibling(siblingDhikrId)
    }

    BackHandler(onBack = handleBack)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.dhikr?.title.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = topBarTitleFont,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = handleBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val screenInsets = mergePaddingValues(innerPadding, contentPadding, layoutDirection)
        val contentInsets = mergePaddingValues(
            screenInsets,
            PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 124.dp),
            layoutDirection,
        )
        val dhikr = uiState.dhikr
        if (dhikr == null) {
            EmptyStateCard(
                title = "Dhikr not found",
                subtitle = "The selected item may no longer be available in the current dataset.",
                modifier = Modifier
                    .padding(screenInsets)
                    .padding(20.dp),
            )
            return@Scaffold
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 900.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(contentInsets),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(motionPreferences.expressiveSpatial()),
                    shape = expressiveShapes.heroCard,
                    colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Rtl) {
                            SelectionContainer {
                                Text(
                                    text = dhikr.arabicText,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = (28.sp * uiState.settings.arabicFontScale),
                                        lineHeight = (48.sp * uiState.settings.arabicFontScale),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                )
                            }
                        }
                        AnimatedVisibility(visible = uiState.settings.showTransliteration && !dhikr.transliteration.isNullOrBlank()) {
                            Text(
                                text = dhikr.transliteration.orEmpty(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize * uiState.settings.transliterationFontScale,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * uiState.settings.transliterationFontScale,
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                            )
                        }
                        AnimatedVisibility(visible = uiState.settings.showTranslation && !dhikr.translation.isNullOrBlank()) {
                            Text(
                                text = dhikr.translation.orEmpty(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize * uiState.settings.translationFontScale,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * uiState.settings.translationFontScale,
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.92f),
                            )
                        }
                    }
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = expressiveShapes.actionCard,
                    colors = androidx.compose.material3.CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                ) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        FilledTonalButton(
                            onClick = viewModel::toggleFavorite,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (uiState.isFavorite) "Saved" else "Save")
                        }
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "Dhikr",
                                                formatShareText(dhikr),
                                            ),
                                        ),
                                    )
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy")
                        }
                        OutlinedButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, formatShareText(dhikr))
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share dhikr"))
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share")
                        }
                    }
                }

                if (uiState.settings.showReference && (!dhikr.notes.isNullOrBlank() || !dhikr.sourceReference.isNullOrBlank())) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = expressiveShapes.actionCard,
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = "Reference",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            dhikr.sourceReference?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                            dhikr.notes?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

            }

            DetailFloatingActions(
                previousDhikrId = uiState.previousDhikrId,
                nextDhikrId = uiState.nextDhikrId,
                showsCounterHero = uiState.showsCounterHero,
                uiState = uiState,
                screenInsets = screenInsets,
                onCount = {
                    if (uiState.isCounterRoundComplete) {
                        haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                        viewModel.resetCounter()
                    } else {
                        val hapticType = if (uiState.remainingCount == 1) {
                            HapticFeedbackType.ToggleOn
                        } else {
                            HapticFeedbackType.VirtualKey
                        }
                        haptic.performHapticFeedback(hapticType)
                        viewModel.incrementCounter()
                    }
                },
                onOpenSibling = handleOpenSibling,
            )
        }
    }
}

@Composable
private fun BoxScope.DetailFloatingActions(
    previousDhikrId: Long?,
    nextDhikrId: Long?,
    showsCounterHero: Boolean,
    uiState: DhikrDetailUiState,
    screenInsets: PaddingValues,
    onCount: () -> Unit,
    onOpenSibling: (Long) -> Unit,
) {
    if (previousDhikrId == null && nextDhikrId == null && !showsCounterHero) return

    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .widthIn(max = 900.dp)
            .padding(screenInsets)
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingSiblingNavGroup(
            previousDhikrId = previousDhikrId,
            nextDhikrId = nextDhikrId,
            onOpenSibling = onOpenSibling,
        )

        if (showsCounterHero) {
            DhikrCounterFab(
                uiState = uiState,
                onClick = onCount,
            )
        }
    }
}

@Composable
private fun FloatingSiblingNavGroup(
    previousDhikrId: Long?,
    nextDhikrId: Long?,
    onOpenSibling: (Long) -> Unit,
) {
    if (previousDhikrId == null && nextDhikrId == null) return

    Surface(
        modifier = Modifier.animateContentSize(LocalMotionPreferences.current.expressiveSpatial()),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.92f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            previousDhikrId?.let { dhikrId ->
                TextButton(
                    onClick = { onOpenSibling(dhikrId) },
                    shape = CircleShape,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Previous")
                }
            }

            if (previousDhikrId != null && nextDhikrId != null) {
                VerticalDivider(
                    modifier = Modifier.height(28.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                )
            }

            nextDhikrId?.let { dhikrId ->
                TextButton(
                    onClick = { onOpenSibling(dhikrId) },
                    shape = CircleShape,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun DhikrCounterFab(
    uiState: DhikrDetailUiState,
    onClick: () -> Unit,
) {
    val motionPreferences = LocalMotionPreferences.current
    val isComplete = uiState.isCounterRoundComplete
    val remaining = uiState.remainingCount ?: return
    val containerColor by animateColorAsState(
        targetValue = if (isComplete) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        animationSpec = motionPreferences.expressiveFastEffect(),
        label = "counterFabContainerColor",
    )
    val contentColor by animateColorAsState(
        targetValue = if (isComplete) {
            MaterialTheme.colorScheme.onTertiaryContainer
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer
        },
        animationSpec = motionPreferences.expressiveFastEffect(),
        label = "counterFabContentColor",
    )
    val scale by animateFloatAsState(
        targetValue = if (isComplete) 1.06f else 1f,
        animationSpec = motionPreferences.expressiveSpatial(),
        label = "counterFabScale",
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(72.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .animateContentSize(motionPreferences.expressiveSpatial()),
        shape = RoundedCornerShape(28.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
    ) {
        AnimatedContent(
            targetState = isComplete,
            label = "counterFabContent",
        ) { complete ->
            if (complete) {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                )
            } else {
                Text(
                    text = remaining.toString(),
                    style = MaterialTheme.typography.displaySmall,
                )
            }
        }
    }
}

private fun formatShareText(dhikr: com.example.hisnulmuslim.core.model.Dhikr): String {
    return buildString {
        appendLine(dhikr.title)
        appendLine()
        appendLine(dhikr.arabicText)
        dhikr.transliteration?.let {
            appendLine()
            appendLine(it)
        }
        dhikr.translation?.let {
            appendLine()
            appendLine(it)
        }
        dhikr.sourceReference?.let {
            appendLine()
            appendLine("Reference: $it")
        }
    }.trim()
}
