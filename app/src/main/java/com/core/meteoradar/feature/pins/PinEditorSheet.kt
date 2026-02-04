/**
 * Questo file disabilita alcuni avvisi del compilatore per le API sperimentali utilizzate,
 * in particolare per il `ModalBottomSheet` di Material 3.
 */
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.core.meteoradar.feature.pins

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Un [ModalBottomSheet] riutilizzabile per creare un nuovo segnaposto o modificare uno esistente.
 *
 * La sua modalità di funzionamento (creazione vs. modifica) è determinata dalla presenza
 * del parametro `onDelete`. Se `onDelete` è `null`, il componente si comporta come un editor
 * per un nuovo pin. Se `onDelete` è fornito, mostra un pulsante "Elimina" e si comporta
 * come un editor per un pin esistente.
 *
 * @param visible `true` per mostrare il bottom sheet, `false` per nasconderlo.
 * @param onDismiss Callback invocata quando l'utente chiude il sheet (es. toccando fuori).
 * @param onSave Callback invocata quando si preme "Salva", fornendo il titolo e la nota inseriti.
 * @param initialTitle Il titolo da mostrare inizialmente. Utile per la modifica.
 * @param initialNote La nota da mostrare inizialmente. Utile per la modifica.
 * @param onDelete Una callback opzionale per l'eliminazione. Se non è `null`, viene mostrato
 *                 il pulsante "Elimina" e il titolo del sheet cambia in "Modifica segnaposto".
 */
@Composable
fun PinEditorSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSave: (title: String, note: String?) -> Unit,
    initialTitle: String = "Segnaposto",
    initialNote: String? = null,
    onDelete: (() -> Unit)? = null
) {
    // Se non è visibile, non disegniamo nulla per ottimizzare le performance.
    if (!visible) return

    // Stati interni per gestire il contenuto dei campi di testo.
    // `remember` con i parametri iniziali assicura che lo stato venga "resettato"
    // ogni volta che i valori iniziali cambiano (es. quando si apre per un pin diverso).
    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    var note by remember(initialNote) { mutableStateOf(initialNote.orEmpty()) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Il titolo del sheet cambia a seconda che si stia creando o modificando un pin.
            Text(if (onDelete == null) "Nuovo segnaposto" else "Modifica segnaposto")

            // Campo di testo per il titolo del pin.
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Titolo") }, modifier = Modifier.fillMaxWidth()
            )

            // Campo di testo per la nota opzionale.
            OutlinedTextField(
                value = note, onValueChange = { note = it },
                label = { Text("Nota (opzionale)") }, modifier = Modifier.fillMaxWidth()
            )

            // Pulsante Salva.
            Button(
                onClick = {
                    // Pulisce gli input e fornisce valori di default prima di salvare.
                    val finalTitle = title.trim().ifEmpty { "Segnaposto" }
                    val finalNote = note.trim().ifEmpty { null }
                    onSave(finalTitle, finalNote)
                    onDismiss() // Chiude il sheet dopo aver salvato.
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Salva") }

            // Il pulsante Elimina viene mostrato solo se la funzione onDelete è stata fornita.
            if (onDelete != null) {
                TextButton(
                    onClick = { 
                        onDelete()
                        onDismiss() // Chiude il sheet anche dopo l'eliminazione.
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Elimina") }
            }
        }
    }
}
