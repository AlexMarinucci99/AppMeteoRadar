package com.core.meteoradar.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Gestisce la persistenza dei dati di sessione dell'utente utilizzando Jetpack DataStore.
 *
 * Questa classe fornisce un modo per salvare e recuperare dati semplici chiave-valore, come le preferenze
 * dell'utente e lo stato della UI, in modo asincrono e sicuro.
 */

// Estensione per creare un'istanza di DataStore a livello di applicazione, legata al contesto.
// Il nome "session_prefs" identifica in modo univoco questo DataStore.
private val Context.sessionDataStore by preferencesDataStore("session_prefs")

/**
 * Data class per rappresentare lo stato della camera sulla mappa.
 * Contiene latitudine, longitudine e livello di zoom.
 */
data class CameraPrefs(
    val lat: Double,
    val lon: Double,
    val zoom: Float
)

/**
 * Classe che incapsula la logica per interagire con il DataStore della sessione.
 * Viene iniettata da Hilt dove necessario.
 *
 * @param context Il contesto dell'applicazione, fornito da Hilt.
 */
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Istanza di Gson per la serializzazione/deserializzazione di oggetti complessi in JSON.
    private val gson = Gson()

    /**
     * Oggetto privato per contenere tutte le chiavi utilizzate nel DataStore.
     * Questo aiuta a prevenire errori di battitura e a mantenere le chiavi centralizzate.
     */
    private object Keys {
        val CAMERA_LAT = doublePreferencesKey("camera_lat")
        val CAMERA_LON = doublePreferencesKey("camera_lon")
        val CAMERA_ZOOM = floatPreferencesKey("camera_zoom")

        val SATELLITE = booleanPreferencesKey("satellite")

    }

    // --------- CAMERA ---------

    /**
     * Un [Flow] che emette le preferenze della camera salvate ogni volta che cambiano.
     * Se non ci sono preferenze salvate o se manca uno dei valori, emette `null`.
     */
    val cameraFlow: Flow<CameraPrefs?> =
        context.sessionDataStore.data.map { prefs ->
            val lat = prefs[Keys.CAMERA_LAT]
            val lon = prefs[Keys.CAMERA_LON]
            val zoom = prefs[Keys.CAMERA_ZOOM]
            if (lat != null && lon != null && zoom != null) {
                CameraPrefs(lat, lon, zoom)
            } else {
                null
            }
        }

    /**
     * Salva lo stato della camera (latitudine, longitudine, zoom) nel DataStore.
     * È una suspend function perché `edit` è un'operazione asincrona.
     */
    suspend fun saveCamera(lat: Double, lon: Double, zoom: Float) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.CAMERA_LAT] = lat
            prefs[Keys.CAMERA_LON] = lon
            prefs[Keys.CAMERA_ZOOM] = zoom
        }
    }

    // --------- SATELLITE / TEMA MAPPA ---------

    /**
     * Un [Flow] che emette lo stato della modalità satellite (attivata/disattivata).
     * Se il valore non è impostato, il valore predefinito è `false`.
     */
    val satelliteFlow: Flow<Boolean> =
        context.sessionDataStore.data.map { it[Keys.SATELLITE] ?: false }

    /**
     * Imposta lo stato della modalità satellite nel DataStore.
     */
    suspend fun setSatellite(enabled: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.SATELLITE] = enabled
        }
    }

    // --------- INDICE PIN PER EMAIL ---------

    /**
     * Data Transfer Object (DTO) interno utilizzato per serializzare una mappa di pin in formato JSON.
     * Questo permette di salvare una struttura dati complessa come una singola stringa nel DataStore.
     * La mappa associa un'email a un insieme di ID di pin.
     */
    private data class PinsIndex(
        val map: Map<String, Set<Long>> = emptyMap()
    )

    /**
     * Decodifica una stringa JSON in una mappa di pin.
     * Gestisce il caso in cui il JSON sia nullo, vuoto o malformato, restituendo una mappa vuota.
     */
    private fun decodeIndex(json: String?): Map<String, Set<Long>> =
        if (json.isNullOrBlank()) {
            emptyMap()
        } else {
            runCatching {
                gson.fromJson(json, PinsIndex::class.java)?.map ?: emptyMap()
            }.getOrDefault(emptyMap())
        }

    /**
     * Codifica una mappa di pin in una stringa JSON.
     */
    private fun encodeIndex(map: Map<String, Set<Long>>): String =
        gson.toJson(PinsIndex(map))

}
