package com.core.meteoradar.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest

/**
 * Verifica se l'applicazione ha i permessi per accedere alla posizione dell'utente.
 * Controlla sia il permesso 'FINE' (preciso) che 'COARSE' (approssimativo).
 * 
 * @param context Il contesto dell'applicazione o dell'activity.
 * @return True se almeno uno dei due permessi è concesso, false altrimenti.
 */
fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}

/**
 * Genera una chiave stringa univoca basata sulle coordinate geografiche.
 * La latitudine e la longitudine vengono formattate a 4 cifre decimali.
 * Utile per l'identificazione di posizioni o come chiave per la cache.
 * 
 * @param lat Latitudine.
 * @param lon Longitudine.
 * @return Una stringa formattata "lat,lon".
 */
fun locationKey(lat: Double, lon: Double): String =
    "${"%.4f".format(lat)},${"%.4f".format(lon)}"
