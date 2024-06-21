package it.gmstyle.getit.data.models

import android.graphics.Bitmap
import androidx.compose.runtime.snapshots.SnapshotStateList

data class ChatMessage(
    val message: String,
    val images: List<Bitmap>? = null,
    val isUser: Boolean
)
