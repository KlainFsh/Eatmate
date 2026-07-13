package com.example.eatmate.data.remote

import com.example.eatmate.data.remote.dto.ChatResponse
import com.example.eatmate.data.remote.dto.TextChatRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface QwenChatService {
    @POST("compatible-mode/v1/chat/completions")
    suspend fun chat(@Body request: TextChatRequest): ChatResponse
}
