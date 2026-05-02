package be.corentinvanhaeren.sandwix.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SandwixPrimaryLight,
    onPrimary = SandwixOnPrimaryLight,
    primaryContainer = SandwixPrimaryContainerLight,
    onPrimaryContainer = SandwixOnPrimaryContainerLight,
    secondary = SandwixSecondaryLight,
    onSecondary = SandwixOnSecondaryLight,
    secondaryContainer = SandwixSecondaryContainerLight,
    onSecondaryContainer = SandwixOnSecondaryContainerLight,
    tertiary = SandwixTertiaryLight,
    onTertiary = SandwixOnTertiaryLight,
    tertiaryContainer = SandwixTertiaryContainerLight,
    onTertiaryContainer = SandwixOnTertiaryContainerLight,
    error = SandwixErrorLight,
    onError = SandwixOnErrorLight,
    background = SandwixBackgroundLight,
    onBackground = SandwixOnBackgroundLight,
    surface = SandwixSurfaceLight,
    onSurface = SandwixOnSurfaceLight,
    surfaceVariant = SandwixSurfaceVariantLight,
    onSurfaceVariant = SandwixOnSurfaceVariantLight,
    outline = SandwixOutlineLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = SandwixPrimaryDark,
    onPrimary = SandwixOnPrimaryDark,
    primaryContainer = SandwixPrimaryContainerDark,
    onPrimaryContainer = SandwixOnPrimaryContainerDark,
    secondary = SandwixSecondaryDark,
    onSecondary = SandwixOnSecondaryDark,
    secondaryContainer = SandwixSecondaryContainerDark,
    onSecondaryContainer = SandwixOnSecondaryContainerDark,
    tertiary = SandwixTertiaryDark,
    onTertiary = SandwixOnTertiaryDark,
    tertiaryContainer = SandwixTertiaryContainerDark,
    onTertiaryContainer = SandwixOnTertiaryContainerDark,
    error = SandwixErrorDark,
    onError = SandwixOnErrorDark,
    background = SandwixBackgroundDark,
    onBackground = SandwixOnBackgroundDark,
    surface = SandwixSurfaceDark,
    onSurface = SandwixOnSurfaceDark,
    surfaceVariant = SandwixSurfaceVariantDark,
    onSurfaceVariant = SandwixOnSurfaceVariantDark,
    outline = SandwixOutlineDark,
)

@Composable
fun SandwixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (context as Activity).window
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.background.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
