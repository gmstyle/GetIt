package it.gmstyle.getit.compose.screens.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import it.gmstyle.getit.R
import it.gmstyle.getit.compose.screens.homescreen.composables.ListSticker
import it.gmstyle.getit.viewmodels.shoppinglists.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
) {

    val lists by viewModel.shoppingLists.collectAsState(initial = emptyList())

    Scaffold(
        contentWindowInsets = WindowInsets.ime,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    // Aggiungi il pulsante per creare una nuova lista
                    IconButton(onClick = {
                        // navigo verso la schermata ListScreen per creare una nuova lista
                        val listId: Int? = null
                        navController.navigate("list/$listId")
                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                    }
                }
            )
        },
        floatingActionButton = {
            // val alla schermata chat
            ExtendedFloatingActionButton(
                text = { Text(stringResource(id = R.string.button_label_ai_assistant)) },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.baseline_assistant_24),
                        contentDescription = "Chat"
                    )
                },
                onClick = { navController.navigate("chat") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(lists) { list ->
                    ListSticker(
                        shoppingList = list,
                        onViewList = {
                            navController.navigate("list/${list.list.id}")
                        },
                        onDeleteList = {
                            viewModel.deleteListWithItems(list.list)
                        }
                    )
                }
            }
        }
    }
}


