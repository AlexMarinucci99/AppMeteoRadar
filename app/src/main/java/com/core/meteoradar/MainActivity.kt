package com.core.meteoradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.core.meteoradar.navigation.AppNavGraph
import com.core.meteoradar.ui.theme.MeteoRadarTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * L'unica Activity dell'applicazione, che funge da punto di ingresso principale.
 *
 * Questa classe è responsabile di configurare l'ambiente iniziale dell'app.
 *
 * L'annotazione `@AndroidEntryPoint` è fondamentale per l'integrazione con Dagger-Hilt.
 * Abilita l'iniezione delle dipendenze in questa Activity e in tutti i Fragment o View
 * ad essa collegati.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Metodo chiamato alla creazione dell'Activity.
     *
     * Qui viene definito il contenuto della UI dell'intera applicazione utilizzando Jetpack Compose.
     *
     * @param savedInstanceState Se l'activity viene ricreata dopo essere stata distrutta,
     *                           questo Bundle contiene lo stato salvato in precedenza.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // `setContent` è il punto di ingresso per definire il layout con Jetpack Compose.
        setContent {
            // `MeteoRadarTheme` è il tema personalizzato dell'applicazione (colori, tipografia, forme).
            // Avvolge l'intera UI per garantire uno stile coerente.
            MeteoRadarTheme {
                // `AppNavGraph` è il Composable che gestisce tutta la logica di navigazione
                // tra le diverse schermate (Welcome, Map, PinsList) dell'applicazione.
                AppNavGraph()
            }
        }
    }
}
