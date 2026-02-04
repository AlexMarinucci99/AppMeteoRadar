package com.core.meteoradar.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Modulo Dagger-Hilt per la fornitura di dipendenze relative alla geolocalizzazione.
 *
 * Questo modulo è installato in `SingletonComponent`, il che significa che la dipendenza
 * fornita qui avrà un'unica istanza per l'intera durata dell'applicazione (singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    /**
     * Fornisce un'istanza singleton di [FusedLocationProviderClient].
     *
     * Il [FusedLocationProviderClient] è il principale punto di accesso per interagire
     * con il provider di posizione fuso di Google Play Services, che combina in modo intelligente
     * i segnali GPS, Wi-Fi e della rete cellulare per fornire la migliore stima della posizione.
     *
     * @param context Il contesto dell'applicazione, fornito da Hilt.
     * @return Un'istanza di FusedLocationProviderClient.
     */
    @Provides @Singleton
    fun provideFusedLocation(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}
