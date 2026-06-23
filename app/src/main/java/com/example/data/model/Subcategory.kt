package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "subcategorias",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoria_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Subcategory(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria_id: Int
)
