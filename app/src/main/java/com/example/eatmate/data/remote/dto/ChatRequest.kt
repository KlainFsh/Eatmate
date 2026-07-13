package com.example.eatmate.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Request
@Serializable
data class ChatRequest(
    val model: String = "qwen3.6-plus",
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: List<ContentPart>
)

@Serializable
data class ContentPart(
    val type: String,
    val text: String? = null,
    @SerialName("image_url")
    val imageUrl: ImageUrl? = null
)

@Serializable
data class ImageUrl(
    val url: String
)

// Response
@Serializable
data class ChatResponse(
    val choices: List<Choice> = emptyList()
)

@Serializable
data class Choice(
    val message: ResponseMessage
)

@Serializable
data class ResponseMessage(
    val content: String
)
