package com.example.hisnulmuslim.feature.adhkardetail

import android.content.ClipData
import android.content.Intent
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import kotlin.math.absoluteValue

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

    val activity = LocalActivity.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val appFonts = LocalAppFonts.current
    val topBarTitleFont = appFonts.topBarTitle
    val arabicFont = appFonts.arabic
    val layoutDirection = LocalLayoutDirection.current
    val expressiveShapes = LocalExpressiveShapes.current
    val motionPreferences = LocalMotionPreferences.current
    val pagerState = rememberPagerState(pageCount = { uiState.collectionDhikr.size })
    val pageScrollStates = remember(uiState.collectionDhikr.size) { mutableStateMapOf<Int, ScrollState>() }
    val headerTitle = uiState.dhikr?.collectionTitle.orEmpty()
    val currentPageScroll = pageScrollStates[pagerState.currentPage]?.value ?: 0
    val collapseProgress by animateFloatAsState(
        targetValue = (currentPageScroll / 144f).coerceIn(0f, 1f),
        animationSpec = motionPreferences.expressiveFastEffect(),
        label = "detailHeaderCollapseProgress",
    )

    DisposableEffect(viewModel, collectionId, activity) {
        onDispose {
            if (activity?.isChangingConfigurations == true) return@onDispose
            viewModel.clearCollectionProgress(collectionId)
        }
    }

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

    val screenInsets = contentPadding
    val pageInsets = mergePaddingValues(
        PaddingValues(start = 20.dp, top = 28.dp, end = 20.dp, bottom = 124.dp),
        PaddingValues(bottom = contentPadding.calculateBottomPadding()),
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
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) {
                DetailScrollableHeader(
                    title = headerTitle,
                    fontFamily = topBarTitleFont,
                    onBack = onBack,
                    isFavorite = uiState.isFavorite,
                    onToggleFavorite = { viewModel.toggleFavorite() },
                    onCopy = {
                        val currentDhikr = uiState.dhikr ?: return@DetailScrollableHeader
                        coroutineScope.launch {
                            clipboard.setClipEntry(
                                ClipEntry(
                                    ClipData.newPlainText(
                                        "Dhikr",
                                        formatShareText(currentDhikr),
                                    ),
                                ),
                            )
                        }
                    },
                    onShare = {
                        val currentDhikr = uiState.dhikr ?: return@DetailScrollableHeader
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, formatShareText(currentDhikr))
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share dhikr"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 900.dp)
                        .graphicsLayer {
                            alpha = lerpFloat(1f, 0.94f, collapseProgress)
                            scaleX = lerpFloat(1f, 0.985f, collapseProgress)
                            scaleY = lerpFloat(1f, 0.985f, collapseProgress)
                            translationY = lerpFloat(0f, (-8).dp.toPx(), collapseProgress)
                        }
                        .padding(
                            start = 20.dp,
                            top = contentPadding.calculateTopPadding() + 16.dp,
                            end = 20.dp,
                        ),
                )
            }

            if (uiState.showsCollectionDots) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = lerpFloat(1f, 0.72f, collapseProgress)
                            translationY = lerpFloat(0f, (-10).dp.toPx(), collapseProgress)
                        }
                        .padding(top = 8.dp, bottom = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CollectionProgressIndicator(
                        count = uiState.collectionDhikr.size,
                        selectedIndex = uiState.currentIndex,
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapAnimationSpec = motionPreferences.expressiveSpatial(),
                    snapPositionalThreshold = 0.42f,
                ),
                userScrollEnabled = uiState.collectionDhikr.size > 1,
                beyondViewportPageCount = 0,
            ) { page ->
                val pageDhikr = uiState.collectionDhikr[page]
                val pageScrollState = pageScrollStates.getOrPut(page) { ScrollState(0) }
                val rawOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                val pageOffset = rawOffset.absoluteValue.coerceIn(0f, 1f)
                val pageScale = lerpFloat(start = 0.972f, stop = 1f, fraction = 1f - pageOffset)
                val pageAlpha = lerpFloat(start = 0.94f, stop = 1f, fraction = 1f - pageOffset)
                val pageTranslation = lerpFloat(
                    start = 40f,
                    stop = 0f,
                    fraction = 1f - pageOffset,
                ) * rawOffset

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = pageAlpha
                            scaleX = pageScale
                            scaleY = pageScale
                            translationX = pageTranslation
                        },
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 900.dp)
                            .verticalScroll(pageScrollState)
                            .padding(pageInsets),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(motionPreferences.expressiveSpatial()),
                            verticalArrangement = Arrangement.spacedBy(28.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 760.dp)
                                    .align(Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                CompositionLocalProvider(
                                    LocalLayoutDirection provides LayoutDirection.Rtl,
                                ) {
                                    SelectionContainer {
                                        Text(
                                            text = pageDhikr.arabicText,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            style = TextStyle(
                                                fontFamily = arabicFont,
                                                fontSize = (30.sp * uiState.settings.arabicFontScale),
                                                lineHeight = (54.sp * uiState.settings.arabicFontScale),
                                                color = MaterialTheme.colorScheme.onSurface,
                                            ),
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = uiState.settings.showTransliteration && !pageDhikr.transliteration.isNullOrBlank(),
                            ) {
                                DetailSupportingTextSection(
                                    label = "Transliteration",
                                    labelColor = MaterialTheme.colorScheme.secondary,
                                    text = pageDhikr.transliteration.orEmpty(),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontStyle = FontStyle.Italic,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * uiState.settings.transliterationFontScale,
                                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * uiState.settings.transliterationFontScale,
                                    ),
                                    textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.96f),
                                )
                            }
                            AnimatedVisibility(
                                visible = uiState.settings.showTranslation && !pageDhikr.translation.isNullOrBlank(),
                            ) {
                                DetailSupportingTextSection(
                                    label = "Translation",
                                    labelColor = MaterialTheme.colorScheme.secondary,
                                    text = pageDhikr.translation.orEmpty(),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize * uiState.settings.translationFontScale,
                                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * uiState.settings.translationFontScale,
                                    ),
                                    textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                )
                            }
                        }

                        if (uiState.settings.showReference && (!pageDhikr.notes.isNullOrBlank() || !pageDhikr.sourceReference.isNullOrBlank())) {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = expressiveShapes.actionCard,
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                ),
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
        }

        DetailFloatingActions(
            showsCounterHero = uiState.showsCounterHero,
            remainingCount = uiState.remainingCount,
            isCounterRoundComplete = uiState.isCounterRoundComplete,
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

@Composable
private fun DetailScrollableHeader(
    title: String,
    fontFamily: FontFamily,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var actionsExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
            )
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = fontFamily,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (isFavorite) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = if (isFavorite) "Saved" else "Save",
            )
        }
        Box {
            IconButton(onClick = { actionsExpanded = true }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "More actions",
                )
            }
            DropdownMenu(
                expanded = actionsExpanded,
                onDismissRequest = { actionsExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Copy") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        actionsExpanded = false
                        onCopy()
                    },
                )
                DropdownMenuItem(
                    text = { Text("Share") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        actionsExpanded = false
                        onShare()
                    },
                )
            }
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
private fun CollectionProgressIndicator(
    count: Int,
    selectedIndex: Int,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "${selectedIndex + 1} of $count",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        CollectionDotsIndicator(
            count = count,
            selectedIndex = selectedIndex,
        )
    }
}

