package com.example.eatmate.domain.repository

import com.example.eatmate.data.local.entity.ChatMessageEntity

interface ChatRepository {
    suspend fun getAllMessages(): List<ChatMessageEntity>
    suspend fun getRecentMessages(count: Int = 30): List<ChatMessageEntity>
    suspend fun getMessageCount(): Int
    suspend fun saveMessage(message: ChatMessageEntity): Long
    suspend fun updateMessage(id: Long, foodDataJson: String)
    suspend fun sendMessage(userText: String?, imageBytes: ByteArray? = null, existingUserMsgId: Long = 0): ChatMessageEntity
}
