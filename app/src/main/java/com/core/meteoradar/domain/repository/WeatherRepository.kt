package com.core.meteoradar.domain.repository

import com.core.meteoradar.domain.model.Weather
import com.core.meteoradar.util.AppResult

/**
 * Interfaccia che definisce il contratto per un repository che gestisce le operazioni sui dati meteo.
 *
 * Questa interfaccia astrae la fonte dei dati (rete con cache, ecc.) dal resto dell'applicazione.
 * Le classi che utilizzano questo repository (come i ViewModel) dipenderanno da questa interfaccia,
 * non dalla sua implementazione concreta.
 */
interface WeatherRepository {
    /**
     * Recupera le condizioni meteo attuali per una data coordinata geografica.
     *
     * @param lat La latitudine della località.
     * @param lon La longitudine della località.
     * @param useCache Un booleano che indica se tentare di leggere prima dalla cache locale.
     *                 Il valore predefinito è `true`.
     * @return Un [AppResult] che incapsula il risultato dell'operazione:
     *         - [AppResult.Success] con i dati [Weather] in caso di successo.
     *         - [AppResult.Error] con l'[Throwable] in caso di fallimento.
     */
    suspend fun getCurrentWeather(lat: Double, lon: Double, useCache: Boolean = true): AppResult<Weather>
}
