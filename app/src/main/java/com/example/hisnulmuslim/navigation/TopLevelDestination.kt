package com.example.hisnulmuslim.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val destination: AppDestination,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    Home(
        destination = AppDestination.Home,
        label = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
    ),
    Favorites(
        destination = AppDestination.Favorites,
        label = "Favorites",
        icon = Icons.Outlined.Bookmarks,
        selectedIcon = Icons.Filled.Bookmarks,
    ),
    Settings(
        destination = AppDestination.Settings,
        label = "Settings",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
    ),
}
