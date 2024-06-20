package it.gmstyle.getit.compose.screens.listscreen.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.gmstyle.getit.compose.composables.commons.CommonTextField

@Composable
fun NewItemBox(
    enabled: Boolean,
    onSave: (String) -> Unit,
) {
    var itemName by remember { mutableStateOf("") }
    Row {
        CommonTextField(
            modifier = Modifier.weight(1f),
            value = itemName,
            onValueChange = { newName ->
                itemName = newName
            },
        )
        Spacer(modifier = Modifier.width(8.dp))
        FilledIconButton(
            enabled = enabled && itemName.isNotBlank(),
            onClick = {
              onSave(itemName)
                itemName = ""
            }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }

}