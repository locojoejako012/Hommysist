package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servicios")
data class ServiceRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cliente_id: String, // foreign key reference to User ID (email)
    val proveedor_id: String? = null, // nullable reference to User ID
    val categoria_id: Int,
    val subcategoria_id: Int,
    val descripcion: String,
    val direccion: String,
    val fecha_servicio: String, // formatted as date-time/string
    val estado: String = "Pendiente", // "Pendiente", "Aceptado", "En Progreso", "Finalizado", "Cancelado"
    val created_at: Long = System.currentTimeMillis(),
    val rating: Int? = null,
    val comentario: String? = null
)
