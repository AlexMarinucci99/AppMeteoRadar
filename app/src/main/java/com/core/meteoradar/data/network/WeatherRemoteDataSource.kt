package com.core.meteoradar.data.network

import javax.inject.Inject

/**
 * Classe che funge da fonte di dati remota per le informazioni meteo.
 *
 * Questa classe è responsabile di effettuare le chiamate di rete tramite [WeatherApiService]
 * per recuperare i dati meteo dall'API.
 * Funziona come un intermediario tra il repository e il servizio di rete (Retrofit).
 *
 * L'annotazione `@Inject constructor` indica a Dagger-Hilt come fornire un'istanza
 * di questa classe, iniettando automaticamente la dipendenza [WeatherApiService].
 *
 * @param api L'istanza di [WeatherApiService] generata da Retrofit, utilizzata per
 *            eseguire le chiamate API effettive.
 */
class WeatherRemoteDataSource @Inject constructor(
    private val api: WeatherApiService
) {
    /**
     * Recupera i dati meteo attuali da una specifica coordinata geografica.
     *
     * È una `suspend fun` perché delega la chiamata a `api.getCurrentWeather`, che è
     * anch'essa una funzione di sospensione per le operazioni di rete asincrone.
     *
     * @param lat La latitudine della località.
     * @param lon La longitudine della località.
     * @return Un oggetto [WeatherResponseDto] che contiene i dati grezzi ricevuti dalla rete.
     */
    suspend fun getCurrent(lat: Double, lon: Double): WeatherResponseDto =
        api.getCurrentWeather(lat, lon)
}
