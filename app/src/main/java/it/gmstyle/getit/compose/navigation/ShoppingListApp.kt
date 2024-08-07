package it.gmstyle.getit.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.gmstyle.getit.compose.screens.chatscreen.ChatScreen
import it.gmstyle.getit.compose.screens.listscreen.ShoppingListScreen
import it.gmstyle.getit.compose.screens.homescreen.HomeScreen
import org.koin.compose.KoinContext

@Composable
fun ShoppingListApp() {
    val navController = rememberNavController()

    KoinContext {
        NavHost(navController = navController, startDestination = "home") {
            // Add destinations here
            composable("home") { HomeScreen(navController = navController) }
            composable("list/{id}") { navBackStackEntry ->
                val listId = navBackStackEntry.arguments?.getString("id")?.toIntOrNull()
                ShoppingListScreen(
                    navController = navController,
                    listId = listId)
            }
            composable("chat") {
                ChatScreen(navController = navController)
            }
        }
    }
}