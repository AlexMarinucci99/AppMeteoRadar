package com.core.meteoradar.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.core.meteoradar.util.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Fornisce l'accesso alla posizione corrente del dispositivo.
 *
 * Questa classe astrae la logica per ottenere la posizione utilizzando il [FusedLocationProviderClient],
 * gestendo i permessi e fornendo un meccanismo di fallback.
 * È un singleton, quindi ne esisterà una sola istanza nell'applicazione.
 *
 * @param context Il contesto dell'applicazione, fornito da Hilt.
 * @param fused Il client per interagire con il provider di posizione fuso di Google.
 */
@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fused: FusedLocationProviderClient
) {

    /**
     * Recupera in modo asincrono la posizione corrente del dispositivo.
     *
     * La strategia di recupero è la seguente:
     * 1. Controlla se l'app ha i permessi di localizzazione. In caso contrario, restituisce `null`.
     * 2. Tenta di ottenere la posizione corrente con alta precisione (`PRIORITY_HIGH_ACCURACY`).
     *    Questa operazione può richiedere alcuni secondi per attivare il GPS.
     * 3. Se il primo tentativo fallisce o non restituisce una posizione, fa un fallback
     *    richiedendo l'ultima posizione nota (`lastLocation`). Quest'ultima è più veloce
     *    da ottenere ma potrebbe essere obsoleta o meno precisa.
     *
     * L'annotazione `@SuppressLint("MissingPermission")` è usata perché il controllo dei permessi
     * viene eseguito manualmente all'inizio del metodo con `hasLocationPermission(context)`,
     * ma l'analizzatore statico di Lint non è in grado di rilevarlo e segnalerebbe un falso errore.
     *
     * @return Un oggetto [Location] se la posizione viene trovata, altrimenti `null`.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrent(): Location? {
        // Se non abbiamo il permesso di accedere alla posizione, interrompiamo subito l'operazione.
        if (!hasLocationPermission(context)) {
            return null
        }

        // 1) Tentativo di ottenere la posizione corrente con alta precisione.
        // Utilizziamo `suspendCancellableCoroutine` per trasformare l'API basata su callback di Google
        // in una funzione di sospensione che si integra con le coroutine di Kotlin.
        val current = suspendCancellableCoroutine<Location?> { continuation ->
            // CancellationTokenSource permette di annullare la richiesta se la coroutine viene cancellata.
            val cts = CancellationTokenSource()

            fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    // Se la coroutine è ancora attiva, riprendila con la posizione trovata.
                    if (!continuation.isCompleted) continuation.resume(location)
                }
                .addOnFailureListener {
                    // In caso di fallimento, riprendi con null.
                    if (!continuation.isCompleted) continuation.resume(null)
                }
                .addOnCanceledListener {
                    // In caso di cancellazione esterna, riprendi con null.
                    if (!continuation.isCompleted) continuation.resume(null)
                }

            // Se la coroutine che ha chiamato questa funzione viene cancellata,
            // propaghiamo la cancellazione alla richiesta di posizione.
            continuation.invokeOnCancellation { cts.cancel() }
        }

        // Se abbiamo ottenuto una posizione valida, la restituiamo subito.
        if (current != null) return current

        // 2) Fallback: se non abbiamo ottenuto la posizione corrente, proviamo con l'ultima nota.
        // Anche qui, usiamo `suspendCancellableCoroutine` per avvolgere l'API asincrona.
        return suspendCancellableCoroutine { continuation ->
            fused.lastLocation
                .addOnSuccessListener { location ->
                    if (!continuation.isCompleted) continuation.resume(location)
                }
                .addOnFailureListener {
                    // In caso di fallimento, l'operazione complessiva restituirà null.
                    if (!continuation.isCompleted) continuation.resume(null)
                }
        }
    }
}
