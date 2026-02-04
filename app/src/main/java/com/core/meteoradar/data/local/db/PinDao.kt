package com.core.meteoradar.data.local.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) per l'entità [PinEntity].
 *
 * Questa interfaccia definisce i metodi per interagire con la tabella "pins" nel database.
 * Room genererà l'implementazione concreta di questa interfaccia.
 * L'annotazione [Dao] la identifica come una classe DAO per Room.
 */
@Dao
interface PinDao {

    /**
     * Osserva e restituisce un [Flow] con l'elenco di tutti i pin presenti nel database.
     * La lista è ordinata in base all'ID in ordine decrescente (dal più recente al meno recente).
     * Essendo un [Flow], emetterà automaticamente una nuova lista ogni volta che i dati nella
     * tabella "pins" cambiano, permettendo alla UI di aggiornarsi in modo reattivo.
     *
     * @return Un Flow che emette una lista di [PinEntity].
     */
    @Query("SELECT * FROM pins ORDER BY id DESC")
    fun observeAll(): Flow<List<PinEntity>>

    /**
     * Inserisce un nuovo pin o aggiorna un pin esistente nel database.
     * L'annotazione [Insert] definisce questa funzione come un'operazione di inserimento.
     * La strategia di conflitto `OnConflictStrategy.REPLACE` significa che se si tenta di
     * inserire un pin con un ID che esiste già, il vecchio pin verrà sostituito con quello nuovo.
     *
     * È una `suspend fun` perché le operazioni sul database devono essere eseguite
     * fuori dal thread principale (UI thread).
     *
     * @param entity L'entità [PinEntity] da inserire o aggiornare.
     * @return L'ID del pin appena inserito o aggiornato.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PinEntity): Long

    /**
     * Elimina un pin dal database in base al suo ID.
     *
     * @param id L'ID del pin da eliminare.
     */
    @Query("DELETE FROM pins WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * Recupera un singolo pin dal database in base al suo ID.
     *
     * @param id L'ID del pin da recuperare.
     * @return L'entità [PinEntity] corrispondente se trovata, altrimenti `null`.
     */
    @Query("SELECT * FROM pins WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PinEntity?
}
