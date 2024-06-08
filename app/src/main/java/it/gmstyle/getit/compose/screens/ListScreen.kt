package it.gmstyle.getit.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.viewmodels.ShoppingListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    navController: NavController,
    listId: String,
    viewModel: ShoppingListViewModel = koinViewModel<ShoppingListViewModel>()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val listItems by viewModel.items.collectAsState(initial = emptyList())

    LaunchedEffect(key1 = Unit) {
        if (listId != "0") {
            viewModel.getShoppingListWithItems(listId.toInt())
        }
    }

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        MediumTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        ), title = {
            Text(viewModel.listName.value.let {
                it.ifEmpty { "New List" }
            }, overflow = TextOverflow.Ellipsis)
        }, navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                content = {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                },
            )
        }, scrollBehavior = scrollBehavior

        )

    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Row {
                TextField(value = viewModel.listName.value, onValueChange = { newName ->
                    viewModel.listName.value = newName
                }, label = { Text("List Name") }, modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (listId == "0") {
                        val list = ShoppingList(name = viewModel.listName.value)
                        viewModel.saveList(list)
                    } else {
                        val list =
                            ShoppingList(id = listId.toInt(), name = viewModel.listName.value)
                        viewModel.updateList(list)
                    }
                }) {
                    Text("Save")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Aggiungi un singolo elemento per l'aggiunta di un nuovo elemento
                item {
                    var newItemName by remember { mutableStateOf("") }

                    Row {
                        TextField(
                            modifier = Modifier.weight(1f),
                            value = newItemName,
                            onValueChange = { newName ->
                                newItemName = newName
                            },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            //TODO: funziona ma formalmente non corretto e da rivedere
                            if (listId != "0") {
                                viewModel.insertItem(
                                    ListItem(
                                        name = newItemName, listId = listId.toInt()
                                    )
                                )
                                coroutineScope.launch {
                                    viewModel.getShoppingListWithItems(listId.toInt())
                                }
                            } else {
                                viewModel.insertItem(
                                    ListItem(
                                        name = newItemName,
                                        listId = viewModel.newListId.intValue
                                    )
                                )
                                coroutineScope.launch {
                                    viewModel.getShoppingListWithItems(viewModel.newListId.intValue)
                                }
                            }

                            newItemName = ""
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                    }
                }

                // Elenco esistente di elementi
                items(listItems.reversed()) { item ->
                    Row {
                        TextField(
                            modifier = Modifier.weight(1f),
                            value = item.name,
                            onValueChange = { newName ->
                                viewModel.updateItem(item.copy(name = newName))
                            },
                        )
                        Checkbox(checked = item.completed, onCheckedChange = { isChecked ->
                            viewModel.updateItem(item.copy(completed = isChecked))
                        })
                    }
                }
            }
        }
    }
}