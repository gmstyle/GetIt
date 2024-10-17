package it.gmstyle.getit.compose.screens.listscreen.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import it.gmstyle.getit.R
import it.gmstyle.getit.compose.composables.commons.CommonTextField

@Composable
fun ListNameBox(
    editableListName: String,
    onListNameChange: (String) -> Unit,
    onSaveList: (String) -> Unit,
    isError: Boolean = false
) {
    var hasBeenFocused by remember { mutableStateOf(false) }
    Row {
        CommonTextField(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    hasBeenFocused = hasBeenFocused || focusState.isFocused
                    if (!focusState.isFocused && hasBeenFocused && editableListName.isNotEmpty()) {
                        onSaveList(editableListName)
                    }
                },
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = null
                )
            },
            isError =  isError,
            value = editableListName,
            onValueChange = { newName ->
                onListNameChange(newName)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = androidx.compose.ui.text.input.ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSaveList(editableListName)
                }
            ),
            placeholder = { Text(stringResource(id = R.string.placeholder_list_name)) }
        )
    }
}