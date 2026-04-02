package com.yasir.hisnalmuslim.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.yasir.hisnalmuslim.core.designsystem.LocalMotionPreferences
import com.yasir.hisnalmuslim.feature.adhkardetail.DhikrDetailScreen
import com.yasir.hisnalmuslim.feature.favorites.FavoritesScreen
import com.yasir.hisnalmuslim.feature.home.HomeScreen
import com.yasir.hisnalmuslim.feature.search.SearchScreen
import com.yasir.hisnalmuslim.feature.settings.SettingsScreen
import com.yasir.hisnalmuslim.notifications.NotificationOpenTarget

@Composable
fun HisnulMuslimNavHost(
    modifier: Modifier = Modifier,
    pendingNotificationTarget: NotificationOpenTarget? = null,
    onPendingNotificationTargetConsumed: () -> Unit = {},
) {
    val activity = checkNotNull(LocalActivity.current)
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
    val motionPreferences = LocalMotionPreferences.current
    @Suppress("UNCHECKED_CAST")
    val backStack = rememberNavBackStack(AppDestination.Home) as NavBackStack<AppDestination>
    val currentDestination = backStack.lastOrNull()
    val snackbarHostState = remember { SnackbarHostState() }
    var settingsResetSignal by remember { mutableIntStateOf(0) }
    val entryDecorators = listOf<NavEntryDecorator<AppDestination>>(
        rememberSaveableStateHolderNavEntryDecorator<AppDestination>(),
        rememberViewModelStoreNavEntryDecorator<AppDestination>(viewModelStoreOwner),
    )

    LaunchedEffect(pendingNotificationTarget) {
        val target = pendingNotificationTarget ?: return@LaunchedEffect
        backStack.clear()
        backStack.add(AppDestination.Home)
        backStack.add(
            AppDestination.DhikrDetail(
                dhikrId = target.dhikrId,
                collectionId = target.collectionId,
            ),
        )
        onPendingNotificationTargetConsumed()
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            TopLevelFloatingNavigationBar(
                currentDestination = currentDestination,
                onNavigate = { destination ->
                    backStack.navigateToTopLevel(destination.destination)
                },
                onReselect = { destination ->
                    when (destination.destination) {
                        AppDestination.Settings -> settingsResetSignal++
                        AppDestination.Home -> backStack.navigateToTopLevel(AppDestination.Home)
                        AppDestination.Favorites -> backStack.navigateToTopLevel(AppDestination.Favorites)
                        else -> Unit
                    }
                },
            )
        },
    ) { innerPadding ->
        SharedTransitionLayout {
            NavDisplay(
                backStack = backStack,
                modifier = Modifier.fillMaxSize(),
                onBack = { backStack.onBack(activity::finish) },
                entryDecorators = entryDecorators,
                transitionSpec = {
                    fadeIn(animationSpec = motionPreferences.expressiveFastEffect())
                        .togetherWith(fadeOut(animationSpec = motionPreferences.expressiveFastEffect()))
                },
                popTransitionSpec = {
                    fadeIn(animationSpec = motionPreferences.expressiveFastEffect())
                        .togetherWith(fadeOut(animationSpec = motionPreferences.expressiveFastEffect()))
                },
                predictivePopTransitionSpec = { _ ->
                    fadeIn(animationSpec = motionPreferences.expressiveFastEffect())
                        .togetherWith(fadeOut(animationSpec = motionPreferences.expressiveFastEffect()))
                },
                entryProvider = entryProvider<AppDestination> {
                    entry<AppDestination.Home> { _ ->
                        HomeScreen(
                            contentPadding = innerPadding,
                            onOpenSearch = { backStack.navigateToTopLevel(AppDestination.Search) },
                            onOpenCollection = { collection ->
                                backStack.add(
                                    AppDestination.DhikrDetail(
                                        dhikrId = collection.firstDhikrId,
                                        collectionId = collection.id,
                                    ),
                                )
                            },
                            onOpenDhikr = { dhikr ->
                                backStack.add(
                                    AppDestination.DhikrDetail(
                                        dhikrId = dhikr.id,
                                        collectionId = dhikr.collectionId,
                                    ),
                                )
                            },
                        )
                    }
                    entry<AppDestination.DhikrDetail> { destination ->
                        DhikrDetailScreen(
                            dhikrId = destination.dhikrId,
                            collectionId = destination.collectionId,
                            contentPadding = innerPadding,
                            onBack = { backStack.onBack(activity::finish) },
                        )
                    }
                    entry<AppDestination.Search> { _ ->
                        SearchScreen(
                            contentPadding = innerPadding,
                            onBack = { backStack.onBack(activity::finish) },
                            onOpenCollection = { collection ->
                                backStack.add(
                                    AppDestination.DhikrDetail(
                                        dhikrId = collection.firstDhikrId,
                                        collectionId = collection.id,
                                    )
                                )
                            },
                        )
                    }
                    entry<AppDestination.Favorites> { _ ->
                        FavoritesScreen(
                            contentPadding = innerPadding,
                            snackbarHostState = snackbarHostState,
                            onOpenDhikr = { dhikr ->
                                backStack.add(
                                    AppDestination.DhikrDetail(
                                        dhikrId = dhikr.id,
                                        collectionId = dhikr.collectionId,
                                    ),
                                )
                            },
                        )
                    }
                    entry<AppDestination.Settings> { _ ->
                        SettingsScreen(
                            contentPadding = innerPadding,
                            resetToMainSignal = settingsResetSignal,
                        )
                    }
                },
            )
        }
    }
}

internal fun AppDestination?.shouldShowTopLevelNavigation(): Boolean {
    return TopLevelDestination.entries.any { destination -> destination.destination == this }
}

private fun MutableList<AppDestination>.onBack(finish: () -> Unit) {
    if (size > 1) removeLastOrNull() else finish()
}

private fun MutableList<AppDestination>.navigateToTopLevel(destination: AppDestination) {
    if (isEmpty() || first() != AppDestination.Home) {
        clear()
        add(AppDestination.Home)
    }

    if (destination == AppDestination.Home) {
        if (size > 1) subList(1, size).clear()
        return
    }

    if (size < 2) add(destination) else this[1] = destination
    if (size > 2) subList(2, size).clear()
}
