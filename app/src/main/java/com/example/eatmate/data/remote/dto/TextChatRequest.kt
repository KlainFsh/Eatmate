package com.example.eatmate.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Text-only message for chat API (qwen-plus, etc.)
 * Uses plain string content instead of ContentPart list.
 */
@Serializable
data class TextChatRequest(
    @kotlinx.serialization.SerialName("model")
    val model: String,
    @kotlinx.serialization.SerialName("messages")
    val messages: List<TextMessage>
)

@Serializable
data class TextMessage(
    val role: String,
    val content: String
)
