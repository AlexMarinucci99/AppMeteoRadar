package com.core.meteoradar.util

/**
 * Mapping WMO (Open-Meteo) -> emoji/descrizione IT.
 * Riferimento codici (ridotto):
 * 0=sereno, 1=preval. sereno, 2=parz. nuvoloso, 3=nuvoloso,
 * 45/48=nebbia, 51..57=pioviggine, 61..67=pioggia,
 * 71..77=neve, 80..82=rovesci, 85..86=rovesci di neve,
 * 95=temporale, 96..99=temporale con grandine
 */

fun weatherEmoji(code: Int?): String = when (code) {
    null -> "⛅" // fallback neutro

    0 -> "☀️"
    1 -> "🌤️"
    2 -> "⛅"
    3 -> "☁️"

    45, 48 -> "🌫️"

    in 51..57 -> "🌦️" // pioviggine
    in 61..67 -> "🌧️" // pioggia
    in 71..77 -> "🌨️" // neve

    in 80..82 -> "🌧️" // rovesci
    in 85..86 -> "🌨️" // rovesci di neve

    95 -> "⛈️"
    in 96..99 -> "🌩️"

    else -> "⛅"
}

fun weatherDescIt(code: Int?): String = when (code) {
    null -> "Condizione non disponibile"

    0 -> "Sereno"
    1 -> "Prevalentemente sereno"
    2 -> "Parzialmente nuvoloso"
    3 -> "Nuvoloso"

    45, 48 -> "Nebbia"

    51 -> "Pioviggine debole"
    53 -> "Pioviggine"
    55 -> "Pioviggine intensa"
    56 -> "Pioviggine gelata"
    57 -> "Pioviggine gelata intensa"

    61 -> "Pioggia debole"
    63 -> "Pioggia moderata"
    65 -> "Pioggia intensa"
    66 -> "Pioggia gelata"
    67 -> "Pioggia gelata intensa"

    71 -> "Neve debole"
    73 -> "Neve moderata"
    75 -> "Neve intensa"
    77 -> "Granelli di neve"

    80 -> "Rovesci deboli"
    81 -> "Rovesci"
    82 -> "Rovesci intensi"

    85 -> "Rovesci di neve deboli"
    86 -> "Rovesci di neve intensi"

    95 -> "Temporale"
    96 -> "Temporale con grandine"
    99 -> "Temporale forte con grandine"

    else -> "Condizione sconosciuta"
}

/** Comodo se vuoi stampare “🌧️ Pioggia moderata” in un colpo solo. */
fun weatherLabel(code: Int?): String = "${weatherEmoji(code)} ${weatherDescIt(code)}"
