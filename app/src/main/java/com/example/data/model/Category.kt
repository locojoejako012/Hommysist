package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Category(
    @PrimaryKey val id: Int,
    val nombre: String
)
