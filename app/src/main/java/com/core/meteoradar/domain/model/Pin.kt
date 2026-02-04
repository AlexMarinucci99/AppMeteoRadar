package com.core.meteoradar.domain.model

/**
 * Rappresenta un "pin" o un segnaposto salvato dall'utente sulla mappa.
 *
 * Questa data class è un modello di dominio, il che significa che rappresenta un oggetto
 * fondamentale della logica di business dell'applicazione, indipendente da come i dati
 * vengono salvati (database) o visualizzati (UI).
 *
 * @property id L'identificatore univoco del pin. Il valore predefinito è 0, che di solito
 *              indica un nuovo pin non ancora salvato nel database (Room assegnerà un ID).
 * @property title Un titolo o nome per il pin, fornito dall'utente.
 * @property lat La coordinata di latitudine del pin.
 * @property lon La coordinata di longitudine del pin.
 * @property note Una nota o descrizione testuale opzionale associata al pin.
 */
data class Pin(
    val id: Long = 0,
    val title: String,
    val lat: Double,
    val lon: Double,
    val note: String? = null
)
