package it.gmstyle.getit.viewmodels.chat

import it.gmstyle.getit.data.models.ChatMessage

sealed class ChatUiState {
    data object Initial : ChatUiState()
    data object Loading : ChatUiState()
    data class Success(val chatHistory: List<ChatMessage>) : ChatUiState()
    data class Error(val errorMessage: String) : ChatUiState()
}