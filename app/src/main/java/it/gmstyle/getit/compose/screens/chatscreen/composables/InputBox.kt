package it.gmstyle.getit.compose.screens.chatscreen.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.gmstyle.getit.compose.composables.commons.CommonTextField
import it.gmstyle.getit.data.models.ChatMessage

@Composable
fun InputBox(
    onSend: (ChatMessage) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    // a chat input box
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        CommonTextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message") },
        )
        Spacer(modifier = Modifier.width(8.dp))
        // send button
        FilledIconButton(
            enabled = prompt.isNotBlank(),
            onClick = {
                onSend(ChatMessage(prompt, true))
                prompt = ""
            })
        {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "" )
        }

    }
}

@Preview
@Composable
fun InputBoxPreview() {
    InputBox {}
}