package com.example.hisnulmuslim.feature.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hisnulmuslim.core.designsystem.EmptyStateCard
import com.example.hisnulmuslim.core.designsystem.HisnulMuslimTheme
import com.example.hisnulmuslim.core.designsystem.LocalAppFonts
import com.example.hisnulmuslim.core.designsystem.appTopBarContainerColor
import com.example.hisnulmuslim.core.designsystem.groupedTileContainerColor
import com.example.hisnulmuslim.core.designsystem.mergePaddingValues
import com.example.hisnulmuslim.core.model.Collection
import com.example.hisnulmuslim.core.model.Dhikr

private val HomeTopRadius = 28.dp
private val HomeInnerRadius = 8.dp
private val HomeBottomRadius = 28.dp
private val HomePaneMaxWidth = 600.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onOpenSearch: () -> Unit,
    onOpenCollection: (Collection) -> Unit,
    onOpenDhikr: (Dhikr) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        contentPadding = contentPadding,
        onOpenSearch = onOpenSearch,
        onOpenCollection = onOpenCollection,
        onOpenDhikr = onOpenDhikr,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    contentPadding: PaddingValues,
    onOpenSearch: () -> Unit,
    onOpenCollection: (Collection) -> Unit,
    onOpenDhikr: (Dhikr) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val layoutDirection = LocalLayoutDirection.current
    val topBarTitleFont = LocalAppFonts.current.topBarTitle

    val collapseTarget by remember(listState) {
        derivedStateOf {
            when {
                listState.firstVisibleItemIndex > 0 -> 1f
                else -> (listState.firstVisibleItemScrollOffset / 160f).coerceIn(0f, 1f)
            }
        }
    }
    val collapseProgress by animateFloatAsState(targetValue = collapseTarget, label = "dailyReflectionCollapse")

    val topBarContainer = appTopBarContainerColor()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(topBarContainer),
        contentAlignment = Alignment.TopCenter,
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = topBarContainer,
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
                    title = {
                        Text(
                            text = "Hisnul Muslim",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 32.sp,
                                lineHeight = 32.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = topBarTitleFont,
                            ),
                            modifier = Modifier
                                .padding(vertical = 14.dp),
                        )
                    },
                    subtitle = {},
                    actions = {
                        FilledTonalIconButton(onClick = onOpenSearch) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Open search",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = topBarContainer,
                        scrolledContainerColor = topBarContainer,
                    ),
                    titleHorizontalAlignment = Alignment.Start,
                    scrollBehavior = scrollBehavior,
                    windowInsets = WindowInsets(),
                )
            },
        ) { innerPadding ->
            val listInsets = mergePaddingValues(innerPadding, contentPadding, layoutDirection)
            val listContentPadding = mergePaddingValues(
                listInsets,
                PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                layoutDirection,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = HomePaneMaxWidth),
                    contentPadding = listContentPadding,
                ) {
                    uiState.dailyHighlight?.let { dhikr ->
                        item {
                            DailyReflectionTile(
                                dhikr = dhikr,
                                collapseProgress = collapseProgress,
                                onClick = { onOpenDhikr(dhikr) },
                            )
                        }
                        item { Spacer(Modifier.height(12.dp)) }
                    }

                    if (!uiState.isLoaded) {
                        item { Spacer(Modifier.height(1.dp)) }
                    } else if (uiState.collections.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = "No collections yet",
                                subtitle = "The local remembrance collection is still loading or needs to be seeded again.",
                            )
                        }
                    } else {
                        item {
                            HomeCollectionGroup(
                                items = uiState.collections,
                                onOpenCollection = onOpenCollection,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyReflectionTile(
    dhikr: Dhikr,
    collapseProgress: Float,
    onClick: () -> Unit,
) {
    val compactPadding = 14.dp
    val expandedPadding = 20.dp
    val horizontalPadding = expandedPadding - ((expandedPadding - compactPadding) * collapseProgress)
    val verticalPadding = expandedPadding - ((expandedPadding - compactPadding) * collapseProgress)

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(HomeTopRadius),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HomeTopRadius))
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Daily reflection",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = dhikr.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = (32 - (6 * collapseProgress)).sp,
                    lineHeight = (36 - (6 * collapseProgress)).sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = if (collapseProgress > 0.7f) 1 else 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = dhikr.translation ?: dhikr.arabicText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.84f),
                maxLines = if (collapseProgress > 0.45f) 1 else 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.graphicsLayer {
                    alpha = 1f - (collapseProgress * 0.45f)
                },
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer {
                    alpha = 1f - (collapseProgress * 0.65f)
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoStories,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Open dhikr",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun HomeCollectionGroup(
    items: List<Collection>,
    onOpenCollection: (Collection) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items.forEachIndexed { index, item ->
            HomeCollectionTile(
                collection = item,
                shape = homeGroupShape(index, items.size),
                onClick = { onOpenCollection(item) },
            )
        }
    }
}

@Composable
private fun HomeCollectionTile(
    collection: Collection,
    shape: RoundedCornerShape,
    onClick: () -> Unit,
) {
    Surface(
        color = homeTileColor(),
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun homeTileColor(): Color {
    return groupedTileContainerColor()
}

private fun homeGroupShape(index: Int, count: Int): RoundedCornerShape {
    return when {
        count == 1 -> RoundedCornerShape(HomeTopRadius)
        index == 0 -> RoundedCornerShape(
            topStart = HomeTopRadius,
            topEnd = HomeTopRadius,
            bottomStart = HomeInnerRadius,
            bottomEnd = HomeInnerRadius,
        )

        index == count - 1 -> RoundedCornerShape(
            topStart = HomeInnerRadius,
            topEnd = HomeInnerRadius,
            bottomStart = HomeBottomRadius,
            bottomEnd = HomeBottomRadius,
        )

        else -> RoundedCornerShape(HomeInnerRadius)
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HisnulMuslimTheme(settings = com.example.hisnulmuslim.core.model.AppSettings()) {
        HomeScreenContent(
            uiState = HomeUiState(
                collections = listOf(
                    Collection(
                        id = 1,
                        title = "Entering the morning",
                        subtitle = "Morning remembrance",
                        orderIndex = 1,
                        firstDhikrId = 1,
                        itemCount = 2,
                    ),
                    Collection(
                        id = 2,
                        title = "Entering the evening",
                        subtitle = "Evening remembrance",
                        orderIndex = 2,
                        firstDhikrId = 3,
                        itemCount = 1,
                    ),
                ),
                dailyHighlight = Dhikr(
                    id = 1,
                    collectionId = 1,
                    collectionTitle = "Entering the morning",
                    collectionSubtitle = "Morning remembrance",
                    collectionOrderIndex = 1,
                    title = "Morning remembrance",
                    arabicText = "اللهم بك أصبحنا",
                    transliteration = "Allahumma bika asbahna",
                    translation = "O Allah, by You we enter the morning.",
                    repeatCount = 1,
                    notes = null,
                    sourceReference = "Abu Dawud",
                    orderIndex = 1,
                    tags = listOf("morning"),
                ),
            ),
            contentPadding = PaddingValues(),
            onOpenSearch = {},
            onOpenCollection = {},
            onOpenDhikr = {},
        )
    }
}
