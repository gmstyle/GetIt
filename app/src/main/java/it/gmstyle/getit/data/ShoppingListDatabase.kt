package it.gmstyle.getit.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import it.gmstyle.getit.data.dao.ListItemDao
import it.gmstyle.getit.data.dao.ShoppingListDao
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList

@Database(entities = [ShoppingList::class, ListItem::class], version = 2)
abstract class ShoppingListDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingItemDao(): ListItemDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {

                // Create a new table with the unique constraint
                db.execSQL("""
            CREATE TABLE ShoppingList_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
        """.trimIndent())

                // Copy the data from the old table to the new table
                db.execSQL("""
            INSERT INTO ShoppingList_new (id, name)
            SELECT id, name FROM ShoppingList
        """.trimIndent())

                // Remove the old table
                db.execSQL("DROP TABLE ShoppingList")

                // Rename the new table to the old table name
                db.execSQL("ALTER TABLE ShoppingList_new RENAME TO ShoppingList")

                // Create the unique index on the name column
                db.execSQL("CREATE UNIQUE INDEX index_ShoppingList_name ON ShoppingList(name)")
            }

        }
    }

}