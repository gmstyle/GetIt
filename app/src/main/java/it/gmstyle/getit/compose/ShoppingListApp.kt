package it.gmstyle.getit.compose

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val SHOPPING_LISTS_ROUTE = "shoppingLists"

private const val SHOPPING_LIST_DETAIL_ROUTE = "shoppingListDetail"

@Composable
fun ShoppingListApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SHOPPING_LISTS_ROUTE) {
        // Add destinations here
        composable(SHOPPING_LISTS_ROUTE) { ShoppingListsScreen(navController = navController) }
        composable(SHOPPING_LIST_DETAIL_ROUTE) { navBackStackEntry ->
            val listId = navBackStackEntry.arguments?.getString("listId") ?: ""
            ShoppingListScreen(navController = navController, listId = listId)
        }
    }
}