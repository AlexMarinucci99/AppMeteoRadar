@file:OptIn(ExperimentalMaterial3Api::class)

package com.core.meteoradar.feature.map

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.core.meteoradar.feature.pins.PinEditorSheet
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import com.core.meteoradar.util.weatherDescIt
import com.core.meteoradar.util.weatherEmoji
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Schermata principale della Mappa.
 * Gestisce la visualizzazione della mappa di Google, i marker dei pin salvati,
 * le informazioni meteo in tempo reale e la ricerca di località.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onOpenPins: () -> Unit,      // Callback per aprire la lista dei pin
    onLogout: () -> Unit,        // Callback per il logout
    initialFocusLat: Double? = null, // Latitudine iniziale (opzionale)
    initialFocusLon: Double? = null, // Longitudine iniziale (opzionale)
    vm: MapViewModel = hiltViewModel()
) {
    // Stato della UI dal ViewModel
    val ui by vm.ui.collectAsState()
    
    // Stati locali per la gestione dei dialog e dei sheet
    var showEditor by remember { mutableStateOf(false) }
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Stato per la ricerca
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // ---- GESTIONE PERMESSI DI LOCALIZZAZIONE ----
    val locPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val permissionStatus = locPermission.status
    val hasLocationPermission = permissionStatus is PermissionStatus.Granted

    // Richiede il permesso all'avvio
    LaunchedEffect(Unit) {
        locPermission.launchPermissionRequest()
    }

    // Notifica il ViewModel del cambio di permessi e ottiene posizione/meteo se concesso
    LaunchedEffect(hasLocationPermission) {
        vm.setPermission(hasLocationPermission)
        if (hasLocationPermission) {
            vm.obtainCurrentLocationAndWeather()
        }
    }

    // Ripristina l'ultima posizione della camera se non ci sono coordinate iniziali
    LaunchedEffect(Unit) {
        if (initialFocusLat == null || initialFocusLon == null) {
            vm.restoreLastCamera()
        }
    }

    // Si sposta sulle coordinate iniziali se fornite (es. da navigazione)
    LaunchedEffect(initialFocusLat, initialFocusLon) {
        if (initialFocusLat != null && initialFocusLon != null) {
            vm.focusOn(initialFocusLat, initialFocusLon, fetch = true)
        }
    }

    // Stato della camera della mappa (Google Maps)
    val camState = rememberCameraPositionState()

    // Centra la mappa quando cambiano le coordinate correnti nel ViewModel
    LaunchedEffect(ui.currentLat, ui.currentLon) {
        val lat = ui.currentLat
        val lon = ui.currentLon
        if (lat != null && lon != null) {
            camState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(lat, lon),
                    12f
                )
            )
        }
    }

    // Salva automaticamente la posizione della camera quando l'utente smette di muoverla
    LaunchedEffect(camState.isMoving) {
        if (!camState.isMoving) {
            val target = camState.position.target
            vm.saveCamera(target.latitude, target.longitude, camState.position.zoom)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Pulsante per switch Satellite/Mappa normale
                        TextButton(onClick = { vm.toggleSatellite() }) {
                            Text(if (ui.satellite) "Satellite" else "Maps")
                        }
                        // Pulsante per ricalibrare posizione e meteo
                        TextButton(onClick = { vm.obtainCurrentLocationAndWeather() }) {
                            Text("Calibra")
                        }
                        // Pulsante di ricerca
                        TextButton(onClick = { showSearch = true }) {
                            Text("🔍Cerca")
                        }
                        // Pulsante per aprire i Pin Salvati
                        TextButton(onClick = onOpenPins) {
                            Text("Salvati")
                        }
                        // Pulsante Logout
                        TextButton(onClick = onLogout) {
                            Text("Esci")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snack) },
        floatingActionButton = {
            // Pulsante (+) per aggiungere un nuovo pin nel punto centrale della mappa
            Box(Modifier.fillMaxSize()) {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 88.dp),
                    onClick = {
                        val target = camState.position.target
                        vm.focusOn(target.latitude, target.longitude, fetch = false)
                        showEditor = true
                    }
                ) {
                    Text("+")
                }
            }
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {

            // ---------- COMPONENTE MAPPA ----------
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = camState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission,
                    mapType = if (ui.satellite) MapType.SATELLITE else MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = false
                ),
                onMapClick = { vm.onMapClick(it.latitude, it.longitude) },
                onMapLongClick = {
                    vm.onMapClick(it.latitude, it.longitude)
                    showEditor = true
                }
            ) {
                // Disegna tutti i pin salvati come marker sulla mappa
                ui.pins.forEach { pin ->
                    val markerState = remember(pin.id) {
                        MarkerState(position = LatLng(pin.lat, pin.lon))
                    }

                    LaunchedEffect(pin.lat, pin.lon) {
                        markerState.position = LatLng(pin.lat, pin.lon)
                    }

                    Marker(
                        state = markerState,
                        title = pin.title
                    )
                }
            }

            // ---------- OVERLAY INFORMAZIONI METEO ----------
            when {
                ui.weather != null -> {
                    val w = ui.weather!!

                    val formattedTime = remember(w.timeIso) {
                        // Ripristiniamo l'orario del dispositivo (telefono) per garantire la sincronizzazione
                        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                        sdf.format(Date())
                    }

                    // Card informativa con temperatura, vento e descrizione
                    ElevatedCard(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Riga 1: Condizione Meteo (Emoji + Testo) allineata a sinistra
                            w.conditionCode?.let { code ->
                                Text(
                                    text = "${weatherEmoji(code)} ${weatherDescIt(code)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }

                            // Riga 2: Statistiche (Temperatura, Vento, Orario)
                            Text(
                                text = buildString {
                                    append("🌡 ${"%.1f".format(w.temperatureC)}°C  |  ")
                                    append("💨 ${"%.0f".format(w.windSpeed)} km/h  |  ")
                                    append("🕒 $formattedTime")
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            // Riga 3: Località
                            ui.placeName?.let {
                                Text(
                                    text = "📍 $it",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp).align(Alignment.Start)
                                )
                            }
                        }
                    }
                }

                ui.loading -> ElevatedCard(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text("Caricamento meteo…", Modifier.padding(12.dp))
                }

                ui.error != null -> ElevatedCard(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "ℹ️ ${ui.error}",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // ---------- MESSAGGIO RATIONALE PERMESSI ----------
            val showRationale =
                permissionStatus is PermissionStatus.Denied &&
                        permissionStatus.shouldShowRationale

            if (!hasLocationPermission && showRationale) {
                ElevatedCard(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Per mostrare la tua posizione attiva il permesso di localizzazione.")
                        TextButton(onClick = { locPermission.launchPermissionRequest() }) {
                            Text("Concedi")
                        }
                    }
                }
            }
        }
    }

    // ---------- BOTTOM SHEET PER LA CREAZIONE/EDIT DEI PIN ----------
    PinEditorSheet(
        visible = showEditor,
        onDismiss = { showEditor = false },
        onSave = { title, note ->
            scope.launch {
                vm.saveCurrentLocationPin(title, note)
                snack.showSnackbar("Pin salvato")
            }
        }
    )

    // ---------- DIALOG DI RICERCA LOCALITÀ ----------
    if (showSearch) {
        AlertDialog(
            onDismissRequest = { showSearch = false },
            title = { Text("Cerca luogo") },
            text = {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    placeholder = { Text("Città, indirizzo…") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.searchAndFocus(searchQuery)
                        showSearch = false
                    }
                ) { Text("Cerca") }
            },
            dismissButton = {
                TextButton(onClick = { showSearch = false }) {
                    Text("Annulla")
                }
            }
        )
    }
}
