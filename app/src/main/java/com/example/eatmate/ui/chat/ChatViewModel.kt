package com.example.eatmate.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatmate.data.local.EnenProfileManager
import com.example.eatmate.data.local.entity.ChatMessageEntity
import com.example.eatmate.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessageEntity> = emptyList(),
    val isEnenTyping: Boolean = false,
    val error: String? = null,
    val enenName: String = "恩恩"
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val enenProfile: EnenProfileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            val messages = chatRepository.getAllMessages()
            val name = enenProfile.getName()
            _uiState.update { it.copy(messages = messages, enenName = name) }
        }
    }

    fun sendText(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isEnenTyping = true, error = null) }
            try {
                val userMsg = ChatMessageEntity(role = "user", content = text)
                val msgId = chatRepository.saveMessage(userMsg)
                val current = chatRepository.getAllMessages()
                _uiState.update { it.copy(messages = current) }

                chatRepository.sendMessage(userText = text, existingUserMsgId = msgId)
                val messages = chatRepository.getAllMessages()
                _uiState.update { it.copy(messages = messages, isEnenTyping = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isEnenTyping = false, error = e.message) }
            }
        }
    }

    fun sendImage(imageBytes: ByteArray, imagePath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isEnenTyping = true, error = null) }
            try {
                val userMsg = ChatMessageEntity(
                    role = "user", content = "[图片]",
                    imagePath = imagePath
                )
                val msgId = chatRepository.saveMessage(userMsg)
                val current = chatRepository.getAllMessages()
                _uiState.update { it.copy(messages = current) }

                chatRepository.sendMessage(
                    userText = null, imageBytes = imageBytes,
                    existingUserMsgId = msgId
                )
                val messages = chatRepository.getAllMessages()
                _uiState.update { it.copy(messages = messages, isEnenTyping = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isEnenTyping = false, error = e.message) }
            }
        }
    }

    fun updateEnenName(name: String) {
        viewModelScope.launch {
            enenProfile.setName(name)
            _uiState.update { it.copy(enenName = name) }
        }
    }

    fun refreshMessages() {
        loadMessages()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(isEnenTyping = false, error = message) }
    }
}
