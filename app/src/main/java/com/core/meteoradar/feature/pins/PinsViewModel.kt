package com.core.meteoradar.feature.pins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.meteoradar.domain.model.Pin
import com.core.meteoradar.domain.repository.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Rappresenta lo stato della UI per la gestione dei Pin salvati.
 * 
 * @property pins La lista completa dei pin recuperati dal database.
 * @property editing Il pin attualmente in fase di modifica (null se non si sta modificando nulla).
 */
data class PinsUiState(
    val pins: List<Pin> = emptyList(),
    val editing: Pin? = null
)

/**
 * ViewModel incaricato di gestire la logica dei pin salvati dall'utente.
 * Permette la visualizzazione, l'aggiunta di test, l'eliminazione e la modifica.
 */
@HiltViewModel
class PinsViewModel @Inject constructor(
    private val repo: PinRepository
) : ViewModel() {

    // Stato interno per tenere traccia del pin che l'utente ha scelto di modificare
    private val _editing = MutableStateFlow<Pin?>(null)

    /**
     * Flusso di stato della UI che combina la lista dei pin dal repository
     * con lo stato di editing corrente.
     */
    val ui: StateFlow<PinsUiState> = combine(
        repo.observePins(), _editing
    ) { pins, editing -> PinsUiState(pins, editing) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PinsUiState()
        )

    /**
     * Funzione di utilità per aggiungere un pin di prova (dummy) a Milano.
     */
    fun addDummyPin() = viewModelScope.launch {
        repo.upsert(Pin(title = "Segnaposto", lat = 45.4642, lon = 9.19, note = "Test"))
    }

    /**
     * Elimina un pin dal database tramite il suo ID univoco.
     */
    fun delete(id: Long) = viewModelScope.launch { 
        repo.deleteById(id) 
    }

    /**
     * Avvia la procedura di modifica per un pin specifico.
     */
    fun startEdit(pin: Pin) { 
        _editing.value = pin 
    }

    /**
     * Annulla l'operazione di modifica corrente.
     */
    fun stopEdit() { 
        _editing.value = null 
    }

    /**
     * Salva le modifiche apportate al pin attualmente in fase di editing.
     * 
     * @param title Nuovo titolo per il pin.
     * @param note Nuova nota opzionale per il pin.
     */
    fun saveEdit(title: String, note: String?) = viewModelScope.launch {
        val cur = _editing.value ?: return@launch
        // Aggiorna il pin nel database con i nuovi dati mantenendo le stesse coordinate e ID
        repo.upsert(cur.copy(title = title, note = note))
        // Chiude la modalità editing
        _editing.value = null
    }
}
