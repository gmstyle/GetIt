package it.gmstyle.getit.data.models

import android.graphics.Bitmap

data class ChatMessage(
    val text: String,
    val images: List<Bitmap>? = null,
    val isUser: Boolean
)
