package it.gmstyle.getit.compose.screens.chatscreen.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.gmstyle.getit.R
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
            bottomEnd = ZeroCornerSize,
            bottomStart = CornerSize(16.dp),
            topEnd = CornerSize(16.dp),
            topStart = CornerSize(16.dp)

        )
    } else {
        // assistant message
        MaterialTheme.shapes.small.copy(
            bottomStart = ZeroCornerSize,
            bottomEnd = CornerSize(16.dp),
            topStart = CornerSize(16.dp),
            topEnd = CornerSize(16.dp)
        )
    }

    // chat item
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .align(alignment)
                .background(backgroundColor, bubbleShape)
                .padding(8.dp)
        ) {
           Text(text = message.message)
            Spacer(modifier = Modifier.padding(4.dp))
            if (isUserMessage) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "",
                        modifier = Modifier
                            .size(18.dp)
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(text = stringResource(id =R.string.placeholder_user))
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_assistant_24),
                        contentDescription = "",
                        modifier = Modifier
                            .size(18.dp)
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(text = stringResource(id = R.string.placeholder_ai_assistant))
                }
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
                message = "Ciao! Tutto bene, grazie!, hai bisogno di aiuto? Posso aiutarti a creare una lista della spesa",
                isUser = false
            )
        )
    }
}
