package com.example.hisnulmuslim.core.designsystem

import android.animation.ValueAnimator
import android.os.Build
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class MotionPreferences(
    val animationsEnabled: Boolean = true,
) {
    fun <T> expressiveSpatial(): FiniteAnimationSpec<T> {
        return if (animationsEnabled) {
            spring(
                dampingRatio = 0.82f,
                stiffness = 380f,
            )
        } else {
            snap()
        }
    }

    fun <T> expressiveFastEffect(): FiniteAnimationSpec<T> {
        return if (animationsEnabled) {
            spring(
                dampingRatio = 1f,
                stiffness = 700f,
            )
        } else {
            snap()
        }
    }
}

val LocalMotionPreferences = staticCompositionLocalOf { MotionPreferences() }

@Composable
fun rememberMotionPreferences(): MotionPreferences {
    return MotionPreferences(
        animationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ValueAnimator.areAnimatorsEnabled()
        } else {
            true
        },
    )
}
