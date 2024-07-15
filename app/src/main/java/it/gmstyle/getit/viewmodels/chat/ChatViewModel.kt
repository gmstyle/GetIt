package it.gmstyle.getit.viewmodels.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.InvalidStateException
import com.google.ai.client.generativeai.type.content
import it.gmstyle.getit.data.models.ChatMessage
import it.gmstyle.getit.services.ChatService
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatViewModel(
    private val chatService: ChatService,
) : ViewModel() {

    private var _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory
    private val loadingMessage = ChatMessage("...", isUser = false)
    private val geminiToolsHelper = chatService.geminiToolsHelper

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
                val functionDeclarations =
                    chatService.generativeModel.tools?.flatMap { it.functionDeclarations ?: emptyList() }

                val functionCalls = generativeResponse.functionCalls
                Log.d("CHAT_DEBUG", "Function calls: $functionCalls")

                if (functionCalls.isNotEmpty()) {

                    functionCalls.forEach { functionCall ->
                            val matchedFunction = functionDeclarations
                                ?.first { functionDeclaration ->
                                    functionDeclaration.name == functionCall.name }
                                ?: throw InvalidStateException("Function not found: ${functionCall.name}")

                            // Esegue la funzione corrispondente
                            val functionResponse = JSONObject()
                            when(matchedFunction.name) {
                                "createList" -> {
                                    val listName = functionCall.args["listName"] ?: throw InvalidStateException("Missing argument: listName")
                                    val result = geminiToolsHelper.createList(listName)

                                    functionResponse.put("result", result)
                                }
                                "addItemsToList" -> {
                                    val listName = functionCall.args["listId"] ?: throw InvalidStateException("Missing argument: listId")
                                    val names = functionCall.args["names"] ?: throw InvalidStateException("Missing argument: names")
                                    val result = geminiToolsHelper.addItemsToList(listName, names)

                                    functionResponse.put("result", result)
                                }
                                "updateItems" -> {
                                    val listName = functionCall.args["listId"] ?: throw InvalidStateException("Missing argument: listId")
                                    val names = functionCall.args["names"] ?: throw InvalidStateException("Missing argument: names")
                                    val completed = functionCall.args["completed"]  ?: throw InvalidStateException("Missing argument: completed")
                                    val result = geminiToolsHelper.updateItems(listName, names, completed.toBoolean())

                                    functionResponse.put("result", result)
                                }
                                "deleteItemsFromList" -> {
                                    val listName = functionCall.args["listId"] ?: throw InvalidStateException("Missing argument: listId")
                                    val names = functionCall.args["names"] ?: throw InvalidStateException("Missing argument: names")
                                    val result = geminiToolsHelper.deleteItemsFromList(listName, names)

                                    functionResponse.put("result", result)
                                }
                                "getListByName" -> {
                                    val listName = functionCall.args["listName"] ?: throw InvalidStateException("Missing argument: listName")
                                    val result = geminiToolsHelper.getListByName(listName)

                                    functionResponse.put("result", result)
                                }
                                "getAllLists" -> {
                                    val fake = functionCall.args["fake"] ?: throw InvalidStateException("Missing argument: fake")
                                    val result = geminiToolsHelper.getAllLists( fake)
                                    functionResponse.put("result", result)
                                }
                            }
                            // Aggiunge la risposta della funzione alla risposta generativa
                            generativeResponse = chatService.sendMessage(
                                content(role = "function") {
                                    part(FunctionResponsePart(functionCall.name, functionResponse))
                                }
                            )
                        }

                } else {
                    Log.d("CHAT_DEBUG", "No function calls found")
                }

                handleResponse(generativeResponse)

            } catch (e: Exception) {
                Log.e("CHAT_ERROR", "Error sending message: ${e.stackTrace}")
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