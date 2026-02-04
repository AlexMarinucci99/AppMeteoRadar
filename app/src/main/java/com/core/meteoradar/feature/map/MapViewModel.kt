@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.core.meteoradar.feature.map

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.meteoradar.data.local.datastore.SessionDataStore
import com.core.meteoradar.data.location.LocationProvider
import com.core.meteoradar.domain.model.Pin
import com.core.meteoradar.domain.model.Weather
import com.core.meteoradar.domain.repository.PinRepository
import com.core.meteoradar.domain.repository.WeatherRepository
import com.core.meteoradar.util.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

/**
 * Stato della UI per la schermata della Mappa.
 */
data class MapUiState(
    val hasLocationPermission: Boolean = false, // Se l'utente ha concesso i permessi GPS
    val currentLat: Double? = null,             // Latitudine attualmente visualizzata o selezionata
    val currentLon: Double? = null,             // Longitudine attualmente visualizzata o selezionata
    val weather: Weather? = null,                // Dati meteo per la posizione corrente
    val pins: List<Pin> = emptyList(),          // Lista dei pin salvati nel database
    val satellite: Boolean = false,             // Se la mappa è in modalità satellite
    val loading: Boolean = false,               // Stato di caricamento (es. durante il fetch del meteo)
    val error: String? = null,                  // Messaggio di errore eventuale
    val placeName: String? = null               // Nome del luogo ottenuto tramite reverse geocoding
)

