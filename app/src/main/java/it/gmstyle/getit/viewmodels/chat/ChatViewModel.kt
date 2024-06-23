package it.gmstyle.getit.viewmodels.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.InvalidStateException
import com.google.ai.client.generativeai.type.content
import it.gmstyle.getit.data.models.ChatMessage
import it.gmstyle.getit.data.services.ChatService
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatViewModel(
    private val chatService: ChatService,
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {

    private var _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory
    private val loadingMessage = ChatMessage("...", isUser = false)

    fun sendMessage(chatPrompt: ChatMessage) {
        viewModelScope.launch {
            _chatHistory.emit(_chatHistory.value + chatPrompt)
            _chatHistory.emit(_chatHistory.value + loadingMessage)

            try {
                val intputContent = content {
                    text(chatPrompt.text)
                    chatPrompt.images?.forEach { image(it) }

                }
                var generativeResponse = chatService.sendMessage(intputContent)
                // Verifica se la risposta contiene chiamate a funzioni
                generativeResponse.functionCalls.let { functionCalls ->
                    functionCalls.forEach { functionCall ->
                        val matchedFunction = chatService.generativeModel.tools?.flatMap { it.functionDeclarations }
                            ?.first { it.name == functionCall.name }
                            ?: throw InvalidStateException("Function not found: ${functionCall.name}")

                        // Esegue la funzione
                        val functionResponse: JSONObject = matchedFunction.execute(functionCall)

                        generativeResponse = chatService.sendMessage(
                            content(role = "function") {
                                part(FunctionResponsePart(functionCall.name, functionResponse))
                            }
                        )
                    }
                }
                handleResponse(generativeResponse)

            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unexpected error occurred"
                _chatHistory.emit(_chatHistory.value - loadingMessage)
                _chatHistory.emit(_chatHistory.value + ChatMessage(errorMessage, isUser = false))
            }
        }
    }

    private suspend fun handleResponse(generativeResponse: GenerateContentResponse) {
        generativeResponse.text?.let { outputContent ->
            val chatResponse = ChatMessage(
                text = outputContent,
                isUser = false
            )
            _chatHistory.emit(_chatHistory.value - loadingMessage)
            _chatHistory.emit(_chatHistory.value + chatResponse)
        } ?: run {
            _chatHistory.emit(_chatHistory.value - loadingMessage)
            _chatHistory.emit(_chatHistory.value + ChatMessage("No text generated", isUser = false))
        }
    }

}