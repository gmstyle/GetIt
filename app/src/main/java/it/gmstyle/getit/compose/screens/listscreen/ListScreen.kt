package it.gmstyle.getit.compose.screens.listscreen

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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.gmstyle.getit.R
import it.gmstyle.getit.compose.composables.commons.CommonLoader
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.viewmodels.shoppinglist.ShoppingListUiState
import it.gmstyle.getit.viewmodels.shoppinglist.ShoppingListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    navController: NavController,
    listId: Int? = null,
    viewModel: ShoppingListViewModel = koinViewModel<ShoppingListViewModel>()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsState()
    var id by remember { mutableIntStateOf(0) }
    var itemName by remember { mutableStateOf("") }
    var editableListName by remember { mutableStateOf("") }
    var hasBeenFocused by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = Unit) {
        listId?.let {
            viewModel.getShoppingListWithItems(it)
        }
        // Osservo lo stato per aggiornare il nome della lista che viene visualizzato
        // quando viene cambiato il nome della lista il valore viene aggiornato correttamente
        snapshotFlow { uiState }
            .collect {
                if (it is ShoppingListUiState.Success) {
                    coroutineScope.launch {
                        editableListName = it.listWithItems.list.name
                    }
                }
            }
    }

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
        MediumTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        ),
            title = {
                Text(
                    editableListName.ifEmpty { "New List" }, overflow = TextOverflow.Ellipsis
                )
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
    },
        floatingActionButton = {
            // val alla schermata chat
            FloatingActionButton(
                onClick = { navController.navigate("chat") }
            ) {
                Icon(
                    painterResource(id = R.drawable.baseline_assistant_24) ,
                    contentDescription = "Chat")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            // Nome lista e pulsante di salvataggio
            Row {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { focusState ->
                            hasBeenFocused = hasBeenFocused || focusState.isFocused
                            if (!focusState.isFocused && hasBeenFocused && editableListName.isNotEmpty()) {
                                saveList(editableListName, uiState, editableListName, viewModel)
                            }
                        },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    value = editableListName,
                    onValueChange = { newName ->
                        editableListName = newName
                        //saveList(newName, state, editableListName, viewModel)
                    },
                    label = { Text("List name") },
                )
               /* Spacer(modifier = Modifier.width(8.dp))
                Button(
                    enabled = editableListName.isNotEmpty(),
                    onClick = {
                        saveList(editableListName, state, editableListName, viewModel)
                }) {
                    Text("Save")
                }*/
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Riga per aggiungere un nuovo elemento alla lista
            Row {
                TextField(

                    modifier = Modifier.weight(1f),
                    value = itemName,
                    onValueChange = { newName ->
                        itemName = newName
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    enabled = id != 0 && itemName.isNotEmpty(),
                    onClick = {
                    val listItem = ListItem(listId = id, name = itemName)
                    viewModel.saveItem(listItem)
                    itemName = ""
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Elementi della lista
            when (uiState) {
                is ShoppingListUiState.Loading -> {
                    CommonLoader()
                }

                is ShoppingListUiState.Error -> {
                    Text((uiState as ShoppingListUiState.Error).message)
                }

                is ShoppingListUiState.Success -> {
                    val listItems = (uiState as ShoppingListUiState.Success).listWithItems.items
                    id = (uiState as ShoppingListUiState.Success).listWithItems.list.id
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Elenco esistente di elementi
                        items(listItems.reversed()) { item ->
                            Row {
                                TextField(
                                    modifier = Modifier.weight(1f),
                                    value = item.name,
                                    // quando item.completed mostra il testo sbarrato
                                    // se non mostra nulla
                                    enabled = !item.completed,
                                    textStyle = if (item.completed)
                                        MaterialTheme.typography.bodyLarge.copy(
                                            textDecoration = TextDecoration.LineThrough
                                        ) else MaterialTheme.typography.bodyLarge,
                                    onValueChange = { newName ->
                                        if (newName != item.name && newName.isNotEmpty()) {
                                            viewModel.updateItem(item.copy(name = newName))
                                        }
                                    },
                                    leadingIcon = {
                                        Checkbox(checked = item.completed, onCheckedChange = { isChecked ->
                                            viewModel.updateItem(item.copy(completed = isChecked))
                                        })
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            viewModel.deleteItem(item)
                                        }) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

private fun saveList(
    newName: String,
    state: ShoppingListUiState,
    editableListName: String,
    viewModel: ShoppingListViewModel
) {
    if (newName.isNotEmpty()) {
        val newList = when (state) {
            is ShoppingListUiState.Success -> {
                val list = state.listWithItems.list
                list.copy(name = editableListName)
            }

            else -> ShoppingList(name = editableListName)
        }

        if (state is ShoppingListUiState.Success) {
            viewModel.updateList(newList)
        } else {
            viewModel.saveList(newList)
        }
    }
}