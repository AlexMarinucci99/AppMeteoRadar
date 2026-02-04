package com.core.meteoradar.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualificatore personalizzato per l'iniezione del CoroutineDispatcher per l'I/O.
 *
 * Utilizzato per distinguere il dispatcher I/O da altri tipi di dispatcher
 * quando vengono iniettati tramite Hilt.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * Modulo Dagger-Hilt che fornisce dipendenze a livello di applicazione (singleton).
 *
 * Questo modulo è installato in `SingletonComponent`, il che significa che tutte le dipendenze
 * fornite qui avranno un'unica istanza per l'intera durata dell'applicazione.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Fornisce un'istanza singleton di `DataStore<Preferences>`.
     *
     * DataStore è una soluzione di archiviazione dati che consente di salvare coppie chiave-valore.
     * Questa factory crea il DataStore con il nome "session_prefs", che verrà usato
     * per la persistenza delle preferenze di sessione.
     *
     * @param context Il contesto dell'applicazione, fornito da Hilt.
     * @return Un'istanza di DataStore<Preferences>.
     */
    @Provides @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("session_prefs") }
        )

    /**
     * Fornisce il `CoroutineDispatcher` per le operazioni di I/O (Input/Output).
     *
     * L'annotazione `@IoDispatcher` è un qualificatore personalizzato che permette a Hilt
     * di distinguere questo dispatcher da altri (come `Dispatchers.Main` o `Dispatchers.Default`).
     *
     * Questo dispatcher è ottimizzato per operazioni che bloccano il thread, come operazioni
     * di rete (chiamate API) o di accesso al disco (lettura/scrittura del database),
     * garantendo che queste operazioni non vengano eseguite sul thread principale (UI).
     *
     * @return Un'istanza di CoroutineDispatcher che corrisponde a `Dispatchers.IO`.
     */
    @Provides @Singleton @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
