package it.gmstyle.getit.viewmodels.chat

import androidx.lifecycle.ViewModel
import com.google.ai.client.generativeai.GenerativeModel
import it.gmstyle.getit.BuildConfig

class ChatViewModel : ViewModel() {

    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.geminiApiKey
    )
}