package com.core.meteoradar

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * La classe Application personalizzata per l'applicazione MeteoRadar.
 *
 * Questa classe funge da punto di ingresso globale per l'app e viene inizializzata
 * prima di qualsiasi Activity, Service o altro componente.
 *
 * L'annotazione `@HiltAndroidApp` è fondamentale per Dagger-Hilt.
 * Indica a Hilt di generare il codice necessario per l'iniezione delle dipendenze
 * a livello di applicazione. Questo crea il contenitore delle dipendenze a cui
 * l'intera app potrà accedere.
 */
@HiltAndroidApp
class MeteoRadarApp : Application()
