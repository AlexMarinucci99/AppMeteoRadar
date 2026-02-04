package com.core.meteoradar.di

import com.core.meteoradar.data.network.WeatherApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Modulo Dagger-Hilt per la fornitura di dipendenze di rete.
 *
 * Questo modulo è installato in `SingletonComponent`, il che significa che tutte le dipendenze
 * fornite qui avranno un'unica istanza per l'intera durata dell'applicazione (singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Fornisce un'istanza singleton di [Gson].
     * Gson è una libreria Java che può essere utilizzata per convertire oggetti Java in JSON e viceversa.
     */
    @Provides @Singleton fun provideGson(): Gson = GsonBuilder().create()

    /**
     * Fornisce un'istanza singleton di [OkHttpClient].
     * OkHttp è un client HTTP per Android e Java.
     *
     * Viene aggiunto un [HttpLoggingInterceptor] per registrare le richieste e le risposte di rete,
     * il che è utile per il debug. Il livello di log è impostato su `BASIC`.
     */
    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()

    /**
     * Fornisce un'istanza singleton di [Retrofit].
     * Retrofit trasforma la tua API HTTP in un'interfaccia Java.
     *
     * @param gson L'istanza [Gson] per la serializzazione e deserializzazione JSON.
     * @param client L'istanza [OkHttpClient] da utilizzare per le richieste di rete.
     */
    @Provides @Singleton
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    /**
     * Fornisce un'istanza singleton di [WeatherApiService].
     * Questa è l'implementazione concreta dell'interfaccia della nostra API, creata da Retrofit.
     *
     * @param retrofit L'istanza [Retrofit] configurata.
     */
    @Provides @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)
}
