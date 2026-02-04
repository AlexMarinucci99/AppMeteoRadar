package com.core.meteoradar.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Schema di colori personalizzato per il tema scuro dell'applicazione.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * Schema di colori personalizzato per il tema chiaro dell'applicazione.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* È possibile sovrascrivere altri colori predefiniti qui, ad esempio:
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * Composable principale per il tema dell'applicazione "MeteoRadar".
 * Questo tema gestisce la selezione tra tema chiaro e scuro e supporta i colori dinamici
 * su Android 12 e versioni successive.
 *
 * @param darkTheme Indica se forzare il tema scuro. Se non specificato, si basa sulle impostazioni di sistema.
 * @param dynamicColor Abilita/disabilita l'uso dei colori dinamici (Material You). Attivo per impostazione predefinita.
 * @param content Il contenuto dell'interfaccia utente a cui applicare il tema.
 */
@Composable
fun MeteoRadarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // I colori dinamici sono disponibili su Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Se i colori dinamici sono abilitati e il sistema li supporta (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            // Seleziona lo schema di colori dinamico (chiaro o scuro) in base al tema corrente
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
