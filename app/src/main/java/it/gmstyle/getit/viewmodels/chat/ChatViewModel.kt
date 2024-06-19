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
    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState.Initial())
    val uiState: StateFlow<ChatUiState> get()= _uiState.asStateFlow()
    private val _chatHistory = mutableListOf<ChatMessage>()
    private val loadingMessage = ChatMessage("...", false)

    fun sendMessage(chatPrompt: ChatMessage) {


        viewModelScope.launch {
            _chatHistory.add(chatPrompt)
           _chatHistory.add(loadingMessage)
            _uiState.value = ChatUiState.Loading(_chatHistory)
            try {
                val generativeResponse = chatRepository.sendMessage(chatPrompt.message)
                generativeResponse.text?.let { outputContent ->
                    val chatResponse = ChatMessage(
                        message = outputContent,
                        isUser = false
                    )
                    _chatHistory.remove(loadingMessage)
                    _chatHistory.add(chatResponse)
                    _uiState.value = ChatUiState.Success(_chatHistory)
                } ?: run {
                    _chatHistory.remove(loadingMessage)
                    _chatHistory.add(ChatMessage("No text generated", false))
                    _uiState.value = ChatUiState.Error(_chatHistory)
                }

                /*val chatResponse = ChatMessage(
                    message = "Mocked response from the model",
                    isUser = false
                )
                _chatHistory.remove(loadingMessage)
                _chatHistory.add(chatResponse)
                _uiState.value = ChatUiState.Success(_chatHistory)*/

            } catch (e: Exception) {
               val errorMessage = e.message ?: "An unexpected error occurred"
                _chatHistory.remove(loadingMessage)
                _chatHistory.add(ChatMessage(errorMessage, false))
                _uiState.value = ChatUiState.Error(_chatHistory)
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