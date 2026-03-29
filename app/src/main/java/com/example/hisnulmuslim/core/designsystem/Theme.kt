package com.example.hisnulmuslim.core.designsystem

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.example.hisnulmuslim.core.model.AppSettings
import com.example.hisnulmuslim.core.model.DefaultThemeSeedColor
import com.example.hisnulmuslim.core.model.ThemeMode
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme

private val CalmLightColors = lightColorScheme(
    primary = Forest700,
    onPrimary = Sand50,
    primaryContainer = Sage100,
    onPrimaryContainer = Ink900,
    secondary = Forest600,
    onSecondary = Sand50,
    secondaryContainer = Color(0xFFE8F1EA),
    onSecondaryContainer = Ink900,
    tertiary = Gold300,
    onTertiary = Ink900,
    background = Sand50,
    onBackground = Ink900,
    surface = Color(0xFFFDFBF7),
    onSurface = Ink900,
    surfaceVariant = Color(0xFFE7E4DD),
    onSurfaceVariant = Color(0xFF454841),
    outline = Color(0xFF72786D),
)

private val CalmDarkColors = darkColorScheme(
    primary = Sage300,
    onPrimary = Ink900,
    primaryContainer = Forest600,
    onPrimaryContainer = Sand50,
    secondary = Color(0xFFAECBC0),
    onSecondary = Ink900,
    secondaryContainer = Color(0xFF35554B),
    onSecondaryContainer = Sand50,
    tertiary = Gold300,
    onTertiary = Ink900,
    background = Color(0xFF0F1715),
    onBackground = Color(0xFFE4E3DC),
    surface = Color(0xFF131C19),
    onSurface = Color(0xFFE4E3DC),
    surfaceVariant = Color(0xFF2D3430),
    onSurfaceVariant = Color(0xFFC3C8BF),
    outline = Color(0xFF8D9389),
)

val LocalBlackTheme = staticCompositionLocalOf { false }

@Composable
fun appTopBarContainerColor(): Color {
    return if (LocalBlackTheme.current) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
}

@Composable
fun detailTopBarContainerColor(): Color {
    return if (LocalBlackTheme.current) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
}

@Composable
fun groupedTileContainerColor(): Color {
    return if (LocalBlackTheme.current) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceBright
    }
}

@Composable
fun HisnulMuslimTheme(
    settings: AppSettings,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val view = LocalView.current
    val motionPreferences = rememberMotionPreferences()
    val useDarkTheme = when (settings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val useBlackTheme = settings.pureBlackThemeEnabled && useDarkTheme
    val colorScheme = when {
        settings.dynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        useDarkTheme -> CalmDarkColors
        else -> CalmLightColors
    }
    val generatedColorScheme = rememberDynamicColorScheme(
        seedColor = if (settings.themeSeedColor == DefaultThemeSeedColor) {
            colorScheme.primary
        } else {
            Color(settings.themeSeedColor)
        },
        isDark = useDarkTheme,
        specVersion = if (useBlackTheme) {
            ColorSpec.SpecVersion.SPEC_2021
        } else {
            ColorSpec.SpecVersion.SPEC_2025
        },
        isAmoled = useBlackTheme,
    )
    val scheme = if (
        settings.themeSeedColor == DefaultThemeSeedColor &&
        !useBlackTheme
    ) {
        colorScheme
    } else {
        generatedColorScheme
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !useDarkTheme
                isAppearanceLightNavigationBars = !useDarkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalAppFonts provides HisnulMuslimAppFonts,
        LocalBlackTheme provides useBlackTheme,
        LocalExpressiveShapes provides ExpressiveShapes(),
        LocalMotionPreferences provides motionPreferences,
        LocalDensity provides Density(
            density = density.density,
            fontScale = density.fontScale * settings.fontScale,
        ),
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = HisnulMuslimTypography,
            shapes = HisnulMuslimShapes,
            content = content,
        )
    }
}
