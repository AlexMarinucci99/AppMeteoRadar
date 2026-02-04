package com.core.meteoradar.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * La classe principale del database dell'applicazione, costruita con Room.
 *
 * Questa classe astratta estende [RoomDatabase] e funge da punto di accesso principale
 * per la connessione al database persistente dell'applicazione.
 *
 * L'annotazione [Database] definisce:
 * - `entities`: L'elenco di tutte le classi di entità (che rappresentano le tabelle)
 *   incluse in questo database. Qui sono [PinEntity] e [WeatherCacheEntity].
 * - `version`: Il numero di versione del database. Deve essere incrementato quando si
 *   modifica lo schema del database (ad es. aggiungendo/modificando tabelle o colonne).
 * - `exportSchema`: Se impostato su `false`, Room non esporterà lo schema del database in
 *   una cartella del progetto. L'esportazione dello schema è utile per tenere traccia
 *   della cronologia delle versioni dello schema nel controllo di versione, ma per progetti
 *   più piccoli può essere disabilitata.
 */
@Database(
    entities = [PinEntity::class, WeatherCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Fornisce l'accesso al Data Access Object (DAO) per le operazioni relative ai pin.
     *
     * @return Un'istanza di [PinDao].
     */
    abstract fun pinDao(): PinDao

    /**
     * Fornisce l'accesso al Data Access Object (DAO) per le operazioni relative alla cache meteo.
     *
     * @return Un'istanza di [WeatherCacheDao].
     */
    abstract fun weatherCacheDao(): WeatherCacheDao
}
