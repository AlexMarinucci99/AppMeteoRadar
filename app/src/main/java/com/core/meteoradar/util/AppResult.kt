package com.core.meteoradar.util

/**
 * Rappresenta il risultato di un'operazione che può trovarsi in uno di tre stati:
 * Successo (con dati), Errore (con un'eccezione) o Caricamento.
 *
 * Questa sealed interface è utile per gestire lo stato della UI in modo reattivo
 * e sicuro, specialmente quando si caricano dati dalla rete o dal database.
 *
 * @param T Il tipo di dati restituiti in caso di successo.
 */
sealed interface AppResult<out T> {
    /**
     * Rappresenta un'operazione completata con successo.
     * @param data I dati risultanti dall'operazione.
     */
    data class Success<T>(val data: T) : AppResult<T>

    /**
     * Rappresenta un fallimento dell'operazione.
     * @param throwable L'eccezione o l'errore riscontrato durante l'operazione.
     */
    data class Error(val throwable: Throwable) : AppResult<Nothing>

    /**
     * Rappresenta lo stato di caricamento in corso.
     */
    data object Loading : AppResult<Nothing>
}
