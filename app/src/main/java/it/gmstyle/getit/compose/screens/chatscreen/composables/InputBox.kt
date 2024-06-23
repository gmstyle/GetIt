package it.gmstyle.getit.compose.screens.chatscreen.composables

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.gmstyle.getit.R
import it.gmstyle.getit.compose.composables.commons.CommonTextField
import it.gmstyle.getit.data.models.ChatMessage

@Composable
fun InputBox(
    onSend: (ChatMessage) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    val selectedImages = remember { mutableStateListOf<Bitmap>() }
    // a chat input box
    Column {
        //attached images preview
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            selectedImages.forEach {
                SelectedImagePreview(
                    image = it,
                    onRemove = {
                        selectedImages.remove(it)
                    })
            }
        }

        // input box
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
                leadingIcon = {
                    // icon button for attachments
                    ImagePickerMenu(
                        selectedImages = selectedImages,
                        maxImages = 3,
                        enabled = selectedImages.size < 3,
                        onImagesSelected = {
                            selectedImages.clear()
                            selectedImages.addAll(it)
                        }
                    )
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            // send button
            FilledIconButton(
                enabled = prompt.isNotBlank(),
                onClick = {
                    onSend(
                        ChatMessage(
                            text = prompt,
                            images = selectedImages.toList(),
                            isUser = true
                        )
                    )
                    prompt = ""
                    selectedImages.clear()
                })
            {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "")
            }

        }
    }
}

//  Selettore di immagini o fotocamera per invio di immagini nella chat
@Composable
fun ImagePickerMenu(
    selectedImages: MutableList<Bitmap>,
    onImagesSelected: (List<Bitmap>) -> Unit,
    maxImages: Int = 3,
    enabled: Boolean = true
) {
    var showMenu by remember { mutableStateOf(false) }
    val _selectedImages = remember { mutableStateListOf<Bitmap>() }.apply {
        clear()
        addAll(selectedImages)
    }
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        val newImages = uris.mapNotNull { uri ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.createSource(context.contentResolver, uri).let { source ->
                        ImageDecoder.decodeBitmap(source)
                    }
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }} catch (e: Exception) {
                null
            }
        }
        // Aggiungi le nuove immagini solo se non superano il limite
        if (_selectedImages.size + newImages.size <= maxImages) {
            _selectedImages.addAll(newImages)
            onImagesSelected(_selectedImages)
        }
    }

    val cameraCapture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            _selectedImages.add(it)
            onImagesSelected(_selectedImages)
        }
    }

    IconButton(
        enabled = enabled,
        onClick = { showMenu = true }
    ) {
        Icon(Icons.Filled.Add, contentDescription = "")
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(id = R.string.placeholder_image_picker_take_photo))
            },
            onClick = {
                cameraCapture.launch(null)
                showMenu = false
            })
        DropdownMenuItem(
            text = {
                Text(stringResource(id = R.string.placeholder_image_picker_choose_from_gallery))
            },
            onClick = {
                imagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
                showMenu = false
            })

    }
}

@Preview
@Composable
fun InputBoxPreview() {
    InputBox {}
}