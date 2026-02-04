package com.core.meteoradar.data.network

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaccia per definire gli endpoint dell'API meteo utilizzando Retrofit.
 *
 * Retrofit utilizza questa interfaccia per generare l'implementazione concreta
 * che effettuerà le chiamate di rete.
 */
interface WeatherApiService {
    /**
     * Recupera i dati meteo attuali per una data coordinata geografica.
     * Questa funzione effettua una chiamata GET all'endpoint "v1/forecast" dell'API Open-Meteo.
     *
     * È una `suspend fun` perché viene eseguita all'interno di una coroutine.
     *
     * @param lat La latitudine per la quale richiedere il meteo.
     * @param lon La longitudine per la quale richiedere il meteo.
     * @param currentWeather Un booleano per indicare se includere i dati meteo attuali nella risposta.
     *                     Il valore predefinito è `true`.
     * @param timezone Impostato su "auto" per ottenere i dati basati sul fuso orario locale delle coordinate.
     * @return Un oggetto [WeatherResponseDto] che contiene i dati della risposta deserializzati.
     */
    // Open-Meteo non richiede una chiave API.
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponseDto
}
