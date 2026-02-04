package com.core.meteoradar.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta una singola riga nella tabella "weather_cache" del database Room.
 *
 * Questa data class viene utilizzata per memorizzare nella cache locale le risposte JSON
 * dell'API meteo per una specifica località. Lo scopo è di ridurre il numero di
 * chiamate di rete e migliorare le prestazioni, mostrando dati precedentemente caricati
 * quando sono ancora validi.
 *
 * L'annotazione [Entity] la definisce come una tabella con il nome "weather_cache".
 */
@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    /**
     * La chiave primaria della tabella.
     * È una stringa che rappresenta in modo univoco una località, solitamente
     * combinando latitudine e longitudine (es. "45.4643,9.1900").
     */
    @PrimaryKey val locationKey: String, // es: "45.4643,9.1900"

    /**
     * La risposta JSON completa ricevuta dall'API meteo per questa località.
     * Memorizzare il JSON grezzo consente flessibilità nel caso in cui il modello
     * di dati dell'API cambi, e permette di deserializzare i dati quando necessario.
     */
    val lastJson: String,

    /**
     * Il timestamp (in millisecondi) di quando questa entry è stata salvata o aggiornata.
     * Questo valore è fondamentale per determinare se i dati nella cache sono "scaduti"
     * e se è necessario effettuare una nuova richiesta di rete.
     */
    val updatedAt: Long
)
