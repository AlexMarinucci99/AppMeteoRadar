package com.core.meteoradar.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

/**
 * Composable che fornisce uno sfondo personalizzato per le schermate dell'app.
 * Include un'immagine di sfondo e un velo (scrim) scuro sovrapposto per migliorare
 * la leggibilità dei contenuti in primo piano.
 *
 * @param imageRes L'ID della risorsa drawable da utilizzare come sfondo.
 * @param darkScrim Il livello di opacità (0.0f a 1.0f) del velo nero sovrapposto all'immagine.
 * @param content Il contenuto della schermata che verrà visualizzato sopra lo sfondo.
 */
@Composable
fun ScreenBackground(
    @DrawableRes imageRes: Int,
    darkScrim: Float = 0.35f,
    content: @Composable () -> Unit
) {
    // Box principale che occupa tutto lo schermo e sovrappone i suoi elementi
    Box(Modifier.fillMaxSize()) {
        // 1. Immagine di sfondo
        Image(
            painter = painterResource(imageRes),
            contentDescription = null, // Descrizione nulla perché è un elemento puramente decorativo
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Taglia l'immagine per riempire tutto lo spazio disponibile
        )

        // 2. Velo scuro (scrim) per aumentare il contrasto con il testo
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = darkScrim),
                            Color.Black.copy(alpha = darkScrim)
                        )
                    )
                )
        )

        // 3. Contenuto della schermata
        content()
    }
}
