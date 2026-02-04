package com.core.meteoradar.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.core.meteoradar.domain.model.Pin

@Entity(tableName = "pins")
data class PinEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val lat: Double,
    val lon: Double,
    val note: String?
)

fun PinEntity.toDomain() = Pin(id, title, lat, lon, note)
fun Pin.toEntity() = PinEntity(id, title, lat, lon, note)
