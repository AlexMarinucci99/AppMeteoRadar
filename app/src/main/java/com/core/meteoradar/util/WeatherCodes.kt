package com.core.meteoradar.util

/**
 * Converte il codice meteorologico di Open-Meteo in un'emoji corrispondente.
 * 
 * @param code Il codice numerico che rappresenta la condizione meteorologica.
 * @return Una stringa contenente l'emoji rappresentativa.
 */
fun weatherEmoji(code: Int): String = when (code) {
    0 -> "☀️" // Cielo sereno
    1 -> "🌤" // Prevalentemente sereno
    2 -> "⛅️" // Parzialmente nuvoloso
    3 -> "☁️" // Nuvoloso
    45, 48 -> "🌫" // Nebbia
    51, 53, 55 -> "🌦" // Pioviggine
    56, 57 -> "🥶🌦" // Pioviggine gelata
    61, 63, 65 -> "🌧" // Pioggia
    66, 67 -> "🧊🌧" // Pioggia gelata
    71, 73, 75 -> "❄️" // Neve
    77 -> "❄️" // Nevischio
    80, 81, 82 -> "🌦" // Rovesci di pioggia
    85, 86 -> "❄️" // Rovesci di neve
    95 -> "⛈" // Temporale
    96, 99 -> "⛈🌨" // Temporale con grandine
    else -> "🌡" // Condizione sconosciuta
}

/**
 * Converte il codice meteorologico di Open-Meteo in una descrizione testuale in italiano.
 * 
 * @param code Il codice numerico che rappresenta la condizione meteorologica.
 * @return Una stringa con la descrizione della condizione in italiano.
 */
fun weatherDescIt(code: Int): String = when (code) {
    0 -> "Sereno"
    1 -> "Poco nuvoloso"
    2 -> "Parzialmente nuvoloso"
    3 -> "Nuvoloso"
    45, 48 -> "Nebbia"
    51, 53, 55 -> "Pioviggine"
    56, 57 -> "Pioviggine gelata"
    61 -> "Pioggia debole"
    63 -> "Pioggia moderata"
    65 -> "Pioggia intensa"
    66, 67 -> "Pioggia gelata"
    71 -> "Neve debole"
    73 -> "Neve moderata"
    75 -> "Neve intensa"
    77 -> "Nevischio"
    80 -> "Rovesci deboli"
    81 -> "Rovesci moderati"
    82 -> "Rovesci forti"
    85 -> "Rovesci di neve"
    86 -> "Rovesci di neve forti"
    95 -> "Temporale"
    96, 99 -> "Temporale con grandine"
    else -> "Condizione sconosciuta"
}
