package it.gmstyle.getit.compose.screens.chatscreen.composables

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun SelectedImagePreview(
    image: Bitmap,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            FilledIconButton(
                modifier = Modifier
                    .size(16.dp),
                onClick = {
                    onRemove()
                }) {
                Icon(Icons.Filled.Close, contentDescription = "")
            }
            Image(
                modifier = Modifier
                    .sizeIn(
                        minHeight = 50.dp,
                        maxHeight = 100.dp
                    )
                    .clip(MaterialTheme.shapes.small),
                bitmap = image.asImageBitmap(),
                contentDescription = "",
            )
        }
    }
}