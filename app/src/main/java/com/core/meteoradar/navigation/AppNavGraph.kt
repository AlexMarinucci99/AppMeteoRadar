package com.core.meteoradar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.core.meteoradar.feature.map.MapScreen
import com.core.meteoradar.feature.map.MapViewModel
import com.core.meteoradar.feature.pins.PinsListScreen
import com.core.meteoradar.feature.welcome.WelcomeScreen

/**
 * Gestisce la navigazione principale dell'applicazione MeteoRadar.
 * Definisce i percorsi (Routes), le schermate e il passaggio di parametri tra di esse.
 */
@Composable
fun AppNavGraph() {
    // Inizializza il controller di navigazione di Jetpack Compose
    val nav = rememberNavController()

    // Configurazione del NavHost: definisce il punto di partenza (WELCOME) e le rotte disponibili
    NavHost(
        navController = nav,
        startDestination = Routes.WELCOME
    ) {

        // --- Schermata iniziale di benvenuto ---
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onContinueAsGuest = {
                    // Naviga verso la mappa e rimuove la schermata di welcome dallo stack
                    nav.navigate(Routes.MAP) {
                        popUpTo(0)
                    }
                }
            )
        }

        // --- Schermata Mappa ---
        // Supporta parametri opzionali 'lat' (latitudine) e 'lon' (longitudine) per centrare la mappa su un punto specifico
        composable(
            route = "${Routes.MAP}?lat={lat}&lon={lon}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; nullable = true },
                navArgument("lon") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            // Estrazione e conversione delle coordinate dagli argomenti della rotta
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()

            MapScreen(
                onOpenPins = { nav.navigate(Routes.PINS) },
                onLogout = {
                    // Torna alla schermata di benvenuto resettando lo stack di navigazione
                    nav.navigate(Routes.WELCOME) { popUpTo(0) }
                },
                initialFocusLat = lat,
                initialFocusLon = lon
            )
        }

        // --- Schermata Lista dei Pin salvati ---
        composable(Routes.PINS) {
            // Ottiene il ViewModel tramite Hilt, iniettandolo in questa schermata
            val mapVm: MapViewModel = hiltViewModel()
            val ui by mapVm.ui.collectAsState()

            PinsListScreen(
                vm = mapVm,
                onBack = { nav.popBackStack() },
                onSelectPin = { lat, lon ->
                    // Naviga verso la mappa centrata sul pin selezionato
                    nav.navigate(Routes.mapWith(lat, lon)) {
                        // Rimuove la vecchia istanza della mappa per aprirne una nuova con le nuove coordinate
                        popUpTo(Routes.MAP) { inclusive = true }
                    }
                }
            )
        }
    }
}
