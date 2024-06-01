package it.gmstyle.getit.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.gmstyle.getit.data.entities.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM ShoppingList")
    fun getAll(): Flow<List<ShoppingList>>

    @Query("SELECT * FROM ShoppingList WHERE id = :id")
    fun getById(id: Int): Flow<ShoppingList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingList: ShoppingList): Long

    @Delete
    suspend fun delete(shoppingList: ShoppingList)

    @Update
    suspend fun update(shoppingList: ShoppingList)

    @Query("DELETE FROM ShoppingList")
    suspend fun deleteAll()
}