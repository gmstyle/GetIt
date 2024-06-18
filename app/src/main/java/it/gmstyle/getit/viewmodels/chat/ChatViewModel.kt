package it.gmstyle.getit.viewmodels.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.gmstyle.getit.data.models.ChatMessage
import it.gmstyle.getit.data.repositories.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState.Initial)
    val uiState: StateFlow<ChatUiState> get()= _uiState.asStateFlow()
    private val _chatHistory = mutableListOf<ChatMessage>()

    fun sendMessage(chatPrompt: ChatMessage) {
        _chatHistory += chatPrompt

        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            try {
                val generativeResponse = chatRepository.sendMessage(chatPrompt.message)
                generativeResponse.text?.let { outputContent ->
                    val chatResponse = ChatMessage(
                        message = outputContent,
                        isUser = false
                    )
                    _chatHistory += chatResponse
                    _uiState.value = ChatUiState.Success(_chatHistory)
                } ?: run {
                    _uiState.value = ChatUiState.Error("No text generated")
                }
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    /*fun sendMessage(chatPrompt: ChatMessage) {
        _chatHistory.value += chatPrompt
        viewModelScope.launch {
            chatRepository.sendMessage(chatPrompt.message)
                .onStart {
                    _uiState.value = ChatUiState.Loading
                }
                .catch {
                    _uiState.value = ChatUiState.Error(it.message ?: "An unexpected error occurred")
                }
                .collect { generativeResponse ->
                    generativeResponse.text?.let { outputContent ->
                        val chatResponse = ChatMessage(
                            message = outputContent,
                            isUser = false
                        )
                        _chatHistory.value += chatResponse
                        _uiState.value = ChatUiState.Success(_chatHistory.value)
                    } ?: run {
                        _uiState.value = ChatUiState.Error("No text generated")
                    }
                }
        }
    }*/

}