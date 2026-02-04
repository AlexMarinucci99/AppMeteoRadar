package com.core.meteoradar.domain.repository

import com.core.meteoradar.domain.model.Pin
import kotlinx.coroutines.flow.Flow

/**
 * Interfaccia che definisce il contratto per un repository che gestisce le operazioni sui dati dei Pin.
 *
 * Questa interfaccia astrae la fonte dei dati (database, rete, ecc.) dal resto dell'applicazione.
 * Le classi che utilizzano questo repository (come i ViewModel) dipenderanno da questa interfaccia,
 * non dalla sua implementazione concreta, seguendo il principio di inversione delle dipendenze.
 */
interface PinRepository {
    /**
     * Avvia l'osservazione di tutti i pin salvati.
     *
     * @return Un [Flow] che emette una lista di [Pin] ogni volta che i dati sottostanti cambiano.
     *         Questo permette alla UI di reagire in tempo reale alle modifiche nel database.
     */
    fun observePins(): Flow<List<Pin>>

    /**
     * Inserisce un nuovo pin o ne aggiorna uno esistente se l'ID è già presente.
     *
     * @param pin Il [Pin] da salvare o aggiornare.
     * @return L'ID del pin appena inserito o aggiornato.
     */
    suspend fun upsert(pin: Pin): Long

    /**
     * Elimina un pin specifico tramite il suo ID.
     *
     * @param id L'ID del pin da eliminare.
     */
    suspend fun deleteById(id: Long)

    /**
     * Recupera un singolo pin tramite il suo ID.
     *
     * @param id L'ID del pin da cercare.
     * @return Il [Pin] corrispondente se trovato, altrimenti `null`.
     */
    suspend fun getById(id: Long): Pin?
}
