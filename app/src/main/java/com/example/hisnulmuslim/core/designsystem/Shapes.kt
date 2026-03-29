package com.example.hisnulmuslim.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val HisnulMuslimShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Immutable
data class ExpressiveShapes(
    val heroCard: RoundedCornerShape = RoundedCornerShape(
        topStart = 32.dp,
        topEnd = 32.dp,
        bottomStart = 18.dp,
        bottomEnd = 32.dp,
    ),
    val actionCard: RoundedCornerShape = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = 16.dp,
        bottomEnd = 24.dp,
    ),
    val chipGroup: RoundedCornerShape = RoundedCornerShape(20.dp),
)

val LocalExpressiveShapes = staticCompositionLocalOf { ExpressiveShapes() }
