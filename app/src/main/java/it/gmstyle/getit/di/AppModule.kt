package it.gmstyle.getit.di

import androidx.room.Room
import it.gmstyle.getit.data.ShoppingListDatabase
import it.gmstyle.getit.data.dao.ListItemDao
import it.gmstyle.getit.data.dao.ShoppingListDao
import it.gmstyle.getit.services.ChatService
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import it.gmstyle.getit.services.helpers.GeminiToolsHelper
import it.gmstyle.getit.viewmodels.chat.ChatViewModel
import it.gmstyle.getit.viewmodels.shoppinglist.ShoppingListViewModel
import it.gmstyle.getit.viewmodels.shoppinglists.HomeViewModel
import org.koin.core.module.dsl.*
import org.koin.dsl.module

val appModule = module {
    ///Database
    single {
        Room.databaseBuilder(
            get(),
            ShoppingListDatabase::class.java,
            "shopping_list_database"
        ).addMigrations(ShoppingListDatabase.MIGRATION_1_2)
            .build()
    }
    ///Daos
    single<ShoppingListDao> {
        val db = get<ShoppingListDatabase>()
        db.shoppingListDao()
    }
    single<ListItemDao> {
        val db = get<ShoppingListDatabase>()
        db.shoppingItemDao()
    }
    ///Repositories
    single<ShoppingListRepository> { ShoppingListRepository(get<ShoppingListDao>(), get<ListItemDao>()) }

    ///Services
    single<GeminiToolsHelper> { GeminiToolsHelper(get<ShoppingListRepository>()) }
    single<ChatService> { ChatService(get<GeminiToolsHelper>()) }

    ///ViewModels
    viewModel<HomeViewModel> { HomeViewModel(get<ShoppingListRepository>()) }
    viewModel<ShoppingListViewModel> { ShoppingListViewModel(get<ShoppingListRepository>()) }
    viewModel<ChatViewModel> { ChatViewModel(get<ChatService>()) }


}