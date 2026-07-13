package com.example.eatmate.data.remote

import com.example.eatmate.data.remote.dto.ChatRequest
import com.example.eatmate.data.remote.dto.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface QwenApiService {

    @POST("compatible-mode/v1/chat/completions")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
