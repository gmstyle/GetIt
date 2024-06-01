package it.gmstyle.getit.compose

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.viewmodels.ShoppingListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShoppingListsScreen(
    navController: NavController,
    viewModel: ShoppingListViewModel = koinViewModel<ShoppingListViewModel>()
) {

    val lists by viewModel.shoppingLists.collectAsState(initial = emptyList())
    var newListName by remember {
        mutableStateOf("")
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // show a dialog to add a new list

            }) {
                Row(modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                    Text("Add List")
                }

            }
        }
    ) { padding ->


        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.insertList(ShoppingList(name = newListName))
                    newListName = ""
                }) {
                    Text("Add")
                }

            }
            LazyColumn {
                items(lists) { list ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)) {
                        Text(list.name, modifier = Modifier.weight(1f))
                        Button(onClick = { navController.navigate("list/${list.id}") }) {
                            Text("View")
                        }
                        Button(onClick = { viewModel.deleteList(list) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}