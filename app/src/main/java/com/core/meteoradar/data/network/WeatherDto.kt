package com.core.meteoradar.data.network

import com.google.gson.annotations.SerializedName
import com.core.meteoradar.domain.model.Weather

/**
 * Data Transfer Object (DTO) che rappresenta la risposta completa dall'API Open-Meteo.
 * Corrisponde alla struttura JSON di primo livello ricevuta dalla rete.
 *
 * @param latitude La latitudine della località per cui sono stati richiesti i dati.
 * @param longitude La longitudine della località per cui sono stati richiesti i dati.
 * @param current Un oggetto [CurrentWeatherDto] che contiene i dati meteo attuali.
 *                È annotato con @SerializedName per mappare il campo JSON "current_weather"
 *                alla proprietà "current" di Kotlin. Può essere nullo se l'API non restituisce
 *                i dati meteo correnti.
 */
data class WeatherResponseDto(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("current_weather") val current: CurrentWeatherDto?
)

/**
 * Data Transfer Object (DTO) per i dati meteo attuali.
 * Corrisponde all'oggetto JSON "current_weather".
 *
 * @param temperature La temperatura attuale in gradi Celsius.
 * @param windSpeed La velocità del vento in km/h. @SerializedName mappa "windspeed" a "windSpeed".
 * @param windDirection La direzione del vento in gradi. @SerializedName mappa "winddirection" a "windDirection".
 * @param isDay Un intero che indica se è giorno (1) o notte (0). @SerializedName mappa "is_day" a "isDay".
 * @param time La data e l'ora in formato ISO 8601 a cui si riferiscono le misurazioni.
 * @param weatherCode Un codice numerico che rappresenta le condizioni meteo (es. sereno, pioggia, neve).
 *                    @SerializedName mappa "weathercode" a "weatherCode". È nullo se non disponibile.
 */
data class CurrentWeatherDto(
    val temperature: Double,
    @SerializedName("windspeed") val windSpeed: Double,
    @SerializedName("winddirection") val windDirection: Double,
    @SerializedName("is_day") val isDay: Int,
    val time: String,
    // Open-Meteo: codice meteo
    @SerializedName("weathercode") val weatherCode: Int? = null
)

/**
 * Funzione di estensione per convertire un oggetto [WeatherResponseDto] (DTO di rete)
 * in un oggetto [Weather] (modello di dominio).
 *
 * Questo è un pattern importante per separare i modelli di dati della rete da quelli
 * utilizzati nel resto dell'applicazione (UI, logica di business).
 * Permette all'app di essere indipendente da cambiamenti specifici nell'API.
 *
 * @return Un oggetto [Weather] se i dati meteo attuali (`current`) non sono nulli,
 *         altrimenti restituisce `null`.
 */
fun WeatherResponseDto.toDomain(): Weather? = current?.let {
    Weather(
        temperatureC = it.temperature,
        windSpeed = it.windSpeed,
        windDirection = it.windDirection,
        isDay = it.isDay == 1,      // Converte l'intero (1/0) in un booleano
        timeIso = it.time,
        conditionCode = it.weatherCode      // Passa il codice della condizione al modello di dominio
    )
}
