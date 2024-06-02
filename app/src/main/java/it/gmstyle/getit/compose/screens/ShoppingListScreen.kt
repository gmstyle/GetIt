package it.gmstyle.getit.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.gmstyle.getit.data.entities.ShoppingItem
import it.gmstyle.getit.viewmodels.ShoppingListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShoppingListScreen(
                       listId: String,
                       viewModel: ShoppingListViewModel = koinViewModel<ShoppingListViewModel>()) {
    val list by viewModel.getShoppingListById(listId.toInt()).collectAsState(initial = null)
    val items by viewModel.getItemsByListId(listId.toInt()).collectAsState(initial = emptyList())
    var newItemName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(list?.name ?: "Loading...")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            BasicTextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                viewModel.insertItem(ShoppingItem(listId = listId.toInt(), name = newItemName))
                newItemName = ""
            }) {
                Text("Add")
            }
        }
        LazyColumn {
            items(items) { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(item.name, modifier = Modifier.weight(1f))
                    Checkbox(checked = item.completed, onCheckedChange = {
                        viewModel.updateItem(item.copy(completed = it))
                    })
                    Button(onClick = { viewModel.deleteItem(item) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }

}