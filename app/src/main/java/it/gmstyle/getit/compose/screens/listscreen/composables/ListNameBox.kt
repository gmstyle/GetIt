package it.gmstyle.getit.compose.screens.listscreen.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import it.gmstyle.getit.compose.composables.commons.CommonTextField

@Composable
fun ListNameBox(
    editableListName: String,
    onListNameChange: (String) -> Unit,
    onSaveList: (String) -> Unit,
) {
    var _editableListName by remember { mutableStateOf(editableListName) }
    var hasBeenFocused by remember { mutableStateOf(false) }
    Row {
        CommonTextField(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    hasBeenFocused = hasBeenFocused || focusState.isFocused
                    if (!focusState.isFocused && hasBeenFocused && _editableListName.isNotEmpty()) {
                        onSaveList(_editableListName)
                    }
                },
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = null
                )
            },
            value = _editableListName,
            onValueChange = { newName ->
                _editableListName = newName
                onListNameChange(_editableListName)
            },
            label = { Text("List name") },
        )
    }
}