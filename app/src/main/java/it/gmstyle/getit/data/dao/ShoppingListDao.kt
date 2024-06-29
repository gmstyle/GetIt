package it.gmstyle.getit.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.entities.ShoppingListWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Transaction
    @Query("SELECT * FROM ShoppingList")
    fun getAllWithItemsFlow(): Flow<List<ShoppingListWithItems>>

    @Transaction
    @Query("SELECT * FROM ShoppingList")
    suspend fun getAllWithItems(): List<ShoppingListWithItems>

    @Transaction
    @Query("SELECT * FROM ShoppingList WHERE id = :id")
    fun getWithItemsByListIdFlow(id: Int): Flow<ShoppingListWithItems>

    @Transaction
    @Query("SELECT * FROM ShoppingList WHERE id = :id")
    suspend fun getWithItemsByListId(id: Int): ShoppingListWithItems

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingList: ShoppingList): Long

    @Delete
    suspend fun delete(shoppingList: ShoppingList)

    @Update
    suspend fun update(shoppingList: ShoppingList)

    @Query("DELETE FROM ShoppingList")
    suspend fun deleteAll()
}