package com.core.meteoradar.data.repository

import com.core.meteoradar.data.local.db.PinDao
import com.core.meteoradar.data.local.db.toDomain
import com.core.meteoradar.data.local.db.toEntity
import com.core.meteoradar.domain.model.Pin
import com.core.meteoradar.domain.repository.PinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementazione concreta di [PinRepository].
 *
 * Questa classe è l'unica fonte di verità (Single Source of Truth) per i dati relativi ai pin.
 * Gestisce la logica di accesso ai dati, interagendo con il DAO ([PinDao]) per recuperare
 * e salvare i dati dal database locale.
 *
 * È un singleton perché non abbiamo bisogno di più istanze del repository nell'app.
 * Hilt si occupa di creare e fornire l'unica istanza.
 *
 * @param pinDao Il Data Access Object per l'entità Pin, iniettato da Hilt.
 */
@Singleton
class PinRepositoryImpl @Inject constructor(
    private val pinDao: PinDao
) : PinRepository {

    /**
     * Osserva tutti i pin salvati nel database e li restituisce come un [Flow] di liste di [Pin].
     *
     * Quando i dati nel database cambiano, il Flow emette automaticamente la nuova lista.
     * Utilizza l'operatore `map` di Flow per trasformare la lista di [PinEntity] (dal DB)
     * in una lista di [Pin] (modello di dominio), che è quello che l'app si aspetta.
     *
     * @return Un [Flow] che emette la lista aggiornata di tutti i pin.
     */
    override fun observePins(): Flow<List<Pin>> =
        pinDao.observeAll().map { list -> list.map { it.toDomain() } }

    /**
     * Inserisce un nuovo pin o aggiorna un pin esistente nel database.
     *
     * Prima di passarlo al DAO, converte l'oggetto [Pin] (modello di dominio)
     * in un [PinEntity] (entità del database).
     *
     * @param pin Il pin da salvare o aggiornare.
     * @return L'ID del pin inserito o aggiornato.
     */
    override suspend fun upsert(pin: Pin): Long =
        pinDao.upsert(pin.toEntity())

    /**
     * Elimina un pin dal database usando il suo ID.
     *
     * @param id L'ID del pin da eliminare.
     */
    override suspend fun deleteById(id: Long) {
        pinDao.deleteById(id)
    }

    /**
     * Recupera un singolo pin dal database usando il suo ID.
     *
     * Se il pin viene trovato nel database, l'oggetto [PinEntity] viene convertito
     * in un oggetto [Pin] (modello di dominio) prima di essere restituito.
     *
     * @param id L'ID del pin da cercare.
     * @return Il [Pin] corrispondente se trovato, altrimenti `null`.
     */
    override suspend fun getById(id: Long): Pin? =
        pinDao.getById(id)?.toDomain()
}
