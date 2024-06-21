package it.gmstyle.getit.compose.screens.chatscreen.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
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

    val bubbleShape = MaterialTheme.shapes.small.copy(
        bottomEnd = CornerSize(16.dp),
        bottomStart = CornerSize(16.dp),
        topEnd = CornerSize(16.dp),
        topStart = ZeroCornerSize)

    // chat item
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.align(alignment),
        ) {
            if (isUserMessage) {
                Image(
                    modifier = Modifier
                        .offset(y = (-8).dp, x = 2.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(4.dp)
                    ,
                    imageVector = Icons.Default.Person,
                    contentDescription = ""
                )
            } else {
                Image(
                    modifier = Modifier
                        .offset(y = (-8).dp, x = 2.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(4.dp)
                    ,
                    painter = painterResource(id = R.drawable.baseline_assistant_24),
                    contentDescription = ""
                )
            }
            Column(
                modifier = Modifier
                    .background(backgroundColor, bubbleShape)
                    .padding(8.dp)
            ) {
                Text(text = message.text)
                message.images?.let { images ->
                    images.forEach { image ->
                        Spacer(modifier = Modifier.padding(4.dp))
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = "",
                            modifier = Modifier
                                .sizeIn(maxHeight = 100.dp)
                                .clip(MaterialTheme.shapes.small)
                        )
                    }
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        MessageBubble(
            message = ChatMessage(
                text = "Ciao, come va?",
                isUser = true
            )
        )
        MessageBubble(
            message = ChatMessage(
                text = "Ciao! Tutto bene, grazie!, hai bisogno di aiuto? Posso aiutarti a creare una lista della spesa",
                images = listOf(),
                isUser = false
            )
        )
    }
}
