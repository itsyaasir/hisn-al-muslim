package com.example.hisnulmuslim.feature.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hisnulmuslim.core.designsystem.EmptyStateCard
import com.example.hisnulmuslim.core.designsystem.LocalAppFonts
import com.example.hisnulmuslim.core.designsystem.appTopBarContainerColor
import com.example.hisnulmuslim.core.designsystem.mergePaddingValues
import com.example.hisnulmuslim.core.model.Dhikr

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoritesScreen(
    contentPadding: PaddingValues,
    onOpenDhikr: (Dhikr) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val favorites by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current
    val topBarContainer = appTopBarContainerColor()
    val topBarTitleFont = LocalAppFonts.current.topBarTitle

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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = topBarContainer,
                        scrolledContainerColor = topBarContainer,
                    ),
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (favorites.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "No favorites yet",
                            subtitle = "Save meaningful adhkar here for quick return later.",
                        )
                    }
                } else {
                    items(favorites) { item ->
                        androidx.compose.material3.ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = { onOpenDhikr(item) },
                        ) {
                            androidx.compose.foundation.layout.Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                    text = item.translation ?: item.arabicText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
