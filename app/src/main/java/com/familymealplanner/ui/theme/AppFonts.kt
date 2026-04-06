package com.familymealplanner.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.familymealplanner.R

enum class AppFont(val displayName: String, val description: String) {
    ROBOTO_LIGHT("Roboto Light", "Thin and elegant"),
    ROBOTO_REGULAR("Roboto Regular", "Default system font"),
    ROBOTO_MEDIUM("Roboto Medium", "Slightly bolder"),
    RUBIK_LIGHT("Rubik Light", "Soft and friendly"),
    RUBIK_REGULAR("Rubik Regular", "Rounded and readable"),
    RUBIK_MEDIUM("Rubik Medium", "Bold and friendly")
}

object AppFonts {
    // Roboto font families - each weight variant uses that weight as Normal
    private val robotoLightFamily by lazy {
        try {
            FontFamily(
                Font(R.font.roboto_light, FontWeight.Normal),
                Font(R.font.roboto_light, FontWeight.Bold),
                Font(R.font.roboto_light, FontWeight.SemiBold),
                Font(R.font.roboto_light, FontWeight.Medium)
            )
        } catch (e: Exception) {
            FontFamily.Default
        }
    }
    
    private val robotoRegularFamily by lazy {
        try {
            FontFamily(
                Font(R.font.roboto_regular, FontWeight.Normal),
                Font(R.font.roboto_medium, FontWeight.Bold),
                Font(R.font.roboto_medium, FontWeight.SemiBold),
                Font(R.font.roboto_regular, FontWeight.Medium)
            )
        } catch (e: Exception) {
            FontFamily.Default
        }
    }
    
    private val robotoMediumFamily by lazy {
        try {
            FontFamily(
                Font(R.font.roboto_medium, FontWeight.Normal),
                Font(R.font.roboto_medium, FontWeight.Bold),
                Font(R.font.roboto_medium, FontWeight.SemiBold),
                Font(R.font.roboto_medium, FontWeight.Medium)
            )
        } catch (e: Exception) {
            FontFamily.Default
        }
    }
    
    // Rubik font families - each weight variant uses that weight as Normal
    private val rubikLightFamily by lazy {
        try {
            FontFamily(
                Font(R.font.rubik_light, FontWeight.Normal),
                Font(R.font.rubik_light, FontWeight.Bold),
                Font(R.font.rubik_light, FontWeight.SemiBold),
                Font(R.font.rubik_light, FontWeight.Medium)
            )
        } catch (e: Exception) {
            FontFamily.Default
        }
    }
    
    private val rubikRegularFamily by lazy {
        try {
            FontFamily(
                Font(R.font.rubik_regular, FontWeight.Normal),
                Font(R.font.rubik_medium, FontWeight.Bold),
                Font(R.font.rubik_medium, FontWeight.SemiBold),
                Font(R.font.rubik_regular, FontWeight.Medium)
            )
        } catch (e: Exception) {
            FontFamily.Default
        }
    }
    
    private val rubikMediumFamily by lazy {
        try {
            FontFamily(
                Font(R.font.rubik_medium, FontWeight.Normal),
                Font(R.font.rubik_medium, FontWeight.Bold),
                Font(R.font.rubik_medium, FontWeight.SemiBold),
                Font(R.font.rubik_medium, FontWeight.Medium)
            )
        } catch (e: Exception) {
            FontFamily.Default
        }
    }
    
    fun getFontFamily(fontName: String): FontFamily {
        return when (fontName) {
            "Roboto Light" -> robotoLightFamily
            "Roboto Regular" -> robotoRegularFamily
            "Roboto Medium" -> robotoMediumFamily
            "Rubik Light" -> rubikLightFamily
            "Rubik Regular" -> rubikRegularFamily
            "Rubik Medium" -> rubikMediumFamily
            else -> FontFamily.Default
        }
    }
}
