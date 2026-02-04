package com.core.meteoradar.domain.model

/**
 * Rappresenta i dati meteo per una specifica località in un dato momento.
 *
 * Questa data class è un modello di dominio, il che significa che rappresenta un oggetto
 * fondamentale della logica di business dell'applicazione, pulito da dettagli di
 * implementazione della rete o del database.
 *
 * @property temperatureC La temperatura attuale in gradi Celsius.
 * @property windSpeed La velocità del vento, solitamente in km/h.
 * @property windDirection La direzione del vento in gradi (0-360).
 * @property isDay `true` se è giorno, `false` se è notte.
 * @property timeIso La stringa ISO 8601 che rappresenta la data e l'ora della misurazione.
 * @property conditionCode Un codice numerico (weathercode da Open-Meteo) che rappresenta
 *                         le condizioni meteo attuali (es. sereno, pioggia, neve).
 *                         È utilizzato per scegliere l'emoji e il testo descrittivo appropriati.
 *                         È nullo se non disponibile.
 */
data class Weather(
    val temperatureC: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val isDay: Boolean,
    val timeIso: String,
    val conditionCode: Int? = null
)
