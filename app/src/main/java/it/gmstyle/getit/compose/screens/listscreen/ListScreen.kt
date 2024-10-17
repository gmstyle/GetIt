package it.gmstyle.getit.compose.screens.listscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.gmstyle.getit.R
import it.gmstyle.getit.compose.composables.commons.CommonLoader
import it.gmstyle.getit.compose.composables.commons.CommonTextField
import it.gmstyle.getit.compose.screens.listscreen.composables.ItemBox
import it.gmstyle.getit.compose.screens.listscreen.composables.ListNameBox
import it.gmstyle.getit.compose.screens.listscreen.composables.NewItemBox
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
    var editableListName by remember { mutableStateOf("") }


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

    Scaffold(
        contentWindowInsets = WindowInsets.ime,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.primary
            ),
                title = {
                    Text(
                        editableListName.ifEmpty { stringResource(id = R.string.placeholder_list_name) },
                        overflow = TextOverflow.Ellipsis
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
        }
    ) { innerPadding ->
        ShoppingListContent(
            innerPadding = innerPadding,
            editableListName = editableListName,
            onListNameChange = { newName -> editableListName = newName },
            onSaveList = { newName ->
                saveList(newName, uiState, editableListName, viewModel)
            },
            uiState = uiState,
            id = id,
            viewModel = viewModel
        )
    }
}

@Composable
fun ShoppingListContent(
    innerPadding: PaddingValues,
    editableListName: String,
    onListNameChange: (String) -> Unit,
    onSaveList: (String) -> Unit,
    uiState: ShoppingListUiState,
    id: Int,
    viewModel: ShoppingListViewModel
) {
    var idState by remember { mutableIntStateOf(id) }
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ListNameBox(
            editableListName = editableListName,
            onListNameChange = onListNameChange,
            onSaveList = onSaveList,
            isError = uiState is ShoppingListUiState.Error
        )

        when (uiState) {
            is ShoppingListUiState.Success -> {
                NewItemBox(enabled = idState != 0) { itemName ->
                    val listItem = ListItem(listId = idState, name = itemName)
                    viewModel.saveItem(listItem)
                }
            }
            is ShoppingListUiState.Error -> {
                ErrorMessage(uiState.message)
            }
            is ShoppingListUiState.Loading -> {
                CommonLoader()
            }
            else -> {}
        }

        if (uiState is ShoppingListUiState.Success) {
            val listItems = uiState.listWithItems.items
            idState = uiState.listWithItems.list.id
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listItems) { item ->
                    ItemBox(
                        item = item,
                        onUpdateItem = viewModel::updateItem,
                        onDelete = viewModel::deleteItem
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.error
    )
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