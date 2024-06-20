package it.gmstyle.getit.compose.screens.listscreen.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextDecoration
import it.gmstyle.getit.compose.composables.commons.CommonTextField
import it.gmstyle.getit.data.entities.ListItem

@Composable
fun ItemBox(
    item: ListItem,
    onUpdateItem: (ListItem) -> Unit,
    onDelete: (ListItem) -> Unit,
) {
    var hasBeenFocused by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf(item.name) }
    Row {
        CommonTextField(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    hasBeenFocused =  hasBeenFocused || focusState.isFocused
                    if (!focusState.isFocused && hasBeenFocused && itemName.isNotEmpty()) {
                        if (item.name != itemName) {
                            onUpdateItem(item.copy(name = itemName))
                        }
                    }
                }
            ,
            value = itemName,
            // quando item.completed mostra il testo sbarrato
            // se non mostra nulla
            enabled = !item.completed,
            textStyle = if (item.completed)
                MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.LineThrough
                ) else MaterialTheme.typography.bodyLarge,
            onValueChange = { newName ->
                itemName = newName
            },
            leadingIcon = {
                Checkbox(
                    checked = item.completed,
                    onCheckedChange = { isChecked ->
                        onUpdateItem(item.copy(completed = isChecked))
                    })
            },
            trailingIcon = {
                IconButton(onClick = {
                    onDelete(item)
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            },
        )
    }
}