package it.gmstyle.getit.viewmodels.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.InvalidStateException
import com.google.ai.client.generativeai.type.content
import it.gmstyle.getit.data.models.ChatMessage
import it.gmstyle.getit.services.ChatService
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
    private val functionDeclarations =
        chatService.generativeModel.tools?.flatMap { it.functionDeclarations ?: emptyList() }

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

                val functionCalls = generativeResponse.functionCalls
               // logga i nomi delle funzioni chiamate
                Log.d("CHAT_DEBUG", "Function calls: ${functionCalls.map { it.name }
                }")

                functionCalls.forEach { functionCall ->
                    handleFunctionCall(functionCall)?.let { updatedResponse ->
                        generativeResponse = updatedResponse
                    }
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

    private suspend fun handleFunctionCall(
        functionCall: FunctionCallPart
    ): GenerateContentResponse? {
        val matchedFunction = functionDeclarations
            ?.firstOrNull { it.name == functionCall.name }
            ?: return null // Ignora funzioni non trovate

        val functionResponse = JSONObject().apply {
            when (matchedFunction.name) {
                "createList" -> {
                    val listName = functionCall.args["listName"]
                        ?: throw InvalidStateException("Missing argument: listName")
                    put("result", geminiToolsHelper.createList(listName))
                }

                "addItems" -> {
                    val listName = functionCall.args["listId"]
                        ?: throw InvalidStateException("Missing argument: listId")
                    val names = functionCall.args["names"]
                        ?: throw InvalidStateException("Missing argument: names")
                    put("result", geminiToolsHelper.addItems(listName, names))
                }

                "updateItems" -> {
                    val listName = functionCall.args["listId"]
                        ?: throw InvalidStateException("Missing argument: listId")
                    val items = functionCall.args["items"]
                        ?: throw InvalidStateException("Missing argument: items")
                    put(
                        "result",
                        geminiToolsHelper.updateItems(listName, items)
                    )
                }

                "deleteItems" -> {
                    val listName = functionCall.args["listId"]
                        ?: throw InvalidStateException("Missing argument: listId")
                    val names = functionCall.args["names"]
                        ?: throw InvalidStateException("Missing argument: names")
                    put("result", geminiToolsHelper.deleteItems(listName, names))
                }

                "getListByName" -> {
                    val listName = functionCall.args["listName"]
                        ?: throw InvalidStateException("Missing argument: listName")
                    put("result", geminiToolsHelper.getListByName(listName))
                }

                "getAllLists" -> {
                    val fake = functionCall.args["fake"]
                        ?: throw InvalidStateException("Missing argument: fake")
                    put("result", geminiToolsHelper.getAllLists(fake))
                }
            }
        }

        return chatService.sendMessage(
            content(role = "function") {
                part(FunctionResponsePart(functionCall.name, functionResponse))
            }
        )
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