package com.yasir.hisnalmuslim.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yasir.hisnalmuslim.core.designsystem.EmptyStateCard
import com.yasir.hisnalmuslim.core.designsystem.LocalAppFonts
import com.yasir.hisnalmuslim.core.designsystem.appTopBarColors
import com.yasir.hisnalmuslim.core.designsystem.appTopBarContainerColor
import com.yasir.hisnalmuslim.core.designsystem.groupedTileContainerColor
import com.yasir.hisnalmuslim.core.designsystem.mergePaddingValues
import com.yasir.hisnalmuslim.core.model.AppSettings
import com.yasir.hisnalmuslim.core.model.Collection
import com.yasir.hisnalmuslim.core.model.CollectionTitleLanguage
import com.yasir.hisnalmuslim.core.model.displayTitle

private val SearchTopRadius = 28.dp
private val SearchInnerRadius = 8.dp
private val SearchBottomRadius = 28.dp
private val SearchPaneMaxWidth = 600.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onOpenCollection: (Collection) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current
    val topBarTitleFont = LocalAppFonts.current.topBarTitle
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
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Search",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                lineHeight = MaterialTheme.typography.headlineLarge.lineHeight,
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
                    .widthIn(max = SearchPaneMaxWidth)
                    .background(topBarContainer),
                contentPadding = listContentPadding,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    SearchField(
                        query = uiState.query,
                        onQueryChange = viewModel::onQueryChange,
                    )
                }

                if (uiState.query.isBlank()) {
                    item {
                        EmptyStateCard(
                            title = "Start searching",
                            subtitle = "Search collections by their name.",
                        )
                    }
                } else if (uiState.results.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No matches yet",
                            subtitle = "Try a different collection name.",
                        )
                    }
                } else {
                    item {
                        SearchResultsGroup(
                            items = uiState.results,
                            settings = uiState.settings,
                            onOpenCollection = onOpenCollection,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Surface(
        color = searchTileColor(),
        shape = RoundedCornerShape(SearchTopRadius),
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(SearchTopRadius),
            singleLine = true,
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                )
            },
            placeholder = { Text("Collection name") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun SearchResultsGroup(
    items: List<Collection>,
    settings: AppSettings,
    onOpenCollection: (Collection) -> Unit,
) {
    androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items.forEachIndexed { index, item ->
            SearchResultTile(
                collection = item,
                settings = settings,
                shape = searchGroupShape(index, items.size),
                onClick = { onOpenCollection(item) },
            )
        }
    }
}

@Composable
private fun SearchResultTile(
    collection: Collection,
    settings: AppSettings,
    shape: RoundedCornerShape,
    onClick: () -> Unit,
) {
    val arabicFont = LocalAppFonts.current.arabic
    val isArabicTitle = settings.collectionTitleLanguage == CollectionTitleLanguage.ARABIC
    Surface(
        color = searchTileColor(),
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick),
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (isArabicTitle) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = collection.displayTitle(settings),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        fontFamily = arabicFont,
                        textAlign = TextAlign.Start,
                    )
                }
            } else {
                Text(
                    text = collection.displayTitle(settings),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
            Text(
                text = "${collection.itemCount} adhkar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun searchTileColor(): Color {
    return groupedTileContainerColor()
}

private fun searchGroupShape(index: Int, count: Int): RoundedCornerShape {
    return when {
        count == 1 -> RoundedCornerShape(SearchTopRadius)
        index == 0 -> RoundedCornerShape(
            topStart = SearchTopRadius,
            topEnd = SearchTopRadius,
            bottomStart = SearchInnerRadius,
            bottomEnd = SearchInnerRadius,
        )

        index == count - 1 -> RoundedCornerShape(
            topStart = SearchInnerRadius,
            topEnd = SearchInnerRadius,
            bottomStart = SearchBottomRadius,
            bottomEnd = SearchBottomRadius,
        )

        else -> RoundedCornerShape(SearchInnerRadius)
    }
}
