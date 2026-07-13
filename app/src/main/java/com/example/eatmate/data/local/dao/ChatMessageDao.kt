package com.example.eatmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.eatmate.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM chat_message ORDER BY timestamp ASC")
    fun observeAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_message ORDER BY timestamp DESC LIMIT :n")
    suspend fun getRecentMessages(n: Int = 30): List<ChatMessageEntity>

    @Query("SELECT COUNT(*) FROM chat_message")
    suspend fun getMessageCount(): Int

    @Insert
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("UPDATE chat_message SET foodDataJson = :foodDataJson WHERE id = :id")
    suspend fun updateFoodDataJson(id: Long, foodDataJson: String)

    @Query("DELETE FROM chat_message WHERE id = :id")
    suspend fun deleteMessage(id: Long)

    @Query("DELETE FROM chat_message")
    suspend fun clearAll()
}
