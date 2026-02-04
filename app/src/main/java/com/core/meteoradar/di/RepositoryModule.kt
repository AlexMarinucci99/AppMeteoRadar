package com.core.meteoradar.di

import com.core.meteoradar.data.repository.PinRepositoryImpl
import com.core.meteoradar.data.repository.WeatherRepositoryImpl
import com.core.meteoradar.domain.repository.PinRepository
import com.core.meteoradar.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Modulo Dagger-Hilt per la fornitura delle implementazioni dei repository.
 *
 * Questo modulo è responsabile di "legare" (bind) le interfacce dei repository
 * alle loro implementazioni concrete. In questo modo, quando una classe richiede
 * un'interfaccia (es. `WeatherRepository`), Hilt sa che deve fornire un'istanza
 * della sua implementazione (es. `WeatherRepositoryImpl`).
 *
 * L'uso di `@Binds` è più performante di `@Provides` quando non è necessaria
 * alcuna logica per creare l'istanza, ma solo per specificare l'implementazione.
 *
 * Il modulo è una `abstract class` perché contiene solo metodi astratti annotati con `@Binds`.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Lega l'interfaccia [WeatherRepository] alla sua implementazione [WeatherRepositoryImpl].
     *
     * L'annotazione `@Singleton` assicura che venga creata una sola istanza di
     * [WeatherRepositoryImpl] e che questa venga riutilizzata in tutta l'applicazione.
     *
     * @param impl L'implementazione concreta del repository. Hilt sa come costruirla
     *             perché è annotata con `@Inject constructor`.
     * @return L'interfaccia del repository che le altre classi useranno.
     */
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    /**
     * Lega l'interfaccia [PinRepository] alla sua implementazione [PinRepositoryImpl].
     *
     * @param impl L'implementazione concreta del repository.
     * @return L'interfaccia del repository.
     */
    @Binds
    @Singleton
    abstract fun bindPinRepository(impl: PinRepositoryImpl): PinRepository
}
