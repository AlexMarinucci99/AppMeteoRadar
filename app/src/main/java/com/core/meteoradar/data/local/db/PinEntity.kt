package com.core.meteoradar.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.core.meteoradar.domain.model.Pin

/**
 * Rappresenta l'entità "Pin" (segnaposto) all'interno del database locale Room.
 * Ogni istanza di questa classe corrisponde a una riga nella tabella "pins".
 */
@Entity(tableName = "pins")
data class PinEntity(
    /**
     * Identificatore univoco del pin. Viene generato automaticamente dal database.
     */
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    /**
     * Titolo o nome assegnato al segnaposto.
     */
    val title: String,

    /**
     * Coordinata latitudine del segnaposto.
     */
    val lat: Double,
    
    /**
     * Coordinata longitudine del segnaposto.
     */
    val lon: Double,
    
    /**
     * Nota opzionale associata al segnaposto.
     */
    val note: String?
)

/**
 * Funzione di estensione per mappare l'oggetto del database (Entity) 
 * nel modello di dominio (Pin) utilizzato dalla logica di business.
 */
fun PinEntity.toDomain() = Pin(id, title, lat, lon, note)

/**
 * Funzione di estensione per mappare l'oggetto del modello di dominio (Pin)
 * nell'entità del database (PinEntity).
 */
fun Pin.toEntity() = PinEntity(id, title, lat, lon, note)
