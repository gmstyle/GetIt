package it.gmstyle.getit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import it.gmstyle.getit.data.dao.ShoppingItemDao
import it.gmstyle.getit.data.dao.ShoppingListDao
import it.gmstyle.getit.data.entities.ShoppingItem
import it.gmstyle.getit.data.entities.ShoppingList

@Database(entities = [ShoppingList::class, ShoppingItem::class], version = 1)
abstract class ShoppingListDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingItemDao(): ShoppingItemDao

}