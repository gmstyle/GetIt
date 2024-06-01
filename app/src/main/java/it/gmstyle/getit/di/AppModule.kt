package it.gmstyle.getit.di

import androidx.room.Room
import it.gmstyle.getit.data.ShoppingListDatabase
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            ShoppingListDatabase::class.java,
            "shopping_list_database"
        ).build()
    }
    single { get<ShoppingListDatabase>().shoppingListDao() }
    single { get<ShoppingListDatabase>().shoppingItemDao() }
    ///Repositories

    ///ViewModels

}