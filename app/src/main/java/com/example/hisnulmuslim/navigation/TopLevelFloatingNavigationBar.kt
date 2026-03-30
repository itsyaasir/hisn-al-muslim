package com.example.hisnulmuslim.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.material3.rememberTooltipState
import androidx.compose.ui.zIndex
import com.example.hisnulmuslim.core.designsystem.LocalMotionPreferences

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopLevelFloatingNavigationBar(
    currentDestination: AppDestination?,
    onNavigate: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionPreferences = LocalMotionPreferences.current
    val layoutDirection = LocalLayoutDirection.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val systemBarsInsets = WindowInsets.systemBars.asPaddingValues()
    val cutoutInsets = WindowInsets.displayCutout.asPaddingValues()
    val wide = remember(windowSizeClass) {
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    }
    val toolbarScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(FloatingToolbarExitDirection.Bottom)

    AnimatedVisibility(
        visible = currentDestination.shouldShowTopLevelNavigation(),
        enter = slideInVertically(animationSpec = motionPreferences.expressiveSpatial()) { it } +
            fadeIn(animationSpec = motionPreferences.expressiveFastEffect()),
        exit = slideOutVertically(animationSpec = motionPreferences.expressiveSpatial()) { it } +
            fadeOut(animationSpec = motionPreferences.expressiveFastEffect()),
        modifier = modifier.fillMaxWidth(),
    ) {
        val toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f)
        val toolbarContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        val selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
        val selectedContentColor = MaterialTheme.colorScheme.secondary

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = cutoutInsets.calculateStartPadding(layoutDirection),
                    end = cutoutInsets.calculateEndPadding(layoutDirection),
                ),
            contentAlignment = Alignment.Center,
        ) {
            HorizontalFloatingToolbar(
                expanded = true,
                scrollBehavior = toolbarScrollBehavior,
                colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(
                    toolbarContainerColor = toolbarContainerColor,
                    toolbarContentColor = toolbarContentColor,
                ),
                modifier = Modifier
                    .padding(
                        top = FloatingToolbarDefaults.ScreenOffset,
                        bottom = systemBarsInsets.calculateBottomPadding() +
                            FloatingToolbarDefaults.ScreenOffset,
                    )
                    .zIndex(1f),
            ) {
                TopLevelDestination.entries.fastForEach { destination ->
                    val selected by remember(currentDestination) {
                        derivedStateOf { currentDestination == destination.destination }
                    }
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Above,
                        ),
                        tooltip = { PlainTooltip { Text(destination.label) } },
                        state = rememberTooltipState(),
                    ) {
                        ToggleButton(
                            checked = selected,
                            onCheckedChange = if (selected) {
                                {}
                            } else {
                                { onNavigate(destination) }
                            },
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = toolbarContentColor,
                                checkedContainerColor = selectedContainerColor,
                                checkedContentColor = selectedContentColor,
                            ),
                            shapes = ToggleButtonDefaults.shapes(
                                CircleShape,
                                CircleShape,
                                CircleShape,
                            ),
                            modifier = Modifier.height(56.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Crossfade(
                                    targetState = selected,
                                    animationSpec = motionPreferences.expressiveFastEffect(),
                                    label = "top_level_nav_icon",
                                ) { isSelected ->
                                    Icon(
                                        imageVector = if (isSelected) {
                                            destination.selectedIcon
                                        } else {
                                            destination.icon
                                        },
                                        contentDescription = destination.label,
                                    )
                                }
                                AnimatedVisibility(
                                    visible = selected || wide,
                                    enter = expandHorizontally(
                                        animationSpec = motionPreferences.expressiveSpatial(),
                                    ) + fadeIn(
                                        animationSpec = motionPreferences.expressiveFastEffect(),
                                    ),
                                    exit = shrinkHorizontally(
                                        animationSpec = motionPreferences.expressiveSpatial(),
                                    ) + fadeOut(
                                        animationSpec = motionPreferences.expressiveFastEffect(),
                                    ),
                                ) {
                                    Text(
                                        text = destination.label,
                                        modifier = Modifier.padding(start = ButtonDefaults.IconSpacing),
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Clip,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