@Composable
private fun BoxScope.DetailFloatingActions(
    showsCounterHero: Boolean,
    remainingCount: Int?,
    isCounterRoundComplete: Boolean,
    screenInsets: PaddingValues,
    onCount: () -> Unit,
) {
    if (!showsCounterHero) return

    DhikrCounterFab(
        remaining = remainingCount ?: return,
        isComplete = isCounterRoundComplete,
        onClick = onCount,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(screenInsets)
            .padding(end = 20.dp, bottom = 24.dp),
    )
}

@Composable
private fun DhikrCounterFab(
    remaining: Int,
    isComplete: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionPreferences = LocalMotionPreferences.current
    val containerColor by animateColorAsState(
        targetValue = if (isComplete) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
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
    val borderColor by animateColorAsState(
        targetValue = if (isComplete) {
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
        },
        animationSpec = motionPreferences.expressiveFastEffect(),
        label = "counterFabBorderColor",
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
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(28.dp),
            )
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

@Composable
private fun ColumnScope.DetailSupportingTextSection(
    label: String,
    labelColor: androidx.compose.ui.graphics.Color,
    text: String,
    textStyle: TextStyle,
    textColor: androidx.compose.ui.graphics.Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 700.dp)
            .align(Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor,
        )
        Text(
            text = text,
            style = textStyle,
            color = textColor,
        )
    }
}

private fun lerpFloat(start: Float, stop: Float, fraction: Float): Float {
    return start + ((stop - start) * fraction)
}
