package com.core.meteoradar.data.repository

import com.core.meteoradar.data.local.db.WeatherCacheDao
import com.core.meteoradar.data.local.db.WeatherCacheEntity
import com.core.meteoradar.data.network.WeatherRemoteDataSource
import com.core.meteoradar.data.network.toDomain
import com.core.meteoradar.di.IoDispatcher
import com.core.meteoradar.domain.model.Weather
import com.core.meteoradar.domain.repository.WeatherRepository
import com.core.meteoradar.util.AppResult
import com.core.meteoradar.util.locationKey
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 *  Il tempo di vita (Time To Live) della cache in millisecondi.
 *  I dati nella cache più vecchi di questo valore verranno considerati scaduti.
 *  Attualmente impostato a 10 minuti.
 */
private const val TTL_MS = 10 * 60 * 1000 // 10 minuti

/**
 * Implementazione concreta di [WeatherRepository].
 *
 * Questa classe è la fonte di verità per i dati meteo. Gestisce la logica per
 * recuperare i dati meteo, combinando una strategia di cache locale con chiamate
 * a una fonte di dati remota.
 *
 * È un singleton per garantire che ci sia una sola istanza del repository nell'app.
 *
 * @param remote La fonte di dati remota per ottenere i dati meteo freschi.
 * @param cacheDao Il DAO per interagire con la cache del database locale.
 * @param gson Libreria per serializzare/deserializzare oggetti Weather in/da JSON per la cache.
 * @param io Il CoroutineDispatcher per eseguire le operazioni di I/O (rete, database)
 *           fuori dal thread principale.
 */
@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val remote: WeatherRemoteDataSource,
    private val cacheDao: WeatherCacheDao,
    private val gson: Gson,
    @IoDispatcher private val io: CoroutineDispatcher
) : WeatherRepository {

    /**
     * Ottiene le condizioni meteo attuali per una data coordinata geografica.
     *
     * La logica è la seguente:
     * 1. Se `useCache` è true, controlla prima la cache locale.
     * 2. Se esiste una voce nella cache per la posizione data e non è scaduta (è più recente di TTL_MS),
     *    deserializza i dati e li restituisce.
     * 3. Se la cache è vuota, scaduta o `useCache` è false, effettua una chiamata di rete.
     * 4. Converte la risposta della rete (DTO) in un modello di dominio ([Weather]).
     * 5. Salva il nuovo modello di dominio in cache come JSON per usi futuri.
     * 6. Restituisce i dati freschi.
     *
     * Tutte le operazioni sono avvolte in un blocco `try-catch` per gestire eventuali errori
     * di rete o di database e restituirli come [AppResult.Error].
     *
     * @param lat Latitudine.
     * @param lon Longitudine.
     * @param useCache Se `true`, tenta di usare la cache prima di fare una chiamata di rete.
     * @return Un [AppResult] che contiene [Weather] in caso di successo o un [Throwable] in caso di errore.
     */
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        useCache: Boolean
    ): AppResult<Weather> = withContext(io) { // Esegue tutto nel dispatcher I/O
        try {
            val key = locationKey(lat, lon)
            val now = System.currentTimeMillis()

            // -------- CACHE LOCALE --------
            if (useCache) {
                cacheDao.get(key)?.let { cached ->
                    // Controlla se la cache è ancora valida (non scaduta)
                    if (now - cached.updatedAt <= TTL_MS) {
                        // Deserializza il JSON dalla cache nel modello di dominio
                        gson.fromJson(cached.lastJson, Weather::class.java)?.let {
                            return@withContext AppResult.Success(it) // Restituisce i dati dalla cache
                        }
                    }
                }
            }

            // -------- CHIAMATA REMOTA --------
            // Se la cache non può essere usata, si procede con la chiamata di rete.
            val dto = remote.getCurrent(lat, lon)

            // Converte il Data Transfer Object (DTO) in un modello di dominio.
            val weather = dto.toDomain()
                ?: return@withContext AppResult.Error( // Gestisce il caso in cui la risposta non contenga dati meteo attuali
                    IllegalStateException("No current weather")
                )

            // Salva il risultato fresco nella cache per le prossime richieste.
            cacheDao.upsert(
                WeatherCacheEntity(
                    locationKey = key,
                    lastJson = gson.toJson(weather), // Serializza il modello di dominio in JSON
                    updatedAt = now
                )
            )
            // Restituisce il risultato ottenuto dalla rete.
            AppResult.Success(weather)
        } catch (t: Throwable) {
            // Se si verifica un'eccezione (es. problemi di rete), la cattura e la restituisce.
            AppResult.Error(t)
        }
    }
}
