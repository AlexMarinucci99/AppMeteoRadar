package com.core.meteoradar.di

import android.content.Context
import androidx.room.Room
import com.core.meteoradar.data.local.db.AppDatabase
import com.core.meteoradar.data.local.db.PinDao
import com.core.meteoradar.data.local.db.WeatherCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Modulo Dagger-Hilt per la fornitura di dipendenze relative al database.
 *
 * Questo modulo è installato in `SingletonComponent`, il che significa che tutte le dipendenze
 * fornite qui (il database e i suoi DAO) avranno un'unica istanza per l'intera durata
 * dell'applicazione (singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Fornisce un'istanza singleton di [AppDatabase].
     *
     * @param context Il contesto dell'applicazione, fornito da Hilt.
     * @return L'istanza del database Room.
     */
    @Provides @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "meteoradar.db")
            // In caso di migrazione di versione senza uno schema di migrazione definito,
            // il database verrà ricreato da zero. Utile in sviluppo, ma da usare con
            // cautela in produzione per non perdere i dati degli utenti.
            .fallbackToDestructiveMigration()
            .build()

    /**
     * Fornisce un'istanza di [PinDao].
     * Hilt sa come creare [AppDatabase] dal metodo qui sopra, quindi lo passa
     * automaticamente come parametro.
     * Non è annotato come @Singleton perché i DAO sono leggeri e il loro ciclo di vita
     * è gestito dall'istanza singleton del database.
     *
     * @param db L'istanza del database.
     * @return Un'istanza di PinDao.
     */
    @Provides fun providePinDao(db: AppDatabase): PinDao = db.pinDao()

    /**
     * Fornisce un'istanza di [WeatherCacheDao].
     *
     * @param db L'istanza del database.
     * @return Un'istanza di WeatherCacheDao.
     */
    @Provides fun provideWeatherCacheDao(db: AppDatabase): WeatherCacheDao = db.weatherCacheDao()
}
