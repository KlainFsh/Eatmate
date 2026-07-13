package com.example.eatmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val role: String,          // "user" or "enen"
    val content: String,       // natural language text
    val imagePath: String? = null,    // if user sent a photo
    val foodDataJson: String? = null  // structured nutrition data for this message
)
