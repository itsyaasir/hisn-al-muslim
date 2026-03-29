package com.example.hisnulmuslim.feature.adhkardetail

import android.content.ClipData
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hisnulmuslim.core.designsystem.EmptyStateCard
import com.example.hisnulmuslim.core.designsystem.LocalAppFonts
import com.example.hisnulmuslim.core.designsystem.LocalExpressiveShapes
import com.example.hisnulmuslim.core.designsystem.LocalMotionPreferences
import com.example.hisnulmuslim.core.designsystem.mergePaddingValues
import com.example.hisnulmuslim.core.model.Dhikr
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DhikrDetailScreen(
    dhikrId: Long,
    collectionId: Long,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    viewModel: DhikrDetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(dhikrId, collectionId) {
        viewModel.bind(
            dhikrId = dhikrId,
            collectionId = collectionId,
        )
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
    val pagerState = rememberPagerState(pageCount = { uiState.collectionDhikr.size })

    LaunchedEffect(uiState.currentIndex, uiState.collectionDhikr.size) {
        if (uiState.collectionDhikr.isEmpty()) return@LaunchedEffect
        if (pagerState.currentPage != uiState.currentIndex) {
            pagerState.scrollToPage(uiState.currentIndex)
        }
    }

    LaunchedEffect(pagerState, uiState.collectionDhikr) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                uiState.collectionDhikr.getOrNull(page)?.let { dhikrItem ->
                    viewModel.selectDhikr(dhikrItem.id)
                }
            }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
                title = {
                    Text(
                        text = uiState.dhikr?.title.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = topBarTitleFont,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets(),
            )
        },
    ) { innerPadding ->
        val screenInsets = mergePaddingValues(innerPadding, contentPadding, layoutDirection)
        val pageInsets = mergePaddingValues(
            screenInsets,
            PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 124.dp),
            layoutDirection,
        )

        if (uiState.collectionDhikr.isEmpty()) {
            EmptyStateCard(
                title = "Dhikr not found",
                subtitle = "The selected item may no longer be available in the current collection.",
                modifier = Modifier
                    .padding(screenInsets)
                    .padding(20.dp),
            )
            return@Scaffold
        }

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = uiState.collectionDhikr.size > 1,
                beyondViewportPageCount = 0,
            ) { page ->
                val pageDhikr = uiState.collectionDhikr[page]
                val isActivePage = pageDhikr.id == uiState.currentDhikrId

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 900.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(pageInsets),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        if (uiState.showsCollectionDots) {
                            CollectionDotsIndicator(
                                count = uiState.collectionDhikr.size,
                                selectedIndex = uiState.currentIndex,
                            )
                        }

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(motionPreferences.expressiveSpatial()),
                            shape = expressiveShapes.heroCard,
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                androidx.compose.runtime.CompositionLocalProvider(
                                    androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Rtl,
                                ) {
                                    SelectionContainer {
                                        Text(
                                            text = pageDhikr.arabicText,
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
                                AnimatedVisibility(
                                    visible = uiState.settings.showTransliteration && !pageDhikr.transliteration.isNullOrBlank(),
                                ) {
                                    Text(
                                        text = pageDhikr.transliteration.orEmpty(),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontSize = MaterialTheme.typography.bodyLarge.fontSize * uiState.settings.transliterationFontScale,
                                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * uiState.settings.transliterationFontScale,
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                                    )
                                }
                                AnimatedVisibility(
                                    visible = uiState.settings.showTranslation && !pageDhikr.translation.isNullOrBlank(),
                                ) {
                                    Text(
                                        text = pageDhikr.translation.orEmpty(),
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
                            colors = CardDefaults.elevatedCardColors(
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
                                    onClick = {
                                        if (isActivePage) {
                                            viewModel.toggleFavorite()
                                        } else {
                                            viewModel.selectDhikr(pageDhikr.id)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(
                                        imageVector = if (isActivePage && uiState.isFavorite) {
                                            Icons.Outlined.Bookmark
                                        } else {
                                            Icons.Outlined.BookmarkBorder
                                        },
                                        contentDescription = null,
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isActivePage && uiState.isFavorite) "Saved" else "Save")
                                }
                                OutlinedButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            clipboard.setClipEntry(
                                                ClipEntry(
                                                    ClipData.newPlainText(
                                                        "Dhikr",
                                                        formatShareText(pageDhikr),
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
                                            putExtra(Intent.EXTRA_TEXT, formatShareText(pageDhikr))
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

                        if (uiState.settings.showReference && (!pageDhikr.notes.isNullOrBlank() || !pageDhikr.sourceReference.isNullOrBlank())) {
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
                                    pageDhikr.sourceReference?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
                                    pageDhikr.notes?.let {
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
                }
            }

            DetailFloatingActions(
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
            )
        }
    }
}

@Composable
private fun CollectionDotsIndicator(
    count: Int,
    selectedIndex: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier.padding(horizontal = 4.dp),
                shape = CircleShape,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
            ) {
                Spacer(
                    modifier = Modifier.size(
                        width = if (selected) 18.dp else 8.dp,
                        height = 8.dp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun BoxScope.DetailFloatingActions(
    showsCounterHero: Boolean,
    uiState: DhikrDetailUiState,
    screenInsets: PaddingValues,
    onCount: () -> Unit,
) {
    if (!showsCounterHero) return

    DhikrCounterFab(
        uiState = uiState,
        onClick = onCount,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(screenInsets)
            .padding(end = 20.dp, bottom = 24.dp),
    )
}

@Composable
private fun DhikrCounterFab(
    uiState: DhikrDetailUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
        modifier = modifier
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

private fun formatShareText(dhikr: Dhikr): String {
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
