package com.yasir.hisnulmuslim.feature.favorites

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yasir.hisnulmuslim.core.designsystem.EmptyStateCard
import com.yasir.hisnulmuslim.core.designsystem.LocalAppFonts
import com.yasir.hisnulmuslim.core.designsystem.LocalMotionPreferences
import com.yasir.hisnulmuslim.core.designsystem.appTopBarColors
import com.yasir.hisnulmuslim.core.designsystem.appTopBarContainerColor
import com.yasir.hisnulmuslim.core.designsystem.groupedTileContainerColor
import com.yasir.hisnulmuslim.core.designsystem.mergePaddingValues
import com.yasir.hisnulmuslim.core.model.Dhikr

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoritesScreen(
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onOpenDhikr: (Dhikr) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val favorites by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current
    val topBarContainer = appTopBarContainerColor()
    val topBarTitleFont = LocalAppFonts.current.topBarTitle
    var pendingRemovedFavorite by remember { mutableStateOf<Dhikr?>(null) }

    LaunchedEffect(pendingRemovedFavorite?.id) {
        val removedFavorite = pendingRemovedFavorite ?: return@LaunchedEffect
        snackbarHostState.currentSnackbarData?.dismiss()
        val result = snackbarHostState.showSnackbar(
            message = "Removed from favorites",
            actionLabel = "Undo",
            withDismissAction = false,
            duration = SnackbarDuration.Long,
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.restoreFavorite(removedFavorite.id)
        }
        pendingRemovedFavorite = null
    }

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
                    title = {
                        Text(
                            text = "Favorites",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                lineHeight = MaterialTheme.typography.headlineLarge.lineHeight,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                fontFamily = topBarTitleFont,
                            ),
                            modifier = Modifier
                                .padding(top = contentPadding.calculateTopPadding())
                                .padding(vertical = 14.dp),
                        )
                    },
                    subtitle = {},
                    titleHorizontalAlignment = Alignment.CenterHorizontally,
                    scrollBehavior = scrollBehavior,
                    windowInsets = WindowInsets(),
                    colors = appTopBarColors(),
                )
            },
        ) { innerPadding ->
            val listInsets = mergePaddingValues(innerPadding, contentPadding, layoutDirection)
            val listContentPadding = mergePaddingValues(
                listInsets,
                PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                layoutDirection,
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp)
                    .background(topBarContainer),
                contentPadding = listContentPadding,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (favorites.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No favorites yet",
                            subtitle = "Save meaningful dhikr here for quick return later.",
                        )
                    }
                } else {
                    itemsIndexed(
                        items = favorites,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        val shape = favoriteGroupShape(index, favorites.size)
                        val dismissState = rememberFavoriteDismissState(dhikrId = item.id)
                        LaunchedEffect(dismissState.currentValue, item.id) {
                            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.removeFavorite(item.id)
                                pendingRemovedFavorite = item
                            }
                        }
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                FavoriteDismissBackground(
                                    state = dismissState,
                                    shape = shape,
                                )
                            },
                            modifier = Modifier,
                        ) {
                            FavoriteDhikrTile(
                                dhikr = item,
                                shape = shape,
                                onClick = { onOpenDhikr(item) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberFavoriteDismissState(
    dhikrId: Long,
): SwipeToDismissBoxState {
    return remember(dhikrId) {
        SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            positionalThreshold = { it * 0.32f },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteDismissBackground(
    state: SwipeToDismissBoxState,
    shape: RoundedCornerShape,
) {
    val motionPreferences = LocalMotionPreferences.current
    val dismissing = state.dismissDirection == SwipeToDismissBoxValue.EndToStart
    val iconScale by animateFloatAsState(
        targetValue = if (dismissing) 1.08f else 0.92f,
        animationSpec = motionPreferences.expressiveSpatial(),
        label = "favoriteDismissIconScale",
    )

    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = shape,
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset {
                    IntOffset(
                        x = ((1f - state.progress) * 18.dp.toPx()).toInt(),
                        y = 0,
                    )
                },
            ) {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        },
                )
            }
        }
    }
}

@Composable
private fun FavoriteDhikrTile(
    dhikr: Dhikr,
    shape: RoundedCornerShape,
    onClick: () -> Unit,
) {
    Surface(
        color = groupedTileContainerColor(),
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = dhikr.collectionTitle,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            )
            val subtitle = when {
                dhikr.title.isNotBlank() && dhikr.title != dhikr.collectionTitle -> dhikr.title
                !dhikr.collectionSubtitle.isNullOrBlank() -> dhikr.collectionSubtitle
                else -> null
            }
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

private val FavoritesTopRadius = 28.dp
private val FavoritesInnerRadius = 8.dp
private val FavoritesBottomRadius = 28.dp

private fun favoriteGroupShape(index: Int, count: Int): RoundedCornerShape {
    return when {
        count == 1 -> RoundedCornerShape(FavoritesTopRadius)
        index == 0 -> RoundedCornerShape(
            topStart = FavoritesTopRadius,
            topEnd = FavoritesTopRadius,
            bottomStart = FavoritesInnerRadius,
            bottomEnd = FavoritesInnerRadius,
        )

        index == count - 1 -> RoundedCornerShape(
            topStart = FavoritesInnerRadius,
            topEnd = FavoritesInnerRadius,
            bottomStart = FavoritesBottomRadius,
            bottomEnd = FavoritesBottomRadius,
        )

        else -> RoundedCornerShape(FavoritesInnerRadius)
    }
}
