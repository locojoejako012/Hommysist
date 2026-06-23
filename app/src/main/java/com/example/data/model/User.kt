package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String, // email or generated UUID
    val nombre: String,
    val telefono: String,
    val email: String,
    val rol: String, // "Cliente", "Proveedor", "Administrador"
    val passwordHash: String, // Simulating credentials locally
    val verificado: Boolean = false,
    val disponible: Boolean = true,
    val cedula: String? = null,
    val fotoRostro: String? = null,
    val rechazado: Boolean = false,
    val motivoRechazo: String? = null,
    val created_at: Long = System.currentTimeMillis()
)
