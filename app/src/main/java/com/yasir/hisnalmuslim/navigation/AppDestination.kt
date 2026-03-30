package com.yasir.hisnalmuslim.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppDestination : NavKey {
    @Serializable
    object Home : AppDestination

    @Serializable
    object Search : AppDestination

    @Serializable
    object Favorites : AppDestination

    @Serializable
    object Settings : AppDestination

    @Serializable
    data class DhikrDetail(
        val dhikrId: Long,
        val collectionId: Long,
    ) : AppDestination
}
