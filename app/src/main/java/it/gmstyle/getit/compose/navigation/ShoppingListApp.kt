package it.gmstyle.getit.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.gmstyle.getit.compose.screens.ShoppingListScreen
import it.gmstyle.getit.compose.screens.ShoppingListsScreen

@Composable
fun ShoppingListApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "shoppingLists") {
        // Add destinations here
        composable("shoppingLists") { ShoppingListsScreen(navController = navController) }
        composable("list/{id}") { navBackStackEntry ->
            val listId = navBackStackEntry.arguments?.getString("id") ?: ""
            ShoppingListScreen(listId = listId)
        }
    }
}