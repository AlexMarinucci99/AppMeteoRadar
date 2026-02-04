@file:OptIn(ExperimentalMaterial3Api::class)

package com.core.meteoradar.feature.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.core.meteoradar.R
import com.core.meteoradar.ui.common.ScreenBackground
import java.util.Calendar

/**
 * Schermata di benvenuto dell'applicazione.
 * Rappresenta il punto di ingresso per gli utenti, fornendo un'introduzione visiva
 * e un pulsante per iniziare a utilizzare l'app.
 *
 * @param onContinueAsGuest Azione da eseguire quando l'utente preme il pulsante per continuare.
 */
@Composable
fun WelcomeScreen(
    onContinueAsGuest: () -> Unit
) {
    // Utilizza un componente di sfondo personalizzato con un'immagine e un velo scuro
    ScreenBackground(imageRes = R.drawable.bg_welcome) {
        // Scaffold con colore trasparente per permettere allo sfondo di essere visibile
        Scaffold(containerColor = Color.Transparent) { pad ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(pad)
                    .padding(24.dp)
            ) {
                // Contenuto centrale: Titolo, sottotitolo e pulsante d'azione
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "MeteoRadar",
                        color = Color.White,
                        style = MaterialTheme.typography.displayLarge
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        "Meteo nella tua posizione e \n" +
                                " in qualsiasi città che ami ☀ ",
                        color = Color.White
                    )
                    
                    Spacer(Modifier.height(32.dp))

                    // Pulsante per navigare verso la mappa principale
                    Button(
                        onClick = onContinueAsGuest,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Vediamo che tempo fa 😊")
                    }
                }

                // Testo del copyright posizionato in basso al centro
                Text(
                    "©${Calendar.getInstance().get(Calendar.YEAR)} MastersCorp",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                )
            }
        }
    }
}
