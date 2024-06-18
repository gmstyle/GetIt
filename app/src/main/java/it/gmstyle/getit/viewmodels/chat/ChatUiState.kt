package it.gmstyle.getit.viewmodels.chat

sealed class ChatUiState {
    data object Initial : ChatUiState()
    data object Loading : ChatUiState()
    data class Success(val outputText: String) : ChatUiState()
    data class Error(val errorMessage: String) : ChatUiState()
}