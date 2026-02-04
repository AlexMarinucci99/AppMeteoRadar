package com.core.meteoradar.navigation

/**
 * Un oggetto singleton che contiene le costanti per le rotte di navigazione
 * utilizzate nell'applicazione con Jetpack Navigation Compose.
 *
 * Centralizzare le stringhe delle rotte in un unico posto aiuta a evitare errori di battitura
 * e a rendere il codice di navigazione più leggibile e manutenibile.
 */
object Routes {
    /**
     * Rotta per la schermata di benvenuto (WelcomeScreen).
     * È la schermata iniziale dell'applicazione.
     */
    const val WELCOME = "welcome"

    /**
     * Rotta per la schermata principale della mappa (MapScreen).
     * Questa rotta supporta anche parametri opzionali per latitudine e longitudine.
     */
    const val MAP = "map"

    /**
     * Rotta per la schermata che elenca i pin salvati dall'utente (PinsListScreen).
     */
    const val PINS = "pins"

    /**
     * Funzione di utility per costruire la rotta per la mappa includendo le coordinate.
     * Questo crea una stringa come "map?lat=45.123&lon=9.456", che può essere usata
     * per navigare verso la mappa e farla centrare su una posizione specifica.
     *
     * @param lat La latitudine su cui centrare la mappa.
     * @param lon La longitudine su cui centrare la mappa.
     * @return La stringa completa della rotta con i parametri.
     */
    fun mapWith(lat: Double, lon: Double): String =
        "$MAP?lat=$lat&lon=$lon"
}
