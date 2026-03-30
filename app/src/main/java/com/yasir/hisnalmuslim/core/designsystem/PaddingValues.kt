package com.yasir.hisnalmuslim.core.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection

fun mergePaddingValues(
    first: PaddingValues,
    second: PaddingValues,
    layoutDirection: LayoutDirection,
): PaddingValues {
    return PaddingValues(
        start = first.calculateStartPadding(layoutDirection) + second.calculateStartPadding(layoutDirection),
        top = first.calculateTopPadding() + second.calculateTopPadding(),
        end = first.calculateEndPadding(layoutDirection) + second.calculateEndPadding(layoutDirection),
        bottom = first.calculateBottomPadding() + second.calculateBottomPadding(),
    )
}
