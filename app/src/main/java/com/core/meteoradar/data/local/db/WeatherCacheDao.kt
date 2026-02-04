package com.core.meteoradar.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) per l'entità [WeatherCacheEntity].
 *
 * Questa interfaccia definisce i metodi per interagire con la tabella "weather_cache" nel database.
 * Room genererà l'implementazione concreta di questa interfaccia.
 * L'annotazione [Dao] la identifica come una classe DAO per Room.
 */
@Dao
interface WeatherCacheDao {
    /**
     * Recupera una entry della cache meteo dal database in base alla sua chiave di localizzazione.
     *
     * @param key La chiave univoca che identifica la località (es. "lat,lon").
     * @return L'entità [WeatherCacheEntity] corrispondente se trovata, altrimenti `null`.
     */
    @Query("SELECT * FROM weather_cache WHERE locationKey = :key LIMIT 1")
    suspend fun get(key: String): WeatherCacheEntity?

    /**
     * Inserisce una nuova entry nella cache meteo o la aggiorna se esiste già.
     * La strategia di conflitto `OnConflictStrategy.REPLACE` significa che se si tenta di
     * inserire dati per una `locationKey` che esiste già, la vecchia entry verrà sostituita.
     *
     * È una `suspend fun` perché le operazioni sul database devono essere eseguite
     * fuori dal thread principale (UI thread).
     *
     * @param entity L'entità [WeatherCacheEntity] da inserire o aggiornare.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WeatherCacheEntity)
}
