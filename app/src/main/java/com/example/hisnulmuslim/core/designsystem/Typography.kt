@file:OptIn(ExperimentalTextApi::class)

package com.example.hisnulmuslim.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.example.hisnulmuslim.R
import com.example.hisnulmuslim.core.model.ArabicFontFamily

private val BaseTypography = Typography()

private val GoogleFlex400 = FontFamily(
    Font(
        R.font.google_sans_flex,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
        ),
    ),
)

private val GoogleFlex600 = FontFamily(
    Font(
        R.font.google_sans_flex,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600),
            FontVariation.Setting("ROND", 100f),
        ),
    ),
)

private val AmiriArabic = FontFamily(
    Font(R.font.amiri_regular),
)

private val NotoNaskhArabic = FontFamily(
    Font(R.font.noto_naskh_arabic_regular),
)

private val ScheherazadeArabic = FontFamily(
    Font(R.font.scheherazade_new_regular),
)

val HisnulMuslimTypography = Typography(
    displayLarge = BaseTypography.displayLarge.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    displayMedium = BaseTypography.displayMedium.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    displaySmall = BaseTypography.displaySmall.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    headlineLarge = BaseTypography.headlineLarge.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    headlineMedium = BaseTypography.headlineMedium.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    headlineSmall = BaseTypography.headlineSmall.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    titleLarge = BaseTypography.titleLarge.copy(
        fontFamily = GoogleFlex400,
        fontFeatureSettings = "ss02, dlig",
    ),
    titleMedium = BaseTypography.titleMedium.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    titleSmall = BaseTypography.titleSmall.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    bodyLarge = BaseTypography.bodyLarge.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    bodyMedium = BaseTypography.bodyMedium.copy(
        fontFamily = GoogleFlex400,
        fontFeatureSettings = "ss02, dlig",
    ),
    bodySmall = BaseTypography.bodySmall.copy(
        fontFamily = GoogleFlex400,
        fontFeatureSettings = "ss02, dlig",
    ),
    labelLarge = BaseTypography.labelLarge.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    labelMedium = BaseTypography.labelMedium.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
    labelSmall = BaseTypography.labelSmall.copy(
        fontFamily = GoogleFlex600,
        fontFeatureSettings = "ss02, dlig",
    ),
)

data class AppFonts(
    val topBarTitle: FontFamily,
    val annotatedString: FontFamily,
    val arabic: FontFamily,
)

val HisnulMuslimAppFonts = AppFonts(
    topBarTitle = FontFamily(
        Font(
            R.font.google_sans_flex,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(900),
                FontVariation.width(112.5f),
                FontVariation.Setting("ROND", 35f),
            ),
        ),
    ),
    annotatedString = FontFamily(
        Font(
            R.font.google_sans_flex,
            weight = FontWeight.Normal,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(400),
            ),
        ),
        Font(
            R.font.google_sans_flex,
            weight = FontWeight.Bold,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(600),
                FontVariation.Setting("ROND", 100f),
            ),
        ),
    ),
    arabic = AmiriArabic,
)

val LocalAppFonts = staticCompositionLocalOf { HisnulMuslimAppFonts }

fun appFontsFor(arabicFontFamily: ArabicFontFamily): AppFonts {
    return HisnulMuslimAppFonts.copy(
        arabic = when (arabicFontFamily) {
            ArabicFontFamily.AMIRI -> AmiriArabic
            ArabicFontFamily.NOTO_NASKH -> NotoNaskhArabic
            ArabicFontFamily.SCHEHERAZADE -> ScheherazadeArabic
        },
    )
}
