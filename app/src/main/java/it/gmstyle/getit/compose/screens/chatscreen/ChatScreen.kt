package it.gmstyle.getit.compose.screens.chatscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.gmstyle.getit.data.models.ChatMessage
import it.gmstyle.getit.viewmodels.chat.ChatViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = koinViewModel<ChatViewModel>()
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    var prompt by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(text = "Get it! Assistant")},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout =  true
            ) {
                val chatHistoryReversed = chatHistory.reversed()
                items(chatHistoryReversed) { chatMessage ->
                    MessageBubble(chatMessage)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            ) {
                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                // send button
                IconButton(
                    onClick = {
                        viewModel.sendMessage(ChatMessage(prompt, true))
                        prompt = ""
                    })
                {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "" )
                }

            }
        }
    }
}