/**
 * ViewModel che gestisce la logica della mappa, del meteo e dei pin.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val weatherRepo: WeatherRepository,
    private val pinRepo: PinRepository,
    private val locationProvider: LocationProvider,
    private val session: SessionDataStore,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _ui = MutableStateFlow(MapUiState())
    val ui: StateFlow<MapUiState> = _ui

    init {
        // 🔁 Osserva costantemente i pin dal database e aggiorna la UI
        viewModelScope.launch {
            pinRepo.observePins().collect { pinsFromDb ->
                _ui.update { it.copy(pins = pinsFromDb) }
            }
        }

        // 🛰️ Osserva la preferenza dell'utente per la modalità satellite
        viewModelScope.launch {
            session.satelliteFlow.collect { sat ->
                _ui.update { it.copy(satellite = sat) }
            }
        }
    }

    // ---- GESTIONE POSIZIONE E METEO ----

    /** Aggiorna lo stato dei permessi nel ViewModel. */
    fun setPermission(granted: Boolean) =
        _ui.update { it.copy(hasLocationPermission = granted) }

    /** Ripristina l'ultima posizione della camera salvata nelle preferenze. */
    fun restoreLastCamera() = viewModelScope.launch {
        session.cameraFlow.firstOrNull()?.let { cam ->
            _ui.update {
                it.copy(
                    currentLat = cam.lat,
                    currentLon = cam.lon
                )
            }
        }
    }

    /** Tenta di ottenere la posizione GPS attuale e scarica il meteo corrispondente. */
    fun obtainCurrentLocationAndWeather() = viewModelScope.launch {
        if (!_ui.value.hasLocationPermission) return@launch
        _ui.update { it.copy(loading = true, error = null) }

        val loc = locationProvider.getCurrent()
        if (loc == null) {
            _ui.update {
                it.copy(
                    loading = false,
                    error = "Posizione non disponibile. Controlla il GPS e riprova."
                )
            }
            return@launch
        }
        focusOn(loc.latitude, loc.longitude, fetch = true)
    }

    /** Aggiorna i dati meteo per le coordinate attualmente selezionate. */
    fun refreshWeather() = viewModelScope.launch {
        val lat = _ui.value.currentLat ?: return@launch
        val lon = _ui.value.currentLon ?: return@launch

        _ui.update { it.copy(loading = true, error = null) }

        when (val res = weatherRepo.getCurrentWeather(lat, lon, useCache = false)) {
            is AppResult.Success ->
                _ui.update { it.copy(weather = res.data, loading = false) }
            is AppResult.Error ->
                _ui.update { it.copy(error = res.throwable.message, loading = false) }
            else -> {}
        }
    }

    /** Gestisce il click sulla mappa: sposta il focus e aggiorna il meteo. */
    fun onMapClick(lat: Double, lon: Double) {
        focusOn(lat, lon, fetch = true)
    }

    /** Sposta la visuale su certe coordinate, esegue il reverse geocoding e opzionalmente aggiorna il meteo. */
    fun focusOn(lat: Double, lon: Double, fetch: Boolean = false) {
        viewModelScope.launch {
            val place = reverseGeocode(lat, lon)
            _ui.update {
                it.copy(
                    currentLat = lat,
                    currentLon = lon,
                    placeName = place
                )
            }
            if (fetch) refreshWeather()
        }
    }

    // ---- GESTIONE PIN ----

    /** Salva un nuovo pin nel database basandosi sulla posizione corrente della mappa. */
    fun saveCurrentLocationPin(title: String, note: String?) = viewModelScope.launch {
        val lat = _ui.value.currentLat ?: return@launch
        val lon = _ui.value.currentLon ?: return@launch
        val newPin = Pin(title = title, lat = lat, lon = lon, note = note)
        pinRepo.upsert(newPin)
    }

    /** Salva le impostazioni correnti della camera (posizione e zoom) nelle preferenze. */
    fun saveCamera(lat: Double, lon: Double, zoom: Float) = viewModelScope.launch {
        session.saveCamera(lat, lon, zoom)
    }

    /** Cambia la modalità di visualizzazione della mappa tra Normale e Satellite. */
    fun toggleSatellite() = viewModelScope.launch {
        session.setSatellite(!_ui.value.satellite)
    }

    /** Rinomina un pin esistente. */
    fun renamePin(pin: Pin, newTitle: String) = viewModelScope.launch {
        pinRepo.upsert(pin.copy(title = newTitle))
    }

    /** Elimina una lista di pin dal database. */
    fun deletePins(pins: List<Pin>) = viewModelScope.launch {
        pins.forEach { pin ->
            if (pin.id != 0L) {
                pinRepo.deleteById(pin.id)
            }
        }
    }

    /** Carica i dati meteo per un pin specifico (usato solitamente nella lista dei pin). */
    fun loadWeatherForPin(pin: Pin, onLoaded: (Weather?) -> Unit) {
        viewModelScope.launch {
            val result = weatherRepo.getCurrentWeather(
                lat = pin.lat,
                lon = pin.lon,
                useCache = true
            )
            val w = when (result) {
                is AppResult.Success -> result.data
                else -> null
            }
            onLoaded(w)
        }
    }

    // ---- RICERCA E GEOCODING ----

    /** Cerca un luogo tramite testo (Geocoding) e sposta la mappa su di esso. */
    fun searchAndFocus(query: String) = viewModelScope.launch {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@launch

        val geoResult = withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                Geocoder(appContext, Locale.getDefault())
                    .getFromLocationName(trimmed, 1)
                    ?.firstOrNull()
            } catch (_: Exception) {
                null
            }
        } ?: return@launch

        focusOn(geoResult.latitude, geoResult.longitude, fetch = true)
    }

    /** Converte le coordinate geografiche in un nome di località leggibile (Reverse Geocoding). */
    private suspend fun reverseGeocode(lat: Double, lon: Double): String? =
        withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val list = Geocoder(appContext, Locale.getDefault())
                    .getFromLocation(lat, lon, 1)
                val a = list?.firstOrNull() ?: return@withContext null
                listOfNotNull(a.locality, a.adminArea, a.countryName)
                    .distinct()
                    .joinToString(", ")
                    .ifBlank { null }
            } catch (_: Exception) {
                null
            }
        }
}
