@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.core.meteoradar.feature.pins

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.core.meteoradar.R
import com.core.meteoradar.domain.model.Pin
import com.core.meteoradar.domain.model.Weather
import com.core.meteoradar.feature.map.MapViewModel
import com.core.meteoradar.util.weatherDescIt
import com.core.meteoradar.util.weatherEmoji

/**
 * Schermata che visualizza un elenco di tutti i segnaposto (pin) salvati dall'utente.
 *
 * Questa Composable gestisce:
 * - La visualizzazione di un elenco scorrevole di pin.
 * - Il caricamento e la visualizzazione delle condizioni meteo per ogni pin.
 * - Una modalità di selezione per consentire all'utente di eliminare più pin contemporaneamente.
 * - La possibilità di rinominare un singolo pin.
 * - La navigazione per tornare alla schermata precedente.
 *
 * @param vm Il [MapViewModel] condiviso, che fornisce l'accesso ai dati dei pin e alle azioni correlate.
 * @param onBack Callback per tornare indietro nella navigazione (solitamente alla mappa).
 * @param onSelectPin Callback (attualmente non utilizzata) che veniva usata per navigare alla mappa
 *                    e centrarla su un pin selezionato.
 */
@Composable
fun PinsListScreen(
    vm: MapViewModel,
    onBack: () -> Unit,
    onSelectPin: (Double, Double) -> Unit // Non più usato, ma mantenuto per compatibilità
) {
    // ---- STATO DELLA UI ----
    val ui by vm.ui.collectAsState() // Osserva lo stato della UI dal ViewModel
    val pins = ui.pins // La lista dei pin da visualizzare

    // ---- STATI LOCALI PER LA GESTIONE DELLA SCHERMATA ----

    // Stato per la modalità di selezione/eliminazione
    var selectionMode by remember { mutableStateOf(false) }
    val selectedPins = remember { mutableStateListOf<Pin>() } // Lista dei pin attualmente selezionati

    // Stato per la gestione della rinomina di un pin
    var pinToRename by remember { mutableStateOf<Pin?>(null) } // Il pin da rinominare (se presente, mostra il dialog)
    var renameText by remember { mutableStateOf("") } // Il nuovo testo del titolo nel dialog di rinomina

    // Stato per il dialog di conferma eliminazione
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // ---- LAYOUT PRINCIPALE ----
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posizioni salvate") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Indietro") }
                },
                actions = {
                    // Mostra il pulsante "Elimina" solo se ci sono pin salvati
                    if (pins.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                if (!selectionMode) {
                                    // Se non siamo in modalità selezione, entriamo
                                    selectionMode = true
                                    selectedPins.clear()
                                } else {
                                    // Se siamo già in modalità selezione...
                                    if (selectedPins.isNotEmpty()) {
                                        // ... e ci sono pin selezionati, mostriamo il dialog di conferma
                                        showDeleteConfirm = true
                                    } else {
                                        // ... ma non ci sono pin selezionati, usciamo dalla modalità
                                        selectionMode = false
                                    }
                                }
                            }
                        ) {
                            // Il testo del pulsante cambia in base al contesto
                            Text(
                                when {
                                    !selectionMode -> "Elimina" // Stato normale
                                    selectedPins.isEmpty() -> "Annulla" // In selezione, ma nessun elemento scelto
                                    else -> "Elimina (${selectedPins.size})" // In selezione, con elementi scelti
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            // Immagine di sfondo con opacità ridotta
            Image(
                painter = painterResource(id = R.drawable.bg_pins),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.25f
            )

            // ---- GESTIONE LISTA VUOTA O PIENA ----
            if (pins.isEmpty()) {
                // Messaggio centrale se non ci sono pin
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nessun pin salvato")
                }
            } else {
                // Elenco scorrevole dei pin
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(pins, key = { it.id }) { pin ->
                        val isSelected = selectedPins.contains(pin)

                        // Stato locale per il meteo di questo specifico pin
                        var weather by remember(pin.id) { mutableStateOf<Weather?>(null) }

                        // Carica i dati meteo per il pin solo una volta quando appare sullo schermo
                        LaunchedEffect(pin.id) {
                            vm.loadWeatherForPin(pin) { w -> weather = w }
                        }

                        // ---- CARD DEL SINGOLO PIN ----
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    if (selectionMode) {
                                        // In modalità selezione, il click aggiunge/rimuove il pin dalla selezione
                                        if (isSelected) selectedPins.remove(pin)
                                        else selectedPins.add(pin)
                                    } else {
                                        // Click normale: nessuna azione (in passato apriva la mappa)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mostra il Checkbox solo in modalità selezione
                                if (selectionMode) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedPins.add(pin)
                                            else selectedPins.remove(pin)
                                        }
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }

                                // Colonna con titolo e meteo del pin
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pin.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(4.dp))

                                    // Sezione meteo
                                    if (weather != null) {
                                        val w = weather!!
                                        val condLabel = w.conditionCode?.let {
                                            "${weatherEmoji(it)} ${weatherDescIt(it)}"
                                        }
                                        Text(
                                            text = buildString {
                                                append("🌡 ${"%.1f".format(w.temperatureC)}°C")
                                                if (condLabel != null) {
                                                    append("  •  $condLabel")
                                                }
                                            },
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    } else {
                                        // Messaggio di caricamento per il meteo
                                        Text(
                                            "Meteo in caricamento…",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                // Pulsante "Modifica", visibile solo in modalità normale
                                if (!selectionMode) {
                                    TextButton(
                                        onClick = {
                                            pinToRename = pin // Imposta il pin da modificare
                                            renameText = pin.title // Pre-compila il campo di testo con il titolo attuale
                                        }
                                    ) {
                                        Text("Modifica")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ---- DIALOG DI RINOMINA ----
    val editingPin = pinToRename
    if (editingPin != null) {
        AlertDialog(
            onDismissRequest = { pinToRename = null }, // Chiude il dialog
            title = { Text("Rinomina pin") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newTitle = renameText.trim()
                        if (newTitle.isNotEmpty()) {
                            vm.renamePin(editingPin, newTitle) // Salva il nuovo nome
                        }
                        pinToRename = null // Chiude il dialog
                    }
                ) { Text("Salva") }
            },
            dismissButton = {
                TextButton(onClick = { pinToRename = null }) { Text("Annulla") }
            }
        )
    }

    // ---- DIALOG DI CONFERMA ELIMINAZIONE ----
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminare definitivamente le posizioni salvate?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.deletePins(selectedPins.toList()) // Chiama il ViewModel per eliminare
                        selectedPins.clear() // Svuota la selezione
                        selectionMode = false // Esce dalla modalità selezione
                        showDeleteConfirm = false // Chiude il dialog
                    }
                ) { Text("Sì") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("No") }
            }
        )
    }
}
