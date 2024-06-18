package it.gmstyle.getit.compose.screens.chatscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.gmstyle.getit.data.models.ChatMessage

@Composable
fun MessageBubble(
    message: ChatMessage,
) {
    val isUserMessage = message.isUser
    // a chat item like a telegram message bubble
    val backgroundColor = if (isUserMessage) {
        // user message
       MaterialTheme.colorScheme.primaryContainer
    } else {
        // assistant message
        MaterialTheme.colorScheme.tertiaryContainer
    }

    val alignment: Alignment = if (isUserMessage) {
        // user message
        Alignment.CenterEnd
    } else {
        // assistant message
        Alignment.CenterStart
    }

    val bubbleShape = if (isUserMessage) {
        // user message
        MaterialTheme.shapes.small.copy(
            bottomEnd = ZeroCornerSize
        )
    } else {
        // assistant message
        MaterialTheme.shapes.small.copy(
            bottomStart = ZeroCornerSize
        )
    }

    // chat item
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .align(alignment)
                .background(backgroundColor, bubbleShape)
                .padding(8.dp)
        ) {
           Text(text = message.message)
            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (isUserMessage) "You" else "Assistant",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

    }

}

//genera la preview del chat
@Preview
@Composable
fun ChatItemPreview() {
    Column(
        modifier= Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        MessageBubble(
            message = ChatMessage(
                message = "Ciao, come va?",
                isUser = true
            )
        )
        MessageBubble(
            message = ChatMessage(
                message = "Ciao! Tutto bene, grazie!",
                isUser = false
            )
        )
    }
}
