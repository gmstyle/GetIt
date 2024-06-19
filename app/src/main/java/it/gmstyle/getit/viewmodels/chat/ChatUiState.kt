package it.gmstyle.getit.viewmodels.chat

import it.gmstyle.getit.data.models.ChatMessage

sealed class ChatUiState(val chatHistory: List<ChatMessage>) {
    data class Initial(val _chatHistory: List<ChatMessage> = emptyList()) : ChatUiState(chatHistory = _chatHistory)
    data class Loading(val _chatHistory: List<ChatMessage>) : ChatUiState(chatHistory = _chatHistory)
    data class Success(val _chatHistory: List<ChatMessage>) : ChatUiState(chatHistory = _chatHistory)
    data class Error(val _chatHistory: List<ChatMessage>) : ChatUiState(chatHistory = _chatHistory)
}