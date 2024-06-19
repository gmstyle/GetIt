package it.gmstyle.getit.viewmodels.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.gmstyle.getit.data.models.ChatMessage
import it.gmstyle.getit.data.repositories.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private var _chatHistory =MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory
     private val loadingMessage = ChatMessage("...", false)

    fun sendMessage(chatPrompt: ChatMessage) {
        viewModelScope.launch {
            _chatHistory.emit(_chatHistory.value + chatPrompt)
           _chatHistory.emit(_chatHistory.value + loadingMessage)

            try {
                /*val generativeResponse = chatRepository.sendMessage(chatPrompt.message)
                generativeResponse.text?.let { outputContent ->
                    val chatResponse = ChatMessage(
                        message = outputContent,
                        isUser = false
                    )
                    _chatHistory.emit(_chatHistory.value - loadingMessage)
                    _chatHistory.emit(_chatHistory.value + chatResponse)

                } ?: run {
                    _chatHistory.emit(_chatHistory.value - loadingMessage)
                    _chatHistory.emit(_chatHistory.value + ChatMessage("No text generated", false))

                }*/

                val chatResponse = ChatMessage(
                    message = "Mocked response from the model",
                    isUser = false
                )
                _chatHistory.emit(_chatHistory.value - loadingMessage)
                _chatHistory.emit(_chatHistory.value + chatResponse)

            } catch (e: Exception) {
               val errorMessage = e.message ?: "An unexpected error occurred"
                _chatHistory.emit(_chatHistory.value - loadingMessage)
                _chatHistory.emit(_chatHistory.value + ChatMessage(errorMessage, false))

            }
        }
    }

}