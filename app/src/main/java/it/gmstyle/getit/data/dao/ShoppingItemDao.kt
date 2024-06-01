package it.gmstyle.getit.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import it.gmstyle.getit.data.entities.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM ShoppingItem where listId = :listId")
    fun getAllByListId(listId: Int): Flow<List<ShoppingItem>>

    @Insert
    suspend fun insert(shoppingItem: ShoppingItem): Long

    @Delete
    suspend fun delete(shoppingItem: ShoppingItem)

    @Update
    suspend fun update(shoppingItem: ShoppingItem)
}