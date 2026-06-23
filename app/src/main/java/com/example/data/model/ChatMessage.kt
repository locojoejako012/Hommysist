package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val servicio_id: Int, // Associated with specific service request
    val sender_id: String, // ID (email) of the sender
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
