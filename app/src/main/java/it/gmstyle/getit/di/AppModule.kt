package it.gmstyle.getit.di

import androidx.room.Room
import it.gmstyle.getit.data.ShoppingListDatabase
import it.gmstyle.getit.data.dao.ListItemDao
import it.gmstyle.getit.data.dao.ShoppingListDao
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import it.gmstyle.getit.viewmodels.shoppinglist.ShoppingListViewModel
import it.gmstyle.getit.viewmodels.shoppinglists.ShoppingListsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            ShoppingListDatabase::class.java,
            "shopping_list_database"
        ).build()
    }
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

    ///ViewModels
    viewModel<ShoppingListsViewModel> { ShoppingListsViewModel(get<ShoppingListRepository>()) }
    viewModel<ShoppingListViewModel> { ShoppingListViewModel(get<ShoppingListRepository>()) }


